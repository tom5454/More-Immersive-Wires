package com.tom.morewires.compat.id;

import org.cyclops.integrateddynamics.RegistryEntries;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.SimpleWireTypeDefinition;

import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;

public class IntegratedDynamicsWireDefinition extends SimpleWireTypeDefinition<IDConnectorBlockEntity> {

	public IntegratedDynamicsWireDefinition() {
		super("id", "Logic Cable", 0x335566);
	}

	@Override
	public IDConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new IDConnectorBlockEntity(MoreImmersiveWires.ID_WIRE.simple().CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).is(RegistryEntries.BLOCK_CABLE);
	}

	@Override
	public Block makeBlock(RegistryObject<BlockEntityType<IDConnectorBlockEntity>> type) {
		return new IDConnectorBlock(type, this::isCable);
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return IDNetworkHandler::new;
	}
}
