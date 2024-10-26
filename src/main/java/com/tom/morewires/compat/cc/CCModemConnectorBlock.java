package com.tom.morewires.compat.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.tom.morewires.MoreImmersiveWires;

import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;

public class CCModemConnectorBlock extends ConnectorBlock<CCModemConnectorBlockEntity> {

	public CCModemConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntity>> entityType) {
		super(ConnectorBlock.PROPERTIES.get(), (DeferredHolder) entityType);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.FACING_ALL, BlockStateProperties.WATERLOGGED, WiredModemFullBlock.MODEM_ON, WiredModemFullBlock.PERIPHERAL_ON);
	}

	@Override
	public final void onRemove(BlockState block, Level world, BlockPos pos, BlockState replace, boolean bool) {
		if (block.getBlock() == replace.getBlock()) return;

		BlockEntity tile = world.getBlockEntity(pos);
		super.onRemove(block, world, pos, replace, bool);
		world.removeBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.destroy();
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack arg0, BlockState arg1, Level arg2, BlockPos arg3, Player player,
			InteractionHand hand, BlockHitResult arg6) {
		if(player.getItemInHand(hand).is(MoreImmersiveWires.CC_WIRE.simple().COIL.get()))
			return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		return super.useItemOn(arg0, arg1, arg2, arg3, player, hand, arg6);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		BlockEntity tile = level.getBlockEntity(pos);
		return tile instanceof CCModemConnectorBlockEntity generic ? generic.use(player) : InteractionResult.PASS;
	}

	@Override
	public final void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbourBlock,
			BlockPos neighbourPos, boolean isMoving) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.onNeighbourChange(neighbourPos);
	}

	@Override
	public final void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbour) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.onNeighbourTileEntityChange(neighbour);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof CCBlockEntity generic) generic.blockTick();
	}
}
