package com.tom.morewires.compat.ae;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.SimpleWireTypeDefinition;

import appeng.api.AECapabilities;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;

public class AEDenseWireDefinition extends SimpleWireTypeDefinition<AEDenseConnectorBlockEntity> {

	public AEDenseWireDefinition() {
		super("ae_dense", "ME Dense Cable", 0x220055);
	}

	@Override
	public AEDenseConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new AEDenseConnectorBlockEntity(MoreImmersiveWires.AE_DENSE_WIRE.simple().CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public Item makeItemBlock(Block block) {
		return new AEItemBlock(block, this);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public Block makeBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<AEDenseConnectorBlockEntity>> type) {
		return new AEDenseConnectorBlock(type);
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return AEDenseNetworkHandler::new;
	}

	@Override
	public boolean isTallConnector() {
		return true;
	}

	@Override
	public boolean datagenConnectorBlock() {
		return false;
	}

	@Override
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, CONNECTOR_ENTITY.get(), (be, _v) -> be);
	}
}
