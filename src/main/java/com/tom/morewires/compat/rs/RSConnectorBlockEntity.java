package com.tom.morewires.compat.rs;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.network.ColoredConnectionStrategy;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IOnCable.IOnCableConnector;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;

public class RSConnectorBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode> implements IOnCableConnector {
	protected GlobalWireNetwork globalNet;

	protected RSConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, new SimpleNetworkNode(0));
	}

	@Override
	protected InWorldNetworkNodeContainer createMainContainer(final SimpleNetworkNode networkNode) {
		return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(this, networkNode)
				.connectionStrategy(new ColoredConnectionStrategy(this::getBlockState,
						this.getBlockPos()) {

					@Override
					public void addOutgoingConnections(ConnectionSink sink) {
						Direction direction = getFacing();
						sink.tryConnectInSameDimension(this.origin.relative(direction), direction.getOpposite());
						LocalWireNetwork local = globalNet.getNullableLocalNet(new ConnectionPoint(worldPosition, 0));
						if (local != null) {
							RSNetworkHandler net = local.getHandler(MoreImmersiveWires.RS_WIRE.simple().NET_ID, RSNetworkHandler.class);
							if (net != null)
								net.addConnections(sink);
						}
					}

					@Override
					public boolean canAcceptIncomingConnection(Direction incomingDirection,
							BlockState connectingState) {
						return incomingDirection == getFacing();
					}
				})
				.build();
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
		return cableType == MoreImmersiveWires.RS_WIRE.simple().wireType;
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
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(MoreImmersiveWires.RS_WIRE.simple().NET_ID);
	}

	@Override
	public Component getName() {
		return MoreImmersiveWires.RS_WIRE.simple().CONNECTOR.get().getName();
	}

	public GlobalPos getGlobalPos() {
		return new GlobalPos(level.dimension(), worldPosition);
	}

	public void networkChanged() {
		this.containers.update(this.level);
	}
}
