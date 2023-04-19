package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import com.tom.morewires.MoreImmersiveWires;

@Mod.EventBusSubscriber(modid = MoreImmersiveWires.modid, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			generator.addProvider(new Recipes(generator));
			generator.addProvider(new LootTables(generator));
			MiwBlockTags blockTags = new MiwBlockTags(generator, event.getExistingFileHelper());
			generator.addProvider(blockTags);
			generator.addProvider(new MiwItemTags(generator, blockTags, event.getExistingFileHelper()));
		}
		if (event.includeClient()) {
			generator.addProvider(new BlockStates(generator, event.getExistingFileHelper()));
			generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
			generator.addProvider(new ItemModels2(generator, event.getExistingFileHelper()));
			generator.addProvider(new LangProvider(generator, "en_us"));
		}
	}
}
