package com.tom.morewires.compat.ae;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition;

import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public class AEDenseWireDefinition implements WireTypeDefinition<AEDenseConnectorBlockEntity> {
	public static final ResourceLocation NET_ID = new ResourceLocation(MoreImmersiveWires.modid, "ae_dense_network");

	@Override
	public AEDenseConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new AEDenseConnectorBlockEntity(MoreImmersiveWires.AE_DENSE_WIRE.CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public Item makeItemBlock(Block block) {
		return new AEItemBlock(block);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public Block makeBlock0(RegistryObject<BlockEntityType<AEDenseConnectorBlockEntity>> type) {
		return new AEDenseConnectorBlock(type);
	}

	@Override
	public void init() {
		LocalNetworkHandler.register(NET_ID, AEDenseNetworkHandler::new);
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(NET_ID);
	}
}
