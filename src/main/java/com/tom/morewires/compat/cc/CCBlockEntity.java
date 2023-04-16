package com.tom.morewires.compat.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import dan200.computercraft.shared.common.TileGeneric;

public class CCBlockEntity extends TileGeneric {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super((BlockEntityType) type, pos, state);
	}

	@Override
	public void blockTick() {
		super.blockTick();
	}
}
