package com.tom.morewires.compat.cc;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_WIRED_ELEMENT;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IOnCableConnector;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.network.wired.IWiredElement;
import dan200.computercraft.api.network.wired.IWiredNode;
import dan200.computercraft.shared.util.CapabilityUtil;
import dan200.computercraft.shared.util.TickScheduler;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;

public class CCConnectorBlockEntity extends CCBlockEntity implements IOnCableConnector, ICCTile {
	protected GlobalWireNetwork globalNet;
	private boolean destroyed = false;
	private boolean connectionsFormed = false;
	private final IWiredElement cable = new CableElement();
	private LazyOptional<IWiredElement> elementCap;
	private final IWiredNode node = cable.getNode();
	private final TickScheduler.Token tickToken = new TickScheduler.Token(this);

	private class CableElement implements IWiredElement {
		private final IWiredNode node = ComputerCraftAPI.createWiredNodeForElement(this);

		@Override
		public Level getLevel() {
			return CCConnectorBlockEntity.this.getLevel();
		}

		@Override
		public Vec3 getPosition() {
			return Vec3.atCenterOf(getBlockPos());
		}

		@Override
		public IWiredNode getNode() {
			return node;
		}

		@Override
		public String getSenderID() {
			return "miw_connector";
		}
	}

	private final NonNullConsumer<LazyOptional<IWiredElement>> connectedNodeChanged = x -> connectionsChanged();

	public CCConnectorBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
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
		return cableType == MoreImmersiveWires.CC_WIRE.simple().wireType;
	}

	@Override
	public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other,
			ConnectionPoint otherTarget) {
	}

	@Override
	public void removeCable(Connection connection, ConnectionPoint attachedPoint) {
		setChanged();
	}

	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}

	@Override
	public Level getLevelNonnull() {
		return level;
	}

	@Override
	public void setLevel(Level worldIn) {
		super.setLevel(worldIn);
		globalNet = GlobalWireNetwork.getNetwork(worldIn);
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
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CAPABILITY_WIRED_ELEMENT) {
			if(destroyed || side != getFacing()) return LazyOptional.empty();
			if (elementCap == null) elementCap = LazyOptional.of(() -> cable);
			return elementCap.cast();
		}
		return super.getCapability(cap, side);
	}

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
			onRemove();
		}
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		elementCap = CapabilityUtil.invalidate(elementCap);
	}

	void connectionsChanged() {
		if (getLevel().isClientSide) return;

		Level world = getLevel();
		BlockPos current = getBlockPos();
		Direction facing = getFacing();
		BlockPos offset = current.relative(facing);
		if (!world.isLoaded(offset))return;

		LazyOptional<IWiredElement> element = ComputerCraftAPI.getWiredElementAt(world, offset,
				facing.getOpposite());
		if (!element.isPresent())return;

		element.addListener(connectedNodeChanged);
		IWiredNode node = element.orElseThrow(NullPointerException::new).getNode();
		this.node.connectTo(node);
	}

	@Override
	public void blockTick() {
		if (getLevel().isClientSide) return;

		if (!connectionsFormed) {
			connectionsFormed = true;

			connectionsChanged();
		}
	}

	@Override
	public IWiredElement getElement() {
		return cable;
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved(); // TODO: Replace with onLoad
		TickScheduler.schedule(tickToken);
	}

	@Override
	public void onNeighbourTileEntityChange(BlockPos neighbour) {
		super.onNeighbourTileEntityChange(neighbour);
		if (!level.isClientSide) {
			TickScheduler.schedule(tickToken);
		}
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(MoreImmersiveWires.CC_WIRE.simple().NET_ID);
	}
}
