package com.tom.morewires.compat.ae;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredHolder;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;

public class AEDenseConnectorBlock extends ConnectorBlock<AEDenseConnectorBlockEntity> {

	public AEDenseConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<AEDenseConnectorBlockEntity>> entityType) {
		super(ConnectorBlock.PROPERTIES.get(), entityType);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.FACING_ALL, BlockStateProperties.WATERLOGGED);
	}
}
