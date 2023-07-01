package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.ftbic.FTBICWireDefinition;
import com.tom.morewires.compat.ic2.IC2WireDefinition;

public class ItemModels extends ItemModelProvider {

	public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
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

		withExistingParent(ForgeRegistries.BLOCKS.getKey(MoreImmersiveWires.AE_DENSE_WIRE.simple().CONNECTOR.get()).getPath(), modLoc("block/ae_dense_connector"));
		withExistingParent(ForgeRegistries.BLOCKS.getKey(CCWireDefinition.CC_MODEM_CONNECTOR.get()).getPath(), modLoc("block/modem_connector"))
		.texture("texture", modLoc("block/relay_cc"))
		.texture("back", new ResourceLocation("computercraft:block/modem_back"))
		.texture("front", new ResourceLocation("computercraft:block/wired_modem_face"));

		singleTexture(ForgeRegistries.ITEMS.getKey(FTBICWireDefinition.ENERGY_ALLOY.get()).getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/energy_alloy"));

		singleTexture(ForgeRegistries.ITEMS.getKey(IC2WireDefinition.ENERGY_ALLOY.get()).getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/energy_alloy"));

		singleTexture(ForgeRegistries.ITEMS.getKey(IC2WireDefinition.UNCOMPRESSED_INSULATION.get()).getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/ic2_unc_ins"));

		singleTexture(ForgeRegistries.ITEMS.getKey(IC2WireDefinition.COMPRESSED_INSULATION.get()).getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/ic2_comp_ins"));
	}
}
