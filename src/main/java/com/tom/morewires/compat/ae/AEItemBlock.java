package com.tom.morewires.compat.ae;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.morewires.SimpleWireTypeDefinition;

import appeng.block.IOwnerAwareBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;

public class AEItemBlock extends BlockItem {
	private final SimpleWireTypeDefinition<?> def;

	public AEItemBlock(Block id, SimpleWireTypeDefinition<?> def) {
		super(id, new Item.Properties());
		this.def = def;
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

	@Override
	public void appendHoverText(ItemStack p_40572_, TooltipContext p_40573_, List<Component> p_40574_, TooltipFlag p_40575_) {
		super.appendHoverText(p_40572_, p_40573_, p_40574_, p_40575_);
		def.appendHoverTextConnector(null, p_40572_, p_40573_, p_40574_, p_40575_);
	}
}
