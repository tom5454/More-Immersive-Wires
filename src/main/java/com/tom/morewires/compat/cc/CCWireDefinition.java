package com.tom.morewires.compat.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.SimpleWireTypeDefinition;

import dan200.computercraft.shared.ModRegistry;

import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;

public class CCWireDefinition extends SimpleWireTypeDefinition<CCConnectorBlockEntity> {

	public CCWireDefinition() {
		super("cc", "Networking Cable", 0x888888);
	}

	public static RegistryObject<Block> CC_MODEM_CONNECTOR;
	public static RegistryObject<BlockEntityType<BlockEntity>> CC_MODEM_CONNECTOR_ENTITY;

	@Override
	public CCConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new CCConnectorBlockEntity(MoreImmersiveWires.CC_WIRE.simple().CONNECTOR_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).is(ModRegistry.Blocks.CABLE.get());
	}

	@Override
	public void init() {
		super.init();

		CC_MODEM_CONNECTOR = MoreImmersiveWires.blockWithItem("cc_modem", () -> new CCModemConnectorBlock(CC_MODEM_CONNECTOR_ENTITY), b -> new BlockItemIE(b, new Item.Properties()));
		CC_MODEM_CONNECTOR_ENTITY = MoreImmersiveWires.blockEntity("cc_modem.tile", (p, s) -> new CCModemConnectorBlockEntity(CC_MODEM_CONNECTOR_ENTITY.get(), p, s), CC_MODEM_CONNECTOR);
	}

	@Override
	public Block makeBlock(RegistryObject<BlockEntityType<CCConnectorBlockEntity>> type) {
		return new CCConnectorBlock(type, this::isCable);
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return CCNetworkHandler::new;
	}
}
