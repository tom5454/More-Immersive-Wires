package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.compat.cc.CCWireDefinition;

import blusunrize.immersiveengineering.api.IETags;

public class MiwBlockTags extends BlockTagsProvider {

	public MiwBlockTags(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, MoreImmersiveWires.modid, helper);
	}

	@Override
	protected void addTags() {
		List<Block> mineable = new ArrayList<>();
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getConnectors().forEach(e -> mineable.add(e.getConnectorBlock().get()));
			wt.wireTypeDef.getRelays().forEach(e -> mineable.add(e.getRelayBlock().get()));
		});
		mineable.add(CCWireDefinition.CC_MODEM_CONNECTOR.get());

		TagsProvider.TagAppender<Block> pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
		TagsProvider.TagAppender<Block> hammer = tag(IETags.hammerHarvestable);
		mineable.stream().map(ForgeRegistries.BLOCKS::getKey).forEach(r -> {
			pickaxe.addOptional(r);
			hammer.addOptional(r);
		});
	}

	@Override
	public String getName() {
		return "More Immersive Wires Block Tags";
	}
}