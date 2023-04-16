package com.tom.morewires.data;

import java.util.function.Consumer;

import org.cyclops.integrateddynamics.RegistryEntries;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.MoreImmersiveWires.Wire;

import dan200.computercraft.shared.Registry;

import appeng.core.definitions.AEBlocks;
import appeng.datagen.providers.tags.ConventionTags;
import blusunrize.immersiveengineering.api.IETags;

public class Recipes extends RecipeProvider {

	public Recipes(DataGenerator p_125973_) {
		super(p_125973_);
	}

	public static enum WireRecipe {
		AE(MoreImmersiveWires.AE_WIRE, CraftingIngredient.of(Blocks.DEEPSLATE), CraftingIngredient.of(ConventionTags.FLUIX_CRYSTAL), CraftingIngredient.of(ConventionTags.GLASS_CABLE)),
		AE_DENSE(MoreImmersiveWires.AE_DENSE_WIRE, CraftingIngredient.of(AEBlocks.SKY_STONE_BLOCK), CraftingIngredient.of(AEBlocks.FLUIX_BLOCK), CraftingIngredient.of(ConventionTags.COVERED_DENSE_CABLE)),
		RS(MoreImmersiveWires.RS_WIRE, CraftingIngredient.of(Tags.Items.STONE), CraftingIngredient.of(RSItems.QUARTZ_ENRICHED_IRON.get()), CraftingIngredient.of(RSBlocks.CABLE.get())),
		ID(MoreImmersiveWires.ID_WIRE, CraftingIngredient.of(RegistryEntries.BLOCK_MENRIL_WOOD), CraftingIngredient.of("integrateddynamics:crystalized_menril_chunk"), CraftingIngredient.of(RegistryEntries.BLOCK_CABLE)),
		CC(MoreImmersiveWires.CC_WIRE, CraftingIngredient.of(Tags.Items.STONE), CraftingIngredient.of(Items.REDSTONE), CraftingIngredient.of(Registry.ModItems.CABLE.get())),
		;
		public final Wire wire;
		public final CraftingIngredient baseItem, coreItem, cableItem, wireItem;

		private WireRecipe(Wire wire, CraftingIngredient baseItem, CraftingIngredient coreItem,
				CraftingIngredient cableItem) {
			this(wire, baseItem, coreItem, cableItem, CraftingIngredient.of(IETags.aluminumWire));
		}

		private WireRecipe(Wire wire, CraftingIngredient baseItem, CraftingIngredient coreItem,
				CraftingIngredient cableItem, CraftingIngredient wireItem) {
			this.wire = wire;
			this.baseItem = baseItem;
			this.coreItem = coreItem;
			this.cableItem = cableItem;
			this.wireItem = wireItem;
		}
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		for (WireRecipe wr : WireRecipe.values()) {
			ShapedRecipeBuilder.shaped(wr.wire.COIL.get(), 4)
			.pattern("wcw")
			.pattern("csc")
			.pattern("wcw")
			.define('s', Tags.Items.RODS_WOODEN)
			.define('c', wr.cableItem.ingredient())
			.define('w', wr.wireItem.ingredient())
			.unlockedBy(wr.wire.name + "_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.cableItem.predicate()))
			.save(consumer);

			ShapedRecipeBuilder.shaped(wr.wire.RELAY.get(), 8)
			.pattern(" c ")
			.pattern("bcb")
			.define('b', wr.baseItem.ingredient())
			.define('c', wr.coreItem.ingredient())
			.unlockedBy(wr.wire.name + "_relay_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.baseItem.predicate(), wr.coreItem.predicate()))
			.save(consumer);

			ShapedRecipeBuilder.shaped(wr.wire.CONNECTOR.get(), 4)
			.pattern(" c ")
			.pattern("bcb")
			.pattern("bwb")
			.define('b', wr.baseItem.ingredient())
			.define('c', wr.coreItem.ingredient())
			.define('w', wr.cableItem.ingredient())
			.unlockedBy(wr.wire.name + "_connector_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.baseItem.predicate(), wr.coreItem.predicate(), wr.cableItem.predicate()))
			.save(consumer);
		}

		ShapelessRecipeBuilder.shapeless(MoreImmersiveWires.CC_MODEM_CONNECTOR.get())
		.requires(MoreImmersiveWires.CC_WIRE.CONNECTOR.get())
		.requires(Registry.ModItems.WIRED_MODEM.get())
		.unlockedBy("cc_modem_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(MoreImmersiveWires.CC_WIRE.CONNECTOR.get(), Registry.ModItems.WIRED_MODEM.get()))
		.save(consumer);
	}

	public static interface CraftingIngredient {
		Ingredient ingredient();
		ItemPredicate predicate();

		public static CraftingIngredient of(ItemLike item) {
			return new ItemIngredient(item);
		}

		public static CraftingIngredient of(String item) {
			return of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
		}

		public static CraftingIngredient of(TagKey<Item> item) {
			return new TagIngredient(item);
		}
	}

	public static class ItemIngredient implements CraftingIngredient {
		private final ItemLike item;

		public ItemIngredient(ItemLike item) {
			this.item = item;
		}

		@Override
		public Ingredient ingredient() {
			return Ingredient.of(item);
		}

		@Override
		public ItemPredicate predicate() {
			return ItemPredicate.Builder.item().of(item).build();
		}
	}

	public static class TagIngredient implements CraftingIngredient {
		private final TagKey<Item> tag;

		public TagIngredient(TagKey<Item> tag) {
			this.tag = tag;
		}

		@Override
		public Ingredient ingredient() {
			return Ingredient.of(tag);
		}

		@Override
		public ItemPredicate predicate() {
			return ItemPredicate.Builder.item().of(tag).build();
		}
	}
}
