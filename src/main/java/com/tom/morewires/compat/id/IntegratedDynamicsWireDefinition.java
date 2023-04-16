package com.tom.morewires.compat.id;

import java.util.Collection;

import org.cyclops.integrateddynamics.RegistryEntries;

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

import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public class IntegratedDynamicsWireDefinition implements WireTypeDefinition<IDConnectorBlockEntity> {
	public static final ResourceLocation NET_ID = new ResourceLocation(MoreImmersiveWires.modid, "id_network");

	@Override
	public IDConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new IDConnectorBlockEntity(MoreImmersiveWires.ID_WIRE.CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).is(RegistryEntries.BLOCK_CABLE);
	}

	@Override
	public Block makeBlock0(RegistryObject<BlockEntityType<IDConnectorBlockEntity>> type) {
		return new IDConnectorBlock(type, this::isCable);
	}

	@Override
	public void init() {
		LocalNetworkHandler.register(NET_ID, IDNetworkHandler::new);
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(NET_ID);
	}
}
