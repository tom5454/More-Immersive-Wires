package com.tom.morewires.compat.id;

import org.cyclops.integrateddynamics.Capabilities;
import org.cyclops.integrateddynamics.RegistryEntries;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

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
	public Block makeBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<IDConnectorBlockEntity>> type) {
		return new IDConnectorBlock(type, this::isCable);
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return IDNetworkHandler::new;
	}

	@Override
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Cable.BLOCK, CONNECTOR_ENTITY.get(), (blockEntity, context) -> blockEntity.getCable());
		event.registerBlockEntity(Capabilities.NetworkCarrier.BLOCK, CONNECTOR_ENTITY.get(), (blockEntity, context) -> blockEntity.getNetworkCarrier());
		event.registerBlockEntity(Capabilities.PathElement.BLOCK, CONNECTOR_ENTITY.get(), (blockEntity, context) -> blockEntity.getPathElement());
	}
}
