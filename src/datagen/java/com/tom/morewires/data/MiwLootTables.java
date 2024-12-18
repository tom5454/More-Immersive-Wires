package com.tom.morewires.data;

import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

public class MiwLootTables extends VanillaBlockLoot {
	public MiwLootTables(Provider registries) {
		super(registries);
	}

	@Override
	protected void generate() {
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getConnectors().forEach(c -> {
				dropSelf(c.getConnectorBlock().get());
			});
			wt.wireTypeDef.getRelays().forEach(r -> {
				dropSelf(r.getRelayBlock().get());
			});
		});
		dropSelf(CCWireDefinition.CC_MODEM_CONNECTOR.get());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return BuiltInRegistries.BLOCK.entrySet().stream()
				.filter(e -> e.getKey().location().getNamespace().equals(MoreImmersiveWires.modid))
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
	}
}
