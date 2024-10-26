package com.tom.morewires.compat.ae;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IOnCable.IOnCableConnector;

import appeng.api.networking.GridFlags;
import appeng.api.orientation.BlockOrientation;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;

public class AEConnectorBlockEntity extends AENetworkedBlockEntity implements IOnCableConnector {
	protected GlobalWireNetwork globalNet;

	static {
		registerBlockEntityItem(MoreImmersiveWires.AE_WIRE.simple().CONNECTOR_ENTITY.get(), MoreImmersiveWires.AE_WIRE.simple().CONNECTOR.get().asItem());
	}

	public AEConnectorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
		super(blockEntityType, pos, blockState);
		this.getMainNode().setIdlePowerUsage(0.0D);
		this.getMainNode().setFlags(new GridFlags[0]);
		this.getMainNode().setExposedOnSides(EnumSet.noneOf(Direction.class));
	}

	@Override
	public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
		return EnumSet.of(getFacing());
	}

	@Override
	public void onReady() {
		this.getMainNode().setExposedOnSides(EnumSet.of(getFacing()));
		super.onReady();
	}

	@Override
	public AECableType getCableConnectionType(Direction dir) {
		if(getFacing() == dir)return AECableType.GLASS;
		else return AECableType.NONE;
	}

	@Override
	public void setLevel(Level worldIn) {
		super.setLevel(worldIn);
		globalNet = GlobalWireNetwork.getNetwork(worldIn);
	}

	@Override
	public boolean canConnect() {
		return true;
	}

	@Override
	public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other,
			ConnectionPoint otherTarget) {
	}

	@Override
	public void removeCable(Connection connection, ConnectionPoint attachedPoint) {
		this.setChanged();
	}

	@Override
	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) {
		LocalWireNetwork local = this.globalNet.getNullableLocalNet(new ConnectionPoint(this.worldPosition, 0));
		if (local != null && !local.getConnections(this.worldPosition).isEmpty()) {
			return false;
		}
		return cableType == MoreImmersiveWires.AE_WIRE.simple().wireType;
	}

	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}

	@Override
	public BlockState getState() {
		return getBlockState();
	}

	@Override
	public Level getLevelNonnull() {
		return level;
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
		return ImmutableList.of(MoreImmersiveWires.AE_WIRE.simple().NET_ID);
	}
}
