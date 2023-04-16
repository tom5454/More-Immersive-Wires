package com.tom.morewires.compat.ae;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition;

import appeng.core.definitions.AEBlocks;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public class AEWireDefinition implements WireTypeDefinition<AEConnectorBlockEntity> {
	public static final ResourceLocation NET_ID = new ResourceLocation(MoreImmersiveWires.modid, "ae_network");

	@Override
	public AEConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new AEConnectorBlockEntity(MoreImmersiveWires.AE_WIRE.CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public Item makeItemBlock(Block block) {
		return new AEItemBlock(block);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(AEBlocks.CABLE_BUS.block());
	}

	@Override
	public void init() {
		LocalNetworkHandler.register(NET_ID, AENetworkHandler::new);
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(NET_ID);
	}
}
