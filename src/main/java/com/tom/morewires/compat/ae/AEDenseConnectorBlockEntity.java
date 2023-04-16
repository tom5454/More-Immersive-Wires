package com.tom.morewires.compat.ae;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.tile.IConnector;

import appeng.api.networking.GridFlags;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkBlockEntity;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;

public class AEDenseConnectorBlockEntity extends AENetworkBlockEntity implements IConnector, IBlockBounds {
	protected GlobalWireNetwork globalNet;

	static {
		registerBlockEntityItem(MoreImmersiveWires.AE_DENSE_WIRE.CONNECTOR_ENTITY.get(), MoreImmersiveWires.AE_DENSE_WIRE.CONNECTOR.get().asItem());
	}

	public AEDenseConnectorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
		super(blockEntityType, pos, blockState);
		this.getMainNode().setIdlePowerUsage(0.0D);
		this.getMainNode().setFlags(GridFlags.DENSE_CAPACITY);
		this.getMainNode().setExposedOnSides(EnumSet.noneOf(Direction.class));
	}

	@Override
	public AECableType getCableConnectionType(Direction dir) {
		if(getFacing() == dir)return AECableType.DENSE_SMART;
		else return AECableType.NONE;
	}

	@Override
	public void setOrientation(Direction inForward, Direction inUp) {
		super.setOrientation(inForward, inUp);
		this.getMainNode().setExposedOnSides(EnumSet.of(getFacing()));
	}

	@Override
	public void onReady() {
		this.getMainNode().setExposedOnSides(EnumSet.of(getFacing()));
		super.onReady();
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
		return cableType == MoreImmersiveWires.AE_DENSE_WIRE.wireType;
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
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) {
		Direction side = getFacing().getOpposite();

		double lengthFromHalf = 1F - type.getRenderDiameter()/2-.5;
		return new Vec3(.5+lengthFromHalf*side.getStepX(),
				.5+lengthFromHalf*side.getStepY(),
				.5+lengthFromHalf*side.getStepZ());
	}

	@Override
	public VoxelShape getBlockBounds(@Nullable CollisionContext ctx) {
		return EnergyConnectorBlockEntity.getConnectorBounds(getFacing(), 1F);
	}
}
