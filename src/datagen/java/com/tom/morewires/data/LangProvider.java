package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.common.data.LanguageProvider;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.ftbic.FTBICWireDefinition;
import com.tom.morewires.compat.ic2.IC2WireDefinition;

public class LangProvider extends LanguageProvider {

	public LangProvider(DataGenerator gen, String locale) {
		super(gen, MoreImmersiveWires.modid, locale);
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.more_immersive_wires.tab", "More Immersive Wires");
		add("tooltip.more_immersive_wires.hold_shift_for_info", "Hold SHIFT for more info.");
		add("tooltip.more_immersive_wires.mod_id", "For mod: %s");
		add("config.moreimmersivewires.wires", "Wire Settings");
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			add("config.moreimmersivewires." + wt.wireTypeDef.getName() + ".settings", wt.wireTypeDef.getLocalized() + " Cable Settings");
			wt.wireTypeDef.getWireCoils().forEach(w -> {
				if (w.doDatagen()) {
					add(w.getCoilItem().get(), w.getLocalized() + " Coil");
					add("config.moreimmersivewires." + w.getName() + ".maxlen", w.getLocalized() + " Cable Max Length");
				}
			});
			wt.wireTypeDef.getRelays().forEach(r -> {
				add(r.getRelayBlock().get(), r.getLocalized() + " Relay");
			});
			wt.wireTypeDef.getConnectors().forEach(c -> {
				add(c.getConnectorBlock().get(), c.getLocalized() + " Connector");
			});
			wt.wireTypeDef.addTranslations(this::add);
		});
		add(CCWireDefinition.CC_MODEM_CONNECTOR.get(), "CC Modem with Connector");
		add(FTBICWireDefinition.ENERGY_ALLOY.get(), "Energy Alloy");
		add("tooltip.more_immersive_wires.network_type", "Linking cable type: %s");
		add(IC2WireDefinition.ENERGY_ALLOY.get(), "Energy Alloy");
		add(IC2WireDefinition.UNCOMPRESSED_INSULATION.get(), "Uncompressed Insulation");
		add(IC2WireDefinition.COMPRESSED_INSULATION.get(), "Compressed Insulation");
	}

}
