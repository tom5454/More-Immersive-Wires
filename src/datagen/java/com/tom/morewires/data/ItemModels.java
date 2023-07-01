package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.ftbic.FTBICWireDefinition;

public class ItemModels extends ItemModelProvider {

	public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, MoreImmersiveWires.modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getWireCoils().forEach(w -> {
				singleTexture(w.getCoilItem().get().getRegistryName().getPath(),
						mcLoc("item/generated"),
						"layer0", modLoc("item/" + w.getName() + "_coil"));
			});
		});

		withExistingParent(MoreImmersiveWires.AE_DENSE_WIRE.simple().CONNECTOR.get().getRegistryName().getPath(), modLoc("block/ae_dense_connector"));
		withExistingParent(CCWireDefinition.CC_MODEM_CONNECTOR.get().getRegistryName().getPath(), modLoc("block/modem_connector"))
		.texture("texture", modLoc("block/relay_cc"))
		.texture("back", new ResourceLocation("computercraft:block/modem_back"))
		.texture("front", new ResourceLocation("computercraft:block/wired_modem_face"));

		singleTexture(FTBICWireDefinition.ENERGY_ALLOY.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/ftbic_energy_alloy"));
	}
}
