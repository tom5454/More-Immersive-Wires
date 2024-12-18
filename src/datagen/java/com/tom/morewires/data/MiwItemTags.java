package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

import blusunrize.immersiveengineering.api.IETags;

public class MiwItemTags extends ItemTagsProvider {

	public MiwItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTags, ExistingFileHelper helper) {
		super(packOutput, lookupProvider, blockTags.contentsGetter(), MoreImmersiveWires.modid, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		List<Item> wiring = new ArrayList<>();
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getConnectors().forEach(e -> wiring.add(e.getConnectorBlock().get().asItem()));
			wt.wireTypeDef.getRelays().forEach(e -> wiring.add(e.getRelayBlock().get().asItem()));
			wt.wireTypeDef.getWireCoils().forEach(e -> wiring.add(e.getCoilItem().get()));
		});
		wiring.add(CCWireDefinition.CC_MODEM_CONNECTOR.get().asItem());

		TagsProvider.TagAppender<Item> w = tag(IETags.toolboxWiring);
		wiring.stream().map(BuiltInRegistries.ITEM::getKey).forEach(w::addOptional);
	}

	@Override
	public String getName() {
		return "More Immersive Wires Item Tags";
	}
}
