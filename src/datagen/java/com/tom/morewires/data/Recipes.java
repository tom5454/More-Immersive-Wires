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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.registries.ForgeRegistries;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.MoreImmersiveWires.Wire;
import com.tom.morewires.WireTypeDefinition.ConnectorInfo;
import com.tom.morewires.WireTypeDefinition.RelayInfo;
import com.tom.morewires.WireTypeDefinition.WireInfo;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.ftbic.FTBICWireDefinition;

import dan200.computercraft.shared.Registry;

import appeng.core.definitions.AEBlocks;
import appeng.datagen.providers.tags.ConventionTags;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.ArcFurnaceRecipeBuilder;
import dev.ftb.mods.ftbic.item.FTBICItems;

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
		FTBIC_LV(MoreImmersiveWires.FTBIC_WIRE_LV, CraftingIngredient.of(FTBICItems.RUBBER.item.get()), CraftingIngredient.of(Tags.Items.INGOTS_COPPER), CraftingIngredient.of(FTBICItems.LV_CABLE.get()), CraftingIngredient.of(FTBICItems.LV_CABLE.get())),
		FTBIC_MV(MoreImmersiveWires.FTBIC_WIRE_MV, CraftingIngredient.of(FTBICItems.RUBBER.item.get()), CraftingIngredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/aluminum"))), CraftingIngredient.of(FTBICItems.MV_CABLE.get()), CraftingIngredient.of(FTBICItems.MV_CABLE.get())),
		FTBIC_HV(MoreImmersiveWires.FTBIC_WIRE_HV, CraftingIngredient.of(FTBICItems.RUBBER_SHEET.get()), CraftingIngredient.of(Tags.Items.INGOTS_GOLD), CraftingIngredient.of(FTBICItems.HV_CABLE.get()), CraftingIngredient.of(FTBICItems.HV_CABLE.get())),
		FTBIC_EV(MoreImmersiveWires.FTBIC_WIRE_EV, CraftingIngredient.of(FTBICItems.RUBBER_SHEET.get()), CraftingIngredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/enderium"))), CraftingIngredient.of(FTBICItems.EV_CABLE.get()), CraftingIngredient.of(FTBICItems.EV_CABLE.get())),
		FTBIC_IV(MoreImmersiveWires.FTBIC_WIRE_IV, CraftingIngredient.of(Tags.Items.GLASS), CraftingIngredient.of(FTBICWireDefinition.ENERGY_ALLOY.get()), CraftingIngredient.of(FTBICItems.IV_CABLE.get()), CraftingIngredient.of(FTBICItems.IV_CABLE.get())),
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
					wire.simple().getWireCoils().toArray(WireInfo[]::new),
					wire.simple().getConnectors().toArray(ConnectorInfo[]::new),
					wire.simple().getRelays().toArray(RelayInfo[]::new),
					new CraftingIngredient[] {baseItem},
					new CraftingIngredient[] {coreItem},
					new CraftingIngredient[] {cableItem},
					new CraftingIngredient[] {wireItem},
					wire.modid
					);
		}

		private WireRecipe(Wire wire, CraftingIngredient[] baseItem,
				CraftingIngredient[] coreItem, CraftingIngredient[] cableItem) {
			this(
					wire.simple().getWireCoils().toArray(WireInfo[]::new),
					wire.simple().getConnectors().toArray(ConnectorInfo[]::new),
					wire.simple().getRelays().toArray(RelayInfo[]::new),
					baseItem, coreItem, cableItem, cableItem,
					wire.modid
					);
		}

		private WireRecipe(Wire wire, CraftingIngredient[] baseItem,
				CraftingIngredient[] coreItem, CraftingIngredient[] cableItem, CraftingIngredient[] wireItem) {
			this(
					new WireInfo[] {wire.simple().getWireCoils().get(0)},
					new ConnectorInfo[] {wire.simple().getConnectors().get(0)},
					new RelayInfo[] {wire.simple().getRelays().get(0)},
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
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		for (WireRecipe wr : WireRecipe.values()) {
			ConditionalRecipeUtil.builder().addCondition(new ModLoadedCondition(wr.modid)).addRecipe(cc -> {
				for (int i = 0; i < wr.wire.length; i++) {
					WireInfo w = wr.wire[i];
					ShapedRecipeBuilder.shaped(w.getCoilItem().get(), 4)
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
						ShapedRecipeBuilder.shaped(r.getRelayBlock().get(), 8)
						.pattern(" c ")
						.pattern("bcb")
						.pattern("bcb")
						.define('b', wr.baseItem[i].ingredient())
						.define('c', wr.coreItem[i].ingredient())
						.unlockedBy(r.getName() + "_relay_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(wr.baseItem[i].predicate(), wr.coreItem[i].predicate()))
						.save(cc);
					} else {
						ShapedRecipeBuilder.shaped(r.getRelayBlock().get(), 8)
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
					ShapedRecipeBuilder.shaped(c.getConnectorBlock().get(), 4)
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
			ShapelessRecipeBuilder.shapeless(CCWireDefinition.CC_MODEM_CONNECTOR.get())
			.requires(MoreImmersiveWires.CC_WIRE.simple().CONNECTOR.get())
			.requires(Registry.ModItems.WIRED_MODEM.get())
			.unlockedBy("cc_modem_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(MoreImmersiveWires.CC_WIRE.simple().CONNECTOR.get(), Registry.ModItems.WIRED_MODEM.get()))
			.save(cc);
		}).build(consumer);

		ArcFurnaceRecipeBuilder.builder(new ItemStack(FTBICWireDefinition.ENERGY_ALLOY.get(), 4))
		.addIngredient("input", FTBICItems.ENERGY_CRYSTAL.item.get())
		.addInput(new IngredientWithSize(ItemTags.create(IETags.getIngot("electrum")), 3))
		.addCondition(new ModLoadedCondition(MoreImmersiveWires.FTBIC))
		.setEnergy(256000)
		.setTime(400)
		.build(consumer, new ResourceLocation(MoreImmersiveWires.modid, "arcfurnace/ftbic_energy_alloy"));
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
