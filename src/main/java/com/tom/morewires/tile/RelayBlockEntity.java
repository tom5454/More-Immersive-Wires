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
import com.tom.morewires.MoreImmersiveWires.Wire;

import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.ImmersiveConnectableBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;

public class RelayBlockEntity extends ImmersiveConnectableBlockEntity implements IConnector, IBlockBounds {
	private static final float LENGTH = 0.5625F;
	private static final float LENGTH_TALL = 0.75F;

	public RelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public RelayBlockEntity(Wire type, BlockPos pos, BlockState state) {
		super(type.ENTITY.get(), pos, state);
	}

	public Wire getWire() {
		return MoreImmersiveWires.WIRE_TYPES.get(getType());
	}

	@Override
	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) {
		return cableType == getWire().wireType;
	}

	@Override
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) {
		Direction side = getFacing().getOpposite();

		double lengthFromHalf = (getWire().tall ? LENGTH_TALL : LENGTH) - type.getRenderDiameter()/2-.5;
		return new Vec3(.5+lengthFromHalf*side.getStepX(),
				.5+lengthFromHalf*side.getStepY(),
				.5+lengthFromHalf*side.getStepZ());
	}

	@Override
	public VoxelShape getBlockBounds(@Nullable CollisionContext ctx) {
		return EnergyConnectorBlockEntity.getConnectorBounds(getFacing(), getWire().tall ? LENGTH_TALL : LENGTH);
	}
}
