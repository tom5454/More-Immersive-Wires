package com.tom.morewires.data;

import static blusunrize.immersiveengineering.ImmersiveEngineering.rl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.google.common.base.Preconditions;

import com.tom.morewires.MoreImmersiveWires;

import blusunrize.immersiveengineering.data.models.TRSRItemModelProvider;
import blusunrize.immersiveengineering.data.models.TRSRModelBuilder;

public class ItemModels2 extends TRSRItemModelProvider {

	public ItemModels2(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, MoreImmersiveWires.modid, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Item Models 2";
	}

	@Override
	protected void registerModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getRelays().forEach(r -> {
				obj(r.getRelayBlock().get(), r.isExTallRelay() ? modLoc("block/relay_hv.obj") : r.isTallRelay() ? modLoc("block/connector_hv.obj") : rl("block/connector/connector_mv.obj"))
				.texture("texture", modLoc("block/relay_" + r.getName()))
				.transforms(modLoc("item/connector"));
			});
			wt.wireTypeDef.getConnectors().forEach(c -> {
				if(c.datagenConnectorBlock()) {
					obj(c.getConnectorBlock().get(), c.isTallConnector() ? modLoc("block/connector_hv.obj") : rl("block/connector/connector_mv.obj"))
					.texture("texture", modLoc("block/connector_" + c.getName()))
					.transforms(modLoc("item/connector"));
				}
			});
		});
	}

	private TRSRModelBuilder obj(ItemLike item, ResourceLocation model) {
		Preconditions.checkArgument(existingFileHelper.exists(model, PackType.CLIENT_RESOURCES, "", "models"));
		return getBuilder(item)
				.customLoader(ObjModelBuilder::begin)
				.flipV(true)
				.modelLocation(ResourceLocation.tryBuild(model.getNamespace(), "models/"+model.getPath()))
				.end();
	}

	private TRSRModelBuilder getBuilder(ItemLike item) {
		return getBuilder(name(item));
	}

	private String name(ItemLike item) {
		return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
	}
}
