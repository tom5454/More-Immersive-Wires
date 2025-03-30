package com.tom.morewires.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.cyclops.integrateddynamics.RegistryEntries;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.MoreImmersiveWires.Wire;
import com.tom.morewires.WireTypeDefinition.ConnectorInfo;
import com.tom.morewires.WireTypeDefinition.RelayInfo;
import com.tom.morewires.WireTypeDefinition.WireInfo;
import com.tom.morewires.compat.cc.CCWireDefinition;

import dan200.computercraft.shared.ModRegistry;

import appeng.core.definitions.AEBlocks;
import appeng.datagen.providers.tags.ConventionTags;
import blusunrize.immersiveengineering.api.IETags;

public class Recipes extends RecipeProvider {

	public Recipes(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries);
	}

	public static enum WireRecipe {
		AE(MoreImmersiveWires.AE_WIRE, CraftingIngredient.of(Blocks.DEEPSLATE), CraftingIngredient.of(ConventionTags.FLUIX_CRYSTAL), CraftingIngredient.of(ConventionTags.GLASS_CABLE)),
		AE_DENSE(MoreImmersiveWires.AE_DENSE_WIRE, CraftingIngredient.of(AEBlocks.SKY_STONE_BLOCK), CraftingIngredient.of(AEBlocks.FLUIX_BLOCK), CraftingIngredient.of(ConventionTags.COVERED_DENSE_CABLE)),
		RS(MoreImmersiveWires.RS_WIRE, CraftingIngredient.of(Tags.Items.STONES), CraftingIngredient.of(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getQuartzEnrichedCopper()), new TagIngredient(com.refinedmods.refinedstorage.common.content.Tags.CABLES)),
		ID(MoreImmersiveWires.ID_WIRE, CraftingIngredient.of(RegistryEntries.BLOCK_MENRIL_WOOD.get()), CraftingIngredient.of("integrateddynamics:crystalized_menril_chunk"), CraftingIngredient.of(RegistryEntries.BLOCK_CABLE.get())),
		CC(MoreImmersiveWires.CC_WIRE, CraftingIngredient.of(Tags.Items.STONES), CraftingIngredient.of(Items.REDSTONE), CraftingIngredient.of(ModRegistry.Items.CABLE.get())),
		;
		public final WireInfo[] wire;
		public final ConnectorInfo[] connector;
		public final RelayInfo[] relay;
		public final CraftingIngredient[] baseItem, coreItem, cableItem, wireItem;
		public final String modid;

		private WireRecipe(Wire wire, CraftingIngredient baseItem, CraftingIngredient coreItem,
				CraftingIngredient cableItem) {
			this(wire, baseItem, coreItem, cableItem, CraftingIngredient.of(IETags.aluminumWire));
		}

		private WireRecipe(Wire wire, CraftingIngredient baseItem, CraftingIngredient coreItem,
				CraftingIngredient cableItem, CraftingIngredient wireItem) {
			this(
					wire.wireTypeDef.getWireCoils().toArray(WireInfo[]::new),
					wire.wireTypeDef.getConnectors().toArray(ConnectorInfo[]::new),
					wire.wireTypeDef.getRelays().toArray(RelayInfo[]::new),
					new CraftingIngredient[] {baseItem},
					new CraftingIngredient[] {coreItem},
					new CraftingIngredient[] {cableItem},
					new CraftingIngredient[] {wireItem},
					wire.modid
					);
		}

		private WireRecipe(Wire wire, WireInfo wi, CraftingIngredient baseItem, CraftingIngredient coreItem,
				CraftingIngredient cableItem) {
			this(
					new WireInfo[] {wi},
					wire.wireTypeDef.getConnectors().toArray(ConnectorInfo[]::new),
					wire.wireTypeDef.getRelays().toArray(RelayInfo[]::new),
					new CraftingIngredient[] {baseItem},
					new CraftingIngredient[] {coreItem},
					new CraftingIngredient[] {cableItem},
					new CraftingIngredient[] {cableItem},
					wire.modid
					);
		}

		private WireRecipe(Wire wire, WireInfo[] wi, CraftingIngredient[] cableItem) {
			this(
					wi,
					new ConnectorInfo[0],
					new RelayInfo[0],
					new CraftingIngredient[0],
					new CraftingIngredient[0],
					cableItem,
					cableItem,
					wire.modid
					);
		}

		private WireRecipe(Wire wire, CraftingIngredient[] baseItem,
				CraftingIngredient[] coreItem, CraftingIngredient[] cableItem) {
			this(
					wire.wireTypeDef.getWireCoils().toArray(WireInfo[]::new),
					wire.wireTypeDef.getConnectors().toArray(ConnectorInfo[]::new),
					wire.wireTypeDef.getRelays().toArray(RelayInfo[]::new),
					baseItem, coreItem, cableItem, cableItem,
					wire.modid
					);
		}

		private WireRecipe(Wire wire, CraftingIngredient[] baseItem,
				CraftingIngredient[] coreItem, CraftingIngredient[] cableItem, CraftingIngredient[] wireItem) {
			this(
					new WireInfo[] {wire.wireTypeDef.getWireCoils().get(0)},
					new ConnectorInfo[] {wire.wireTypeDef.getConnectors().get(0)},
					new RelayInfo[] {wire.wireTypeDef.getRelays().get(0)},
					baseItem, coreItem, cableItem, wireItem,
					wire.modid);
		}

		private WireRecipe(WireInfo[] wire, ConnectorInfo[] connector, RelayInfo[] relay, CraftingIngredient[] baseItem,
				CraftingIngredient[] coreItem, CraftingIngredient[] cableItem, CraftingIngredient[] wireItem,
				String modid) {
			this.wire = wire;
			this.connector = connector;
			this.relay = relay;
			this.baseItem = baseItem;
			this.coreItem = coreItem;
			this.cableItem = cableItem;
			this.wireItem = wireItem;
			this.modid = modid;
		}
	}

	@Override
	protected void buildRecipes(RecipeOutput consumer) {
		for (WireRecipe wr : WireRecipe.values()) {
			ConditionalRecipeUtil.builder().addCondition(new ModLoadedCondition(wr.modid)).addRecipe(cc -> {
				for (int i = 0; i < wr.wire.length; i++) {
					WireInfo w = wr.wire[i];
					ShapedRecipeBuilderEx.shaped(w.getCoilItem().get(), 4)
					.pattern("wcw")
					.pattern("csc")
					.pattern("wcw")
					.define('s', Tags.Items.RODS_WOODEN)
					.define('c', wr.cableItem[i].ingredient())
					.define('w', wr.wireItem[i].ingredient())
					.unlockedBy(w.getName() + "_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.cableItem[i].predicate()))
					.save(cc);
				}

				for (int i = 0; i < wr.relay.length; i++) {
					RelayInfo r = wr.relay[i];
					if(r.isExTallRelay()) {
						ShapedRecipeBuilderEx.shaped(r.getRelayBlock().get(), 8)
						.pattern(" c ")
						.pattern("bcb")
						.pattern("bcb")
						.define('b', wr.baseItem[i].ingredient())
						.define('c', wr.coreItem[i].ingredient())
						.unlockedBy(r.getName() + "_relay_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.baseItem[i].predicate(), wr.coreItem[i].predicate()))
						.save(cc);
					} else {
						ShapedRecipeBuilderEx.shaped(r.getRelayBlock().get(), 8)
						.pattern(" c ")
						.pattern("bcb")
						.define('b', wr.baseItem[i].ingredient())
						.define('c', wr.coreItem[i].ingredient())
						.unlockedBy(r.getName() + "_relay_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.baseItem[i].predicate(), wr.coreItem[i].predicate()))
						.save(cc);
					}
				}

				for (int i = 0; i < wr.connector.length; i++) {
					ConnectorInfo c = wr.connector[i];
					ShapedRecipeBuilderEx.shaped(c.getConnectorBlock().get(), 4)
					.pattern(" c ")
					.pattern("bcb")
					.pattern("bwb")
					.define('b', wr.baseItem[i].ingredient())
					.define('c', wr.coreItem[i].ingredient())
					.define('w', wr.cableItem[i].ingredient())
					.unlockedBy(c.getName() + "_connector_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.baseItem[i].predicate(), wr.coreItem[i].predicate(), wr.cableItem[i].predicate()))
					.save(cc);
				}
			}).build(consumer);
		}

		ConditionalRecipeUtil.builder().addCondition(new ModLoadedCondition(MoreImmersiveWires.CC)).addRecipe(cc -> {
			ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, CCWireDefinition.CC_MODEM_CONNECTOR.get())
			.requires(MoreImmersiveWires.CC_WIRE.simple().CONNECTOR.get())
			.requires(ModRegistry.Items.WIRED_MODEM.get())
			.unlockedBy("cc_modem_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(MoreImmersiveWires.CC_WIRE.simple().CONNECTOR.get(), ModRegistry.Items.WIRED_MODEM.get()))
			.save(cc);
		}).build(consumer);
	}

	public static interface CraftingIngredient {
		Ingredient ingredient();
		ItemPredicate predicate();

		public static CraftingIngredient of(ItemLike item) {
			return new ItemIngredient(item);
		}

		public static CraftingIngredient of(String item) {
			return of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item)));
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

	public static class ShapedRecipeBuilderEx extends ShapedRecipeBuilder {
		static Map<ItemLike, Integer> dupHandler = new HashMap<>();

		public ShapedRecipeBuilderEx(ItemLike p_126114_, int p_126115_) {
			super(RecipeCategory.MISC, p_126114_, p_126115_);
		}

		public static ShapedRecipeBuilderEx shaped(ItemLike p_126117_) {
			return shaped(p_126117_, 1);
		}

		public static ShapedRecipeBuilderEx shaped(ItemLike p_126119_, int p_126120_) {
			return new ShapedRecipeBuilderEx(p_126119_, p_126120_);
		}

		@Override
		public void save(RecipeOutput p_176499_) {
			ResourceLocation r = BuiltInRegistries.ITEM.getKey(getResult());
			int d = dupHandler.merge(getResult(), 1, Integer::sum);
			save(p_176499_, ResourceLocation.tryBuild(r.getNamespace(), r.getPath() + (d > 1 ? d : "")));
		}
	}
}
