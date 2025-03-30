package com.tom.morewires.compat.rs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.refinedmods.refinedstorage.common.networking.CableBlock;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.SimpleWireTypeDefinition;

import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;

public class RSWireDefinition extends SimpleWireTypeDefinition<RSConnectorBlockEntity> {

	public RSWireDefinition() {
		super("rs", "RS Cable", 0x222222);
	}

	@Override
	public RSConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new RSConnectorBlockEntity(MoreImmersiveWires.RS_WIRE.simple().CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof CableBlock;
	}

	@Override
	public Block makeBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<RSConnectorBlockEntity>> type) {
		return new RSConnectorBlock(type, this::isCable);
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return RSNetworkHandler::new;
	}

	@Override
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(), CONNECTOR_ENTITY.get(), (be, _v) -> be.getContainerProvider());
	}
}
