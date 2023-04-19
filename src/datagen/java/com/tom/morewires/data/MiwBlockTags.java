package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.morewires.MoreImmersiveWires;

import blusunrize.immersiveengineering.api.IETags;

public class MiwBlockTags extends BlockTagsProvider {

	public MiwBlockTags(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, MoreImmersiveWires.modid, helper);
	}

	@Override
	protected void addTags() {
		List<Block> mineable = new ArrayList<>();
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			mineable.add(w.CONNECTOR.get());
			mineable.add(w.RELAY.get());
		});
		mineable.add(MoreImmersiveWires.CC_MODEM_CONNECTOR.get());

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(mineable.toArray(Block[]::new));
		tag(IETags.hammerHarvestable).add(mineable.toArray(Block[]::new));
	}

	@Override
	public String getName() {
		return "More Immersive Wires Block Tags";
	}
}