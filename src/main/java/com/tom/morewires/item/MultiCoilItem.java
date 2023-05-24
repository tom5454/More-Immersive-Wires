package com.tom.morewires.item;

import static blusunrize.immersiveengineering.api.wires.utils.WirecoilUtils.hasWireLink;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition.WireInfo;
import com.tom.morewires.item.MultiCoilItem.MultiWireInfo.ConnectorTypeInfo;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.IWireCoil;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.utils.WireLink;
import blusunrize.immersiveengineering.api.wires.utils.WirecoilUtils;
import blusunrize.immersiveengineering.common.items.IEBaseItem;

public class MultiCoilItem extends IEBaseItem implements IWireCoil {
	private static final String MULTI_WIRE_ID = "MultiWireId";
	private final MultiWireInfo def;

	public MultiCoilItem(MultiWireInfo def) {
		super(new Properties(), MoreImmersiveWires.MOD_TAB);
		this.def = def;
	}

	@Override
	public WireType getWireType(ItemStack stack) {
		String id = null;
		if(stack.hasTag())id = stack.getTag().getString(MULTI_WIRE_ID);
		return def.getWireTypeById(id);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		if(hasWireLink(stack)) {
			WireLink link = WireLink.readFromItem(stack);
			list.add(Component.translatable(Lib.DESC_INFO+"attachedToDim", link.cp.getX(),
					link.cp.getY(), link.cp.getZ(), link.dimension));
			list.add(Component.translatable("tooltip.more_immersive_wires.network_type", Component.translatable("tooltip.more_immersive_wires.network_type." + stack.getTag().getString(MULTI_WIRE_ID))));
		}
		def.appendHoverTextCoil(stack, world, list, flag);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		BlockEntity tileEntity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
		if(tileEntity instanceof IImmersiveConnectable ic && ic.canConnect()) {
			Direction side = ctx.getClickedFace();
			BlockPos pos = ctx.getClickedPos();
			float hitX = (float) ctx.getClickLocation().x;
			float hitY = (float) ctx.getClickLocation().y;
			float hitZ = (float) ctx.getClickLocation().z;
			TargetingInfo targetHere = new TargetingInfo(side, hitX-pos.getX(), hitY-pos.getY(), hitZ-pos.getZ());
			BlockPos masterPos = ic.getConnectionMaster(def.getDefWire(), targetHere);
			BlockPos masterOffsetHere = pos.subtract(masterPos);
			tileEntity = ctx.getLevel().getBlockEntity(masterPos);
			if(!(tileEntity instanceof IImmersiveConnectable iicHere) || !iicHere.canConnect())
				return InteractionResult.PASS;
			ConnectionPoint cpHere = iicHere.getTargetedPoint(targetHere, masterOffsetHere);

			String id = def.getTypeId(ctx.getLevel(), new ConnectorTypeInfo(ctx.getLevel().getBlockState(cpHere.position()).getBlock(), cpHere.index()));
			CompoundTag tag = ctx.getItemInHand().getOrCreateTag();
			if(tag.contains(MULTI_WIRE_ID) && hasWireLink(ctx.getItemInHand())) {
				String c = tag.getString(MULTI_WIRE_ID);
				if (!c.equals(id)) {
					if(!ctx.getLevel().isClientSide)
						ctx.getPlayer().displayClientMessage(Component.translatable(Lib.CHAT_WARN + "wrongCable"), true);
					return InteractionResult.FAIL;
				}
			} else {
				tag.putString(MULTI_WIRE_ID, id);
			}
		}
		InteractionResult ir = WirecoilUtils.doCoilUse(this, ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getHand(), ctx.getClickedFace(),
				(float)ctx.getClickLocation().x, (float)ctx.getClickLocation().y, (float)ctx.getClickLocation().z);
		if(!hasWireLink(ctx.getItemInHand())) {
			CompoundTag tag = ctx.getItemInHand().getTag();
			if(tag != null) {
				tag.remove(MULTI_WIRE_ID);
				if(tag.isEmpty())
					ctx.getItemInHand().setTag(null);
			}
		}
		return ir;
	}

	public static interface MultiWireInfo extends WireInfo {
		WireType getWireTypeById(String id);
		WireType getDefWire();
		String getTypeId(Level world, ConnectorTypeInfo cpHere);
		void appendHoverTextCoil(ItemStack stack, Level world, List<Component> list, TooltipFlag flag);

		public static record ConnectorTypeInfo(Block block, int ind) {}
	}
}