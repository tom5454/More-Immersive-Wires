package com.tom.morewires.compat.rs;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.refinedmods.refinedstorage.block.CableBlock;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition;

import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public class RSWireDefinition implements WireTypeDefinition<RSConnectorBlockEntity> {
	public static final ResourceLocation NET_ID = new ResourceLocation(MoreImmersiveWires.modid, "rs_network");

	@Override
	public RSConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new RSConnectorBlockEntity(MoreImmersiveWires.RS_WIRE.CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof CableBlock;
	}

	@Override
	public void init() {
		LocalNetworkHandler.register(NET_ID, RSNetworkHandler::new);
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(NET_ID);
	}

	@Override
	public Block makeBlock0(RegistryObject<BlockEntityType<RSConnectorBlockEntity>> type) {
		return new RSConnectorBlock(type, this::isCable);
	}
}
