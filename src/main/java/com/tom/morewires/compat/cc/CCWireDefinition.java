package com.tom.morewires.compat.cc;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.WireTypeDefinition;

import dan200.computercraft.shared.Registry;

import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;

public class CCWireDefinition implements WireTypeDefinition<CCConnectorBlockEntity> {
	public static final ResourceLocation NET_ID = new ResourceLocation(MoreImmersiveWires.modid, "cc_network");

	public static RegistryObject<Block> CC_MODEM_CONNECTOR;
	public static RegistryObject<BlockEntityType<BlockEntity>> CC_MODEM_CONNECTOR_ENTITY;

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

		CC_MODEM_CONNECTOR = MoreImmersiveWires.blockWithItem("cc_modem", () -> new CCModemConnectorBlock(CC_MODEM_CONNECTOR_ENTITY), b -> new BlockItemIE(b, new Item.Properties().tab(MoreImmersiveWires.MOD_TAB)));
		CC_MODEM_CONNECTOR_ENTITY = MoreImmersiveWires.blockEntity("cc_modem.tile", (p, s) -> new CCModemConnectorBlockEntity(CC_MODEM_CONNECTOR_ENTITY.get(), p, s), CC_MODEM_CONNECTOR);
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
