package com.tom.morewires.compat.ic2;

import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.block.OnCableConnectorBlock;

public class IC2ConnectorBlock extends OnCableConnectorBlock<IC2ConnectorBlockEntity> {
	public static final BooleanProperty OUTPUT = BooleanProperty.create("output");

	public IC2ConnectorBlock(RegistryObject<BlockEntityType<IC2ConnectorBlockEntity>> type,
			BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(type, isOnCable);
		registerDefaultState(defaultBlockState().setValue(OUTPUT, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(OUTPUT);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof IC2ConnectorBlockEntity connector) {
			if(world.isEmptyBlock(pos.relative(connector.getFacing()))) {
				popResource(world, pos, new ItemStack(this));
				connector.getLevel().removeBlock(pos, false);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.more_immersive_wires.ic2.mode_info").withStyle(ChatFormatting.GRAY));
	}
}
