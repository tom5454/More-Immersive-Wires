package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

public class LootTables extends BaseLootTableProvider {

	public LootTables(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected void addTables() {
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getConnectors().forEach(c -> {
				lootTables.put(c.getConnectorBlock().get(), createSimpleTable(c.getName() + "_connector", c.getConnectorBlock().get()));
			});
			wt.wireTypeDef.getRelays().forEach(r -> {
				lootTables.put(r.getRelayBlock().get(), createSimpleTable(r.getName() + "_relay", r.getRelayBlock().get()));
			});
		});
		lootTables.put(CCWireDefinition.CC_MODEM_CONNECTOR.get(), createSimpleTable("cc_modem_connector", CCWireDefinition.CC_MODEM_CONNECTOR.get()));
	}
}
