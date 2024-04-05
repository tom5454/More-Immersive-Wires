package com.tom.morewires.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.tom.morewires.tile.IConnector;

import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.BasicConnectorBlock;

public class RelayBlock<T extends BlockEntity & IImmersiveConnectable> extends BasicConnectorBlock<T> {

	public RelayBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> type) {
		super(ConnectorBlock.PROPERTIES.get(), type);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof IConnector connector) {
			if(world.isEmptyBlock(pos.relative(connector.getFacing()))) {
				popResource(world, pos, new ItemStack(this));
				connector.getLevelNonnull().removeBlock(pos, false);
			}
		}
	}
}
