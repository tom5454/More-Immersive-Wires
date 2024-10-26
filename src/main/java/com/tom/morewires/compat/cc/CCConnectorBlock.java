package com.tom.morewires.compat.cc;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.tom.morewires.block.OnCableConnectorBlock;

public class CCConnectorBlock extends OnCableConnectorBlock<CCConnectorBlockEntity> {

	public CCConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<CCConnectorBlockEntity>> type, BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(type, isOnCable);
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
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		BlockEntity tile = level.getBlockEntity(pos);
		return tile instanceof CCBlockEntity generic ? generic.onActivate(player) : InteractionResult.PASS;
	}

	@Override
	public final void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbourBlock,
			BlockPos neighbourPos, boolean isMoving) {
		super.neighborChanged(state, world, neighbourPos, neighbourBlock, pos, isMoving);
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.onNeighbourChange(neighbourPos);
	}

	@Override
	public final void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbour) {
		super.onNeighborChange(state, world, pos, neighbour);
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.onNeighbourTileEntityChange(neighbour);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof CCBlockEntity generic) generic.blockTick();
	}
}
