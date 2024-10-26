package com.tom.morewires.compat.ae;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.SimpleWireTypeDefinition;

import appeng.core.definitions.AEBlocks;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;

public class AEWireDefinition extends SimpleWireTypeDefinition<AEConnectorBlockEntity> {

	public AEWireDefinition() {
		super("ae", "ME Glass Cable", 0x331166);
	}

	@Override
	public AEConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new AEConnectorBlockEntity(MoreImmersiveWires.AE_WIRE.simple().CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public Item makeItemBlock(Block block) {
		return new AEItemBlock(block, this);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(AEBlocks.CABLE_BUS.block());
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return AENetworkHandler::new;
	}
}
