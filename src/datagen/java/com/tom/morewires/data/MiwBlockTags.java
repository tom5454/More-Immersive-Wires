package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

import blusunrize.immersiveengineering.api.IETags;

public class MiwBlockTags extends BlockTagsProvider {

	public MiwBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MoreImmersiveWires.modid, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		List<Block> mineable = new ArrayList<>();
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getConnectors().forEach(e -> mineable.add(e.getConnectorBlock().get()));
			wt.wireTypeDef.getRelays().forEach(e -> mineable.add(e.getRelayBlock().get()));
		});
		mineable.add(CCWireDefinition.CC_MODEM_CONNECTOR.get());

		TagsProvider.TagAppender<Block> pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
		TagsProvider.TagAppender<Block> hammer = tag(IETags.hammerHarvestable);
		mineable.stream().map(BuiltInRegistries.BLOCK::getKey).forEach(r -> {
			pickaxe.addOptional(r);
			hammer.addOptional(r);
		});
	}

	@Override
	public String getName() {
		return "More Immersive Wires Block Tags";
	}
}