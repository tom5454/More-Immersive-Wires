package com.tom.morewires.compat.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CCBlockEntity extends BlockEntity {

	public CCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void blockTick() {
	}

	public void destroy() {
	}

	public InteractionResult onActivate(Player player) {
		return InteractionResult.PASS;
	}

	public void onNeighbourTileEntityChange(BlockPos neighbour) {
	}

	public void onNeighbourChange(BlockPos neighbourPos) {
	}
}
