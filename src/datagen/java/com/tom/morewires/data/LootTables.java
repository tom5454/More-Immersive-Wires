package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;

import com.tom.morewires.MoreImmersiveWires;

public class LootTables extends BaseLootTableProvider {

	public LootTables(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected void addTables() {
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			lootTables.put(w.RELAY.get(), createSimpleTable(w.name + "_relay", w.RELAY.get()));
			lootTables.put(w.CONNECTOR.get(), createSimpleTable(w.name + "_connector", w.CONNECTOR.get()));
		});
	}
}
