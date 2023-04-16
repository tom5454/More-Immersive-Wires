package com.tom.morewires.compat.ae;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.morewires.MoreImmersiveWires;

import appeng.block.IOwnerAwareBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;

public class AEItemBlock extends BlockItem {

	public AEItemBlock(Block id) {
		super(id, new Item.Properties().tab(MoreImmersiveWires.MOD_TAB));
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		InteractionResult result = super.place(context);
		if (!result.consumesAction()) {
			return result;
		} else {
			BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
			if (be instanceof IOwnerAwareBlockEntity oa) {
				oa.setOwner(context.getPlayer());
			}
			return result;
		}
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState newState) {
		Block b = newState.getBlock();
		if(b instanceof IEBaseBlock ieBlock) {
			if(!ieBlock.canIEBlockBePlaced(newState, context))
				return false;
			boolean ret = super.placeBlock(context, newState);
			if(ret)
				ieBlock.onIEBlockPlacedBy(context, newState);
			return ret;
		} else
			return super.placeBlock(context, newState);
	}
}
