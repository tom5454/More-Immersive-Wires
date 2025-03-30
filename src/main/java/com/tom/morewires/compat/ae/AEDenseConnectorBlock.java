package com.tom.morewires.compat.ae;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.registries.DeferredHolder;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;

public class AEDenseConnectorBlock extends ConnectorBlock<AEDenseConnectorBlockEntity> {
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public AEDenseConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<AEDenseConnectorBlockEntity>> entityType) {
		super(ConnectorBlock.PROPERTIES.get(), entityType);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false).setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.FACING_ALL, BlockStateProperties.WATERLOGGED, POWERED);
	}
}
