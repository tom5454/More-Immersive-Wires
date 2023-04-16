package com.tom.morewires.compat.cc;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition;

import dan200.computercraft.shared.Registry;

import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public class CCWireDefinition implements WireTypeDefinition<CCConnectorBlockEntity> {
	public static final ResourceLocation NET_ID = new ResourceLocation(MoreImmersiveWires.modid, "cc_network");

	@Override
	public CCConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new CCConnectorBlockEntity(MoreImmersiveWires.CC_WIRE.CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).is(Registry.ModBlocks.CABLE.get());
	}

	@Override
	public void init() {
		LocalNetworkHandler.register(NET_ID, CCNetworkHandler::new);
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(NET_ID);
	}

	@Override
	public Block makeBlock0(RegistryObject<BlockEntityType<CCConnectorBlockEntity>> type) {
		return new CCConnectorBlock(type, this::isCable);
	}
}
