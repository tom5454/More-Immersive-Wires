package com.tom.morewires;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.block.OnCableConnectorBlock;

import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerProvider;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;

public interface WireTypeDefinition<T extends BlockEntity & IImmersiveConnectable> extends ILocalHandlerProvider {
	T createBE(BlockPos pos, BlockState state);
	boolean isCable(BlockGetter level, BlockPos pos);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	default Block makeBlock(RegistryObject<BlockEntityType<?>> type) {
		return makeBlock0((RegistryObject) type);
	}

	default Block makeBlock0(RegistryObject<BlockEntityType<T>> type) {
		return new OnCableConnectorBlock<>(type, this::isCable);
	}

	default Item makeItemBlock(Block block) {
		return new BlockItemIE(block, new Item.Properties().tab(MoreImmersiveWires.MOD_TAB));
	}

	default void init() {}
}
