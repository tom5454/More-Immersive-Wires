package com.tom.morewires.tile;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import com.google.common.collect.ImmutableList;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IStateBasedDirectional;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;

public interface IConnector extends IStateBasedDirectional, IImmersiveConnectable {
	Level getLevelNonnull();

	@Override
	public default Property<Direction> getFacingProperty() {
		return ConnectorBlock.DEFAULT_FACING_PROP;
	}

	@Override
	public default PlacementLimitation getFacingLimitation() {
		return PlacementLimitation.SIDE_CLICKED;
	}

	@Override
	public default boolean mirrorFacingOnPlacement(LivingEntity placer) {
		return true;
	}

	@Override
	public default boolean canHammerRotate(Direction side, Vec3 hit, LivingEntity entity) {
		return false;
	}

	@Override
	default void setState(BlockState state) {
		if(getLevelNonnull().getBlockState(getPosition())==getState())
			getLevelNonnull().setBlockAndUpdate(getPosition(), state);
	}

	@Override
	public default Collection<ConnectionPoint> getConnectionPoints() {
		return ImmutableList.of(new ConnectionPoint(getPosition(), 0));
	}

	@Override
	public default BlockPos getConnectionMaster(WireType cableType, TargetingInfo target) {
		return getPosition();
	}

	@Override
	public default ConnectionPoint getTargetedPoint(TargetingInfo info, Vec3i offset) {
		return new ConnectionPoint(getPosition(), 0);
	}
}
