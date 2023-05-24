package com.tom.morewires.compat.cc;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IConnector;

import dan200.computercraft.api.network.wired.IWiredElement;
import dan200.computercraft.api.network.wired.IWiredNode;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.command.text.ChatHelpers;
import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import dan200.computercraft.shared.peripheral.modem.wired.BlockWiredModemFull;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemElement;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemLocalPeripheral;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemPeripheral;
import dan200.computercraft.shared.util.CapabilityUtil;
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

	private boolean invalidPeripheral;
	private boolean peripheralAccessAllowed;
	private final WiredModemLocalPeripheral peripheral = new WiredModemLocalPeripheral(this::queueRefreshPeripheral);

	private boolean destroyed = false;

	private boolean connectionsFormed = false;

	private final WiredModemElement cable = new CableElement();
	private LazyOptional<IWiredElement> elementCap;
	private final IWiredNode node = cable.getNode();
	private final TickScheduler.Token tickToken = new TickScheduler.Token(this);
	private final WiredModemPeripheral modem = new WiredModemPeripheral(
			new ModemState(() -> TickScheduler.schedule(tickToken)), cable) {
		@Override
		protected WiredModemLocalPeripheral getLocalPeripheral() {
			return peripheral;
		}

		@Override
		public Vec3 getPosition() {
			return Vec3.atCenterOf(getBlockPos().relative(getFacing()));
		}

		@Override
		public Object getTarget() {
			return CCModemConnectorBlockEntity.this;
		}
	};
	private LazyOptional<IPeripheral> modemCap;

	//private final NonNullConsumer<LazyOptional<IWiredElement>> connectedNodeChanged = x -> connectionsChanged();

	private void onRemove() {
		if (level == null || !level.isClientSide) {
			node.remove();
			connectionsFormed = false;
		}
	}

	@Override
	public void destroy() {
		if (!destroyed) {
			destroyed = true;
			modem.destroy();
			onRemove();
		}
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		elementCap = CapabilityUtil.invalidate(elementCap);
		modemCap = CapabilityUtil.invalidate(modemCap);
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved(); // TODO: Replace with onLoad
		TickScheduler.schedule(tickToken);
	}

	@Override
	public void onNeighbourChange(BlockPos neighbour) {
		Direction dir = getFacing();
		if (neighbour.equals(getBlockPos().relative(dir)) && !getBlockState().canSurvive(getLevel(), getBlockPos())) {
			// Drop everything and remove block
			Block.popResource(getLevel(), getBlockPos(), new ItemStack(Registry.ModItems.WIRED_MODEM.get()));
			getLevel().removeBlock(getBlockPos(), false);
			// This'll call #destroy(), so we don't need to reset the
			// network here.

			return;
		}

		onNeighbourTileEntityChange(neighbour);
	}

	@Override
	public void onNeighbourTileEntityChange(BlockPos neighbour) {
		super.onNeighbourTileEntityChange(neighbour);
		if (!level.isClientSide && peripheralAccessAllowed) {
			Direction facing = getFacing();
			if (getBlockPos().relative(facing).equals(neighbour)) queueRefreshPeripheral();
		}
	}

	private void queueRefreshPeripheral() {
		if (invalidPeripheral) return;
		invalidPeripheral = true;
		TickScheduler.schedule(tickToken);
	}

	private void refreshPeripheral() {
		invalidPeripheral = false;
		if (level != null && !isRemoved() && peripheral.attach(level, getBlockPos(), getFacing())) {
			updateConnectedPeripherals();
		}
	}

	@Override
	public InteractionResult onActivate(Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.isCrouching() || !player.mayBuild()) return InteractionResult.PASS;
		if (!canAttachPeripheral()) return InteractionResult.FAIL;

		if (getLevel().isClientSide) return InteractionResult.SUCCESS;

		String oldName = peripheral.getConnectedName();
		togglePeripheralAccess();
		String newName = peripheral.getConnectedName();
		if (!Objects.equal(newName, oldName)) {
			if (oldName != null) {
				player.displayClientMessage(Component.translatable(
						"chat.computercraft.wired_modem.peripheral_disconnected", ChatHelpers.copy(oldName)), false);
			}
			if (newName != null) {
				player.displayClientMessage(Component.translatable(
						"chat.computercraft.wired_modem.peripheral_connected", ChatHelpers.copy(newName)), false);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		peripheralAccessAllowed = nbt.getBoolean(NBT_PERIPHERAL_ENABLED);
		peripheral.read(nbt, "");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		nbt.putBoolean(NBT_PERIPHERAL_ENABLED, peripheralAccessAllowed);
		peripheral.write(nbt, "");
		super.saveAdditional(nbt);
	}

	private void updateBlockState() {
		BlockState state = getBlockState();
		boolean modemOn = modem.getModemState().isOpen(), peripheralOn = peripheralAccessAllowed;
		if (state.getValue(BlockWiredModemFull.MODEM_ON) == modemOn
				&& state.getValue(BlockWiredModemFull.PERIPHERAL_ON) == peripheralOn)
			return;

		getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(BlockWiredModemFull.MODEM_ON, modemOn)
				.setValue(BlockWiredModemFull.PERIPHERAL_ON, peripheralOn));
	}

	@Override
	public void blockTick() {
		if (getLevel().isClientSide) return;

		if (invalidPeripheral) refreshPeripheral();

		if (modem.getModemState().pollChanged()) updateBlockState();

		if (!connectionsFormed) {
			connectionsFormed = true;

			//connectionsChanged();
			if (peripheralAccessAllowed) {
				peripheral.attach(level, worldPosition, getFacing());
				updateConnectedPeripherals();
			}
		}
	}

	/*void connectionsChanged() {
		if (getLevel().isClientSide) return;

		BlockState state = getBlockState();
		Level world = getLevel();
		BlockPos current = getBlockPos();
		for (Direction facing : DirectionUtil.FACINGS) {
			BlockPos offset = current.relative(facing);
			if (!world.isLoaded(offset)) continue;

			LazyOptional<IWiredElement> element = ComputerCraftAPI.getWiredElementAt(world, offset,
					facing.getOpposite());
			if (!element.isPresent()) continue;

			element.addListener(connectedNodeChanged);
			IWiredNode node = element.orElseThrow(NullPointerException::new).getNode();
			if (BlockCable.canConnectIn(state, facing)) {
				// If we can connect to it then do so
				this.node.connectTo(node);
			} else if (this.node.getNetwork() == node.getNetwork()) {
				// Otherwise if we're on the same network then attempt to void
				// it.
				this.node.disconnectFrom(node);
			}
		}
	}*/

	void modemChanged() {
		// Tell anyone who cares that the connection state has changed
		elementCap = CapabilityUtil.invalidate(elementCap);

		if (getLevel().isClientSide) return;

		// If we can no longer attach peripherals, then detach any
		// which may have existed
		if (!canAttachPeripheral() && peripheralAccessAllowed) {
			peripheralAccessAllowed = false;
			peripheral.detach();
			node.updatePeripherals(Collections.emptyMap());
			setChanged();
			updateBlockState();
		}
	}

	private void togglePeripheralAccess() {
		if (!peripheralAccessAllowed) {
			peripheral.attach(level, getBlockPos(), getFacing());
			if (!peripheral.hasPeripheral()) return;

			peripheralAccessAllowed = true;
			node.updatePeripherals(peripheral.toMap());
		} else {
			peripheral.detach();

			peripheralAccessAllowed = false;
			node.updatePeripherals(Collections.emptyMap());
		}

		updateBlockState();
	}

	private void updateConnectedPeripherals() {
		Map<String, IPeripheral> peripherals = peripheral.toMap();
		if (peripherals.isEmpty()) {
			// If there are no peripherals then disable access and update the
			// display state.
			peripheralAccessAllowed = false;
			updateBlockState();
		}

		node.updatePeripherals(peripherals);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
		/*if (capability == CAPABILITY_WIRED_ELEMENT) {
			if (destroyed || !BlockCable.canConnectIn(getBlockState(), side)) return LazyOptional.empty();
			if (elementCap == null) elementCap = LazyOptional.of(() -> cable);
			return elementCap.cast();
		}*/

		if (capability == CAPABILITY_PERIPHERAL) {
			if (side != null && getFacing() != side) return LazyOptional.empty();
			if (modemCap == null) modemCap = LazyOptional.of(() -> modem);
			return modemCap.cast();
		}

		return super.getCapability(capability, side);
	}

	private boolean canAttachPeripheral() {
		return true;
	}

	@Override
	public IWiredElement getElement() {
		return cable;
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(MoreImmersiveWires.CC_WIRE.simple().NET_ID);
	}
}
