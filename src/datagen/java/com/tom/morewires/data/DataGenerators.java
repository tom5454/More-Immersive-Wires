package com.tom.morewires.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.tom.morewires.MoreImmersiveWires;

@Mod.EventBusSubscriber(modid = MoreImmersiveWires.modid, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeServer(), new Recipes(generator));
		generator.addProvider(event.includeServer(), new LootTables(generator));
		MiwBlockTags blockTags = new MiwBlockTags(generator, event.getExistingFileHelper());
		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(), new MiwItemTags(generator, blockTags, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new BlockStates(generator, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new ItemModels(generator, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new ItemModels2(generator, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new LangProvider(generator, "en_us"));
	}
}
