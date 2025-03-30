package com.tom.morewires.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import com.tom.morewires.WireTypeDefinition;

import blusunrize.immersiveengineering.api.IEApiDataComponents;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.wires.IWireCoil;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.utils.WireLink;
import blusunrize.immersiveengineering.api.wires.utils.WirecoilUtils;
import blusunrize.immersiveengineering.common.items.IEBaseItem;

public class WireCoilItem extends IEBaseItem implements IWireCoil {
	@Nonnull
	private final WireType type;
	private final WireTypeDefinition<?> def;

	public WireCoilItem(@Nonnull WireType type, WireTypeDefinition<?> def) {
		super(new Properties());
		this.type = type;
		this.def = def;
	}

	@Override
	public WireType getWireType(ItemStack stack) {
		return type;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext world, List<Component> list, TooltipFlag flag) {
		WireLink link = stack.get(IEApiDataComponents.WIRE_LINK.get());
		if (link != null) {
			list.add(Component.translatable(Lib.DESC_INFO+"attachedToDim", link.cp().getX(),
					link.cp().getY(), link.cp().getZ(), link.dimension().location().toString()));
		}
		def.appendHoverTextCoil(type, stack, world, list, flag);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		return WirecoilUtils.doCoilUse(this, ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getHand(), ctx.getClickedFace(),
				(float)ctx.getClickLocation().x, (float)ctx.getClickLocation().y, (float)ctx.getClickLocation().z);
	}
}