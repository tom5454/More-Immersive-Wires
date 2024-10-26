package com.tom.morewires.compat.cc;

import java.util.Collection;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IConnector;

import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.network.wired.WiredNode;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.command.text.ChatHelpers;
import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemElement;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemLocalPeripheral;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemPeripheral;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.util.TickScheduler;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;

public class CCModemConnectorBlockEntity extends CCBlockEntity implements IConnector, IBlockBounds, ICCTile {
	protected GlobalWireNetwork globalNet;

	public CCModemConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public BlockState getState() {
		return getBlockState();
	}

	@Override
	public boolean canConnect() {
		return true;
	}

	@Override
	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) {
		LocalWireNetwork local = this.globalNet.getNullableLocalNet(new ConnectionPoint(this.worldPosition, 0));
		if (local != null && !local.getConnections(this.worldPosition).isEmpty()) {
			return false;
		}
		return MoreImmersiveWires.CC_WIRE.simple().wireType == cableType;
	}

	@Override
	public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other,
			ConnectionPoint otherTarget) {

	}

	@Override
	public void setLevel(Level worldIn) {
		super.setLevel(worldIn);
		globalNet = GlobalWireNetwork.getNetwork(worldIn);
	}

	@Override
	public void removeCable(Connection connection, ConnectionPoint attachedPoint) {
		this.setChanged();
	}

	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}

	private boolean isUnloaded = false;

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		ConnectorBlockEntityHelper.onChunkUnload(globalNet, this);
		isUnloaded = true;
		onRemove();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ConnectorBlockEntityHelper.onChunkLoad(this, level);
		isUnloaded = false;
	}

	public void setRemovedIE() {
		ConnectorBlockEntityHelper.remove(level, this);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if(!isUnloaded)
			setRemovedIE();
		onRemove();
	}

	@Override
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) {
		Direction side = getFacing().getOpposite();

		double lengthFromHalf = .8125F - type.getRenderDiameter()/2-.5;
		return new Vec3(.5+lengthFromHalf*side.getStepX(),
				.5+lengthFromHalf*side.getStepY(),
				.5+lengthFromHalf*side.getStepZ());
	}

	@Override
	public VoxelShape getBlockBounds(CollisionContext ctx) {
		return Shapes.or(EnergyConnectorBlockEntity.getConnectorBounds(getFacing(), .8125F), ModemShapes.getBounds(getFacing()));
	}

	@Override
	public Level getLevelNonnull() {
		return level;
	}

	private static final String NBT_PERIPHERAL_ENABLED = "PeirpheralAccess";

	private class CableElement extends WiredModemElement {
		@Override
		public Level getLevel() {
			return CCModemConnectorBlockEntity.this.getLevel();
		}

		@Override
		public Vec3 getPosition() {
			return Vec3.atCenterOf(getBlockPos());
		}

		@Override
		protected void attachPeripheral(String name, IPeripheral peripheral) {
			modem.attachPeripheral(name, peripheral);
		}

		@Override
		protected void detachPeripheral(String name) {
			modem.detachPeripheral(name);
		}
	}

	private boolean peripheralAccessAllowed;
	private boolean destroyed = false;

	private boolean refreshPeripheral;
	private final WiredModemLocalPeripheral peripheral = new WiredModemLocalPeripheral(PlatformHelper.get().createPeripheralAccess(this, x -> queueRefreshPeripheral()));

	private boolean refreshConnections = false;

	private final WiredModemElement cable = new CableElement();
	private final WiredNode node = cable.getNode();
	private final TickScheduler.Token tickToken = new TickScheduler.Token(this);
	private final WiredModemPeripheral modem = new WiredModemPeripheral(
			new ModemState(() -> TickScheduler.schedule(tickToken)), cable, peripheral, this
			) {
		@Override
		public Vec3 getPosition() {
			var dir = getModemDirection();
			return Vec3.atCenterOf(dir == null ? getBlockPos() : getBlockPos().relative(dir));
		}
	};

	private void onRemove() {
		modem.removed();
		if (level == null || !level.isClientSide) node.remove();
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();
		refreshConnections = refreshPeripheral = true;
		TickScheduler.schedule(tickToken);
	}

	@Override
	@Deprecated
	public void setBlockState(BlockState state) {
		var direction = getModemDirection();
		var hasCable = hasCable();
		super.setBlockState(state);

		// We invalidate both the modem and element if the modem direction or cable are different.
		if (hasCable() != hasCable || getModemDirection() != direction) PlatformHelper.get().invalidateComponent(this);
	}

	private Direction getModemDirection() {
		return getFacing();
	}

	void neighborChanged(BlockPos neighbour) {
		var dir = getModemDirection();
		if (!level.isClientSide && dir != null && getBlockPos().relative(dir).equals(neighbour) && isPeripheralOn()) {
			queueRefreshPeripheral();
		}
	}

	void queueRefreshPeripheral() {
		refreshPeripheral = true;
		TickScheduler.schedule(tickToken);
	}

	public InteractionResult use(Player player) {
		if (!canAttachPeripheral()) return InteractionResult.FAIL;

		if (getLevel().isClientSide) return InteractionResult.SUCCESS;

		var oldName = peripheral.getConnectedName();
		if (isPeripheralOn()) {
			detachPeripheral();
		} else {
			attachPeripheral();
		}
		var newName = peripheral.getConnectedName();

		if (!Objects.equals(newName, oldName)) {
			if (oldName != null) {
				player.displayClientMessage(Component.translatable("chat.computercraft.wired_modem.peripheral_disconnected",
						ChatHelpers.copy(oldName)), false);
			}
			if (newName != null) {
				player.displayClientMessage(Component.translatable("chat.computercraft.wired_modem.peripheral_connected",
						ChatHelpers.copy(newName)), false);
			}
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, Provider pr) {
		super.loadAdditional(nbt, pr);
		peripheral.read(nbt, "");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, Provider pr) {
		peripheral.write(nbt, "");
		super.saveAdditional(nbt, pr);
	}

	private void updateBlockState() {
		BlockState state = getBlockState();
		boolean modemOn = modem.getModemState().isOpen(), peripheralOn = this.peripheral.hasPeripheral();
		if (state.getValue(WiredModemFullBlock.MODEM_ON) == modemOn
				&& state.getValue(WiredModemFullBlock.PERIPHERAL_ON) == peripheralOn)
			return;

		getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(WiredModemFullBlock.MODEM_ON, modemOn)
				.setValue(WiredModemFullBlock.PERIPHERAL_ON, peripheralOn));
	}

	@Override
	public void blockTick() {
		if (getLevel().isClientSide) return;

		if (refreshPeripheral) {
			refreshPeripheral = false;
			if (isPeripheralOn()) attachPeripheral();
		}

		if (modem.getModemState().pollChanged()) updateBlockState();

		refreshConnections = false;
		//if (refreshConnections) connectionsChanged();
	}

	void scheduleConnectionsChanged() {
		refreshConnections = true;
		TickScheduler.schedule(tickToken);
	}

	/*void connectionsChanged() {
		if (getLevel().isClientSide) return;
		refreshConnections = false;

		var state = getBlockState();
		var world = getLevel();
		var current = getBlockPos();
		for (var facing : DirectionUtil.FACINGS) {
			var offset = current.relative(facing);
			if (!world.isLoaded(offset)) continue;

			var element = connectedElements.get(facing);
			if (element == null) continue;

			var node = element.getNode();
			if (CableBlock.canConnectIn(state, facing)) {
				// If we can connect to it then do so
				this.node.connectTo(node);
			} else {
				// Otherwise break the connection.
				this.node.disconnectFrom(node);
			}
		}

		// If we can no longer attach peripherals, then detach any which may have existed
		if (!canAttachPeripheral()) detachPeripheral();
	}*/

	private void attachPeripheral() {
		var dir = Objects.requireNonNull(getModemDirection(), "Attaching without a modem");
		if (peripheral.attach(getLevel(), getBlockPos(), dir)) updateConnectedPeripherals();
		updateBlockState();
	}

	private void detachPeripheral() {
		if (peripheral.detach()) updateConnectedPeripherals();
		updateBlockState();
	}

	private void updateConnectedPeripherals() {
		node.updatePeripherals(peripheral.toMap());
	}

	public WiredElement getWiredElement(Direction direction) {
		return direction == null || CableBlock.canConnectIn(getBlockState(), direction) ? cable : null;
	}

	public IPeripheral getPeripheral(Direction direction) {
		return direction == null || getModemDirection() == direction ? modem : null;
	}

	private boolean isPeripheralOn() {
		return getBlockState().getValue(WiredModemFullBlock.PERIPHERAL_ON);
	}

	boolean hasCable() {
		return true;
	}

	public boolean hasModem() {
		return true;
	}

	private boolean canAttachPeripheral() {
		return true;
	}

	@Override
	public WiredElement getElement() {
		return cable;
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(MoreImmersiveWires.CC_WIRE.simple().NET_ID);
	}

	public IPeripheral getPeripheralCap(Direction side) {
		return side == getFacing() ? modem : null;
	}
}
