package com.tom.morewires.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

public class ItemModels extends ItemModelProvider {

	public ItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, MoreImmersiveWires.modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getWireCoils().forEach(w -> {
				if(w.doDatagen()) {
					singleTexture(w.getCoilItem().getId().getPath(),
							mcLoc("item/generated"),
							"layer0", modLoc("item/" + w.getItemName() + "_coil"));
				}
			});
		});

		withExistingParent(BuiltInRegistries.BLOCK.getKey(MoreImmersiveWires.AE_DENSE_WIRE.simple().CONNECTOR.get()).getPath(), modLoc("block/ae_dense_connector"));
		withExistingParent(BuiltInRegistries.BLOCK.getKey(CCWireDefinition.CC_MODEM_CONNECTOR.get()).getPath(), modLoc("block/modem_connector"))
		.texture("texture", modLoc("block/relay_cc"))
		.texture("back", ResourceLocation.parse("computercraft:block/modem_back"))
		.texture("front", ResourceLocation.parse("computercraft:block/wired_modem_face"));
	}
}
