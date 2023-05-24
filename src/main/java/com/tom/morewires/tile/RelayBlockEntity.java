package com.tom.morewires.tile;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition.RelayInfo;

import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.ImmersiveConnectableBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;

public class RelayBlockEntity extends ImmersiveConnectableBlockEntity implements IConnector, IBlockBounds {
	private static final float LENGTH = 0.5625F;
	private static final float LENGTH_TALL = 0.75F;
	private static final float LENGTH_EX_TALL = 0.875F;

	public RelayBlockEntity(BlockEntityType<RelayBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public RelayInfo getWire() {
		return MoreImmersiveWires.WIRE_TYPES.get(getType());
	}

	@Override
	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) {
		return getWire().isMatchingWireType(cableType);
	}

	@Override
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) {
		Direction side = getFacing().getOpposite();

		double lengthFromHalf = getHeight() - type.getRenderDiameter()/2-.5;
		return new Vec3(.5+lengthFromHalf*side.getStepX(),
				.5+lengthFromHalf*side.getStepY(),
				.5+lengthFromHalf*side.getStepZ());
	}

	protected float getHeight() {
		RelayInfo d = getWire();
		return d.isExTallRelay() ? LENGTH_EX_TALL : d.isTallRelay() ? LENGTH_TALL : LENGTH;
	}

	@Override
	public VoxelShape getBlockBounds(@Nullable CollisionContext ctx) {
		return EnergyConnectorBlockEntity.getConnectorBounds(getFacing(), getHeight());
	}
}
