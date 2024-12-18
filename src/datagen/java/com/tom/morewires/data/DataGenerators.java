package com.tom.morewires.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import com.tom.morewires.MoreImmersiveWires;

@EventBusSubscriber(modid = MoreImmersiveWires.modid, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new Recipes(packOutput, lookupProvider));
		var blockTags = new MiwBlockTags(packOutput, lookupProvider, event.getExistingFileHelper());
		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(), new MiwItemTags(packOutput, lookupProvider, blockTags, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new BlockStates(generator.getPackOutput(), event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new ItemModels(generator.getPackOutput(), event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new ItemModels2(generator.getPackOutput(), event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new LangProvider(generator.getPackOutput(), "en_us"));
		generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
				List.of(new LootTableProvider.SubProviderEntry(MiwLootTables::new, LootContextParamSets.BLOCK)), lookupProvider));
	}
}
