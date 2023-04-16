package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import com.tom.morewires.MoreImmersiveWires;

public class ItemModels extends ItemModelProvider {

	public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, MoreImmersiveWires.modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			singleTexture(ForgeRegistries.ITEMS.getKey(w.COIL.get()).getPath(),
					mcLoc("item/generated"),
					"layer0", modLoc("item/" + w.name + "_coil"));
		});

		withExistingParent(ForgeRegistries.BLOCKS.getKey(MoreImmersiveWires.AE_DENSE_WIRE.CONNECTOR.get()).getPath(), modLoc("block/ae_dense_connector"));
		withExistingParent(ForgeRegistries.BLOCKS.getKey(MoreImmersiveWires.CC_MODEM_CONNECTOR.get()).getPath(), modLoc("block/modem_connector"))
		.texture("texture", modLoc("block/relay_cc"))
		.texture("back", new ResourceLocation("computercraft:block/modem_back"))
		.texture("front", new ResourceLocation("computercraft:block/wired_modem_face"));
	}
}
