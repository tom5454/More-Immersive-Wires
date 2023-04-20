package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.common.data.LanguageProvider;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

public class LangProvider extends LanguageProvider {

	public LangProvider(DataGenerator gen, String locale) {
		super(gen, MoreImmersiveWires.modid, locale);
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.more_immersive_wires.tab", "More Immersive Wires");
		add("tooltip.more_immersive_wires.hold_shift_for_info", "Hold SHIFT for more info.");
		add("config.moreimmersivewires.wires", "Wire Settings");
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			add(w.COIL.get(), w.localized + " Coil");
			add(w.RELAY.get(), w.localized + " Relay");
			add(w.CONNECTOR.get(), w.localized + " Connector");
			add("config.moreimmersivewires." + w.name + ".maxlen", w.localized + " Cable Max Length");
			add("config.moreimmersivewires." + w.name + ".settings", w.localized + " Cable Settings");
		});
		add(CCWireDefinition.CC_MODEM_CONNECTOR.get(), "CC Modem with Connector");
	}

}
