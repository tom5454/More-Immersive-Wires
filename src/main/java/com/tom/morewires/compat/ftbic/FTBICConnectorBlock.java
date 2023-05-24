package com.tom.morewires.compat.ftbic;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.block.OnCableConnectorBlock;

import blusunrize.immersiveengineering.api.IEProperties;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;

public class FTBICConnectorBlock extends OnCableConnectorBlock<FTBICConnectorBlockEntity> {

	public FTBICConnectorBlock(RegistryObject<BlockEntityType<FTBICConnectorBlockEntity>> type, BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(type, isOnCable);
	}

	@Override
	@Deprecated
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onPlace(state, level, pos, state1, b);
		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}

	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onRemove(state, level, pos, state1, b);
		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}

	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
			BlockPos pos, BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		if (!level.isClientSide() && state.getValue(IEProperties.FACING_ALL) == facing) {
			ElectricBlockEntity.electricNetworkUpdated(level, facingPos);
		}

		return state;
	}
}
