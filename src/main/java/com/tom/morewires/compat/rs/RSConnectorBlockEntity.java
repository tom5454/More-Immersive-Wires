package com.tom.morewires.compat.rs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor.Operator;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IOnCableConnector;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;

public class RSConnectorBlockEntity extends NetworkNodeBlockEntity<ConnectorNetworkNode> implements IOnCableConnector {
	public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder().build();
	protected GlobalWireNetwork globalNet;

	protected RSConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, SPEC);
	}

	@Override
	public ConnectorNetworkNode createNode(Level var1, BlockPos var2) {
		return new ConnectorNetworkNode(var1, var2);
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
		return cableType == MoreImmersiveWires.RS_WIRE.wireType;
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

	public void setNetworkHandler(RSNetworkHandler handler) {
		if(!remove)
			getNode().setNetworkHandler(handler);
	}

	public void visit(Operator operator) {
		operator.apply(level, worldPosition, null);
	}
}
