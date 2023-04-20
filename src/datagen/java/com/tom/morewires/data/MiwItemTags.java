package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

import blusunrize.immersiveengineering.api.IETags;

public class MiwItemTags extends ItemTagsProvider {

	public MiwItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
		super(generator, blockTags, MoreImmersiveWires.modid, helper);
	}

	@Override
	protected void addTags() {
		List<Item> wiring = new ArrayList<>();
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			wiring.add(w.CONNECTOR.get().asItem());
			wiring.add(w.RELAY.get().asItem());
			wiring.add(w.COIL.get());
		});
		wiring.add(CCWireDefinition.CC_MODEM_CONNECTOR.get().asItem());

		TagsProvider.TagAppender<Item> w = tag(IETags.toolboxWiring);
		wiring.stream().map(ForgeRegistries.ITEMS::getKey).forEach(w::addOptional);
	}

	@Override
	public String getName() {
		return "More Immersive Wires Item Tags";
	}
}
