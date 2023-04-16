package com.tom.morewires.data;

import static blusunrize.immersiveengineering.ImmersiveEngineering.rl;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ItemLike;

import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.google.common.base.Preconditions;

import com.tom.morewires.MoreImmersiveWires;

import blusunrize.immersiveengineering.data.models.TRSRItemModelProvider;
import blusunrize.immersiveengineering.data.models.TRSRModelBuilder;

public class ItemModels2 extends TRSRItemModelProvider {

	public ItemModels2(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Item Models 2";
	}

	@Override
	protected void registerModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			obj(w.RELAY.get(), w.tall ? modLoc("block/connector_hv.obj") : rl("block/connector/connector_mv.obj"))
			.texture("texture", modLoc("block/relay_" + w.name))
			.transforms(rl("item/connector"));

			if(w !=  MoreImmersiveWires.AE_DENSE_WIRE) {
				obj(w.CONNECTOR.get(), w.tall ? modLoc("block/connector_hv.obj") : rl("block/connector/connector_mv.obj"))
				.texture("texture", modLoc("block/connector_" + w.name))
				.transforms(rl("item/connector"));
			}
		});
	}

	public TRSRModelBuilder obj(ItemLike item, ResourceLocation model) {
		Preconditions.checkArgument(existingFileHelper.exists(model, PackType.CLIENT_RESOURCES, "", "models"));
		return getBuilder(item)
				.customLoader(ObjModelBuilder::begin)
				.flipV(true)
				.modelLocation(new ResourceLocation(model.getNamespace(), "models/"+model.getPath()))
				.end();
	}

	private TRSRModelBuilder getBuilder(ItemLike item) {
		return getBuilder(name(item));
	}

	private String name(ItemLike item) {
		return Registry.ITEM.getKey(item.asItem()).getPath();
	}
}
