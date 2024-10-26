package com.tom.morewires.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import com.tom.morewires.WireTypeDefinition;

import blusunrize.immersiveengineering.common.blocks.BlockItemIE;

public class ConnectorItemBlock extends BlockItemIE {
	private final WireTypeDefinition<?> def;
	private final Object id;

	public ConnectorItemBlock(Block b, Properties props, WireTypeDefinition<?> def, Object id) {
		super(b, props);
		this.def = def;
		this.id = id;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag advanced) {
		super.appendHoverText(stack, ctx, tooltip, advanced);
		def.appendHoverTextConnector(id, stack, ctx, tooltip, advanced);
	}
}
