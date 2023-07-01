package com.tom.morewires.data;

import java.util.HashMap;
import java.util.Map;
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
import com.tom.morewires.compat.ic2.IC2WireDefinition;

import dan200.computercraft.shared.Registry;

import appeng.core.definitions.AEBlocks;
import appeng.datagen.providers.tags.ConventionTags;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.ArcFurnaceRecipeBuilder;
import dev.ftb.mods.ftbic.item.FTBICItems;
import ic2.core.platform.registries.IC2Items;
import ic2.core.platform.registries.IC2Tags;

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

		IC2_ULV(MoreImmersiveWires.IC2_WIRE_ULV, IC2WireDefinition.Types.TIN.getInfo(), CraftingIngredient.of(IC2Tags.RUBBER), CraftingIngredient.of(IC2Tags.INGOT_TIN), CraftingIngredient.of(IC2Items.TIN_CABLE)),
		IC2_LVI(MoreImmersiveWires.IC2_WIRE_LV, IC2WireDefinition.Types.INS_COPPER.getInfo(), CraftingIngredient.of(IC2Tags.RUBBER), CraftingIngredient.of(Tags.Items.INGOTS_COPPER), CraftingIngredient.of(IC2Items.COPPER_CABLE_1X_INSULATED)),
		IC2_LV(MoreImmersiveWires.IC2_WIRE_LV, new WireInfo[] {IC2WireDefinition.Types.COPPER.getInfo()}, new CraftingIngredient[] {CraftingIngredient.of(IC2Items.COPPER_CABLE)}),

		IC2_MVI(MoreImmersiveWires.IC2_WIRE_MV, IC2WireDefinition.Types.INS2_GOLD.getInfo(), CraftingIngredient.of(IC2Tags.RUBBER), CraftingIngredient.of(Tags.Items.INGOTS_GOLD), CraftingIngredient.of(IC2Items.GOLD_CABLE_2X_INSULATED)),
		IC2_MV(MoreImmersiveWires.IC2_WIRE_MV, new WireInfo[] {IC2WireDefinition.Types.GOLD.getInfo(), IC2WireDefinition.Types.INS1_GOLD.getInfo()}, new CraftingIngredient[] {CraftingIngredient.of(IC2Items.GOLD_CABLE), CraftingIngredient.of(IC2Items.GOLD_CABLE_1X_INSULATED)}),
		IC2_MVI2(MoreImmersiveWires.IC2_WIRE_MV, IC2WireDefinition.Types.INS2_BRONZE.getInfo(), CraftingIngredient.of(IC2Tags.RUBBER), CraftingIngredient.of(IC2Tags.INGOT_BRONZE), CraftingIngredient.of(IC2Items.BRONZE_CABLE_2X_INSULATED)),
		IC2_MV2(MoreImmersiveWires.IC2_WIRE_MV, new WireInfo[] {IC2WireDefinition.Types.BRONZE.getInfo(), IC2WireDefinition.Types.INS1_BRONZE.getInfo()}, new CraftingIngredient[] {CraftingIngredient.of(IC2Items.BRONZE_CABLE), CraftingIngredient.of(IC2Items.BRONZE_CABLE_1X_INSULATED)}),

		IC2_HV(MoreImmersiveWires.IC2_WIRE_HV, IC2WireDefinition.Types.GLASS_FIBRE.getInfo(), CraftingIngredient.of(Tags.Items.GLASS), CraftingIngredient.of(IC2WireDefinition.ENERGY_ALLOY.get()), CraftingIngredient.of(IC2Items.GLASSFIBER_CABLE)),

		IC2_EVI(MoreImmersiveWires.IC2_WIRE_EV, IC2WireDefinition.Types.INS3_IRON.getInfo(), CraftingIngredient.of(IC2WireDefinition.COMPRESSED_INSULATION.get()), CraftingIngredient.of(IC2Tags.INGOT_REFINED_IRON), CraftingIngredient.of(IC2Items.IRON_CABLE_4X_INSULATED)),
		IC2_EV(MoreImmersiveWires.IC2_WIRE_EV, new WireInfo[] {IC2WireDefinition.Types.IRON.getInfo(), IC2WireDefinition.Types.INS1_IRON.getInfo(), IC2WireDefinition.Types.INS2_IRON.getInfo()}, new CraftingIngredient[] {CraftingIngredient.of(IC2Items.IRON_CABLE), CraftingIngredient.of(IC2Items.IRON_CABLE_1X_INSULATED), CraftingIngredient.of(IC2Items.IRON_CABLE_2X_INSULATED)}),

		IC2_IVI(MoreImmersiveWires.IC2_WIRE_IV, IC2WireDefinition.Types.INS4_AL.getInfo(), CraftingIngredient.of(IC2WireDefinition.COMPRESSED_INSULATION.get()), CraftingIngredient.of(IC2Tags.INGOT_ALUMINIUM), CraftingIngredient.of(IC2Items.ALUMINIUM_CABLE_8X_INSULATED)),
		IC2_IV(MoreImmersiveWires.IC2_WIRE_IV, new WireInfo[] {IC2WireDefinition.Types.AL.getInfo(), IC2WireDefinition.Types.INS1_AL.getInfo(), IC2WireDefinition.Types.INS2_AL.getInfo(), IC2WireDefinition.Types.INS3_AL.getInfo(), IC2WireDefinition.Types.SUPER.getInfo()}, new CraftingIngredient[] {CraftingIngredient.of(IC2Items.ALUMINIUM_CABLE), CraftingIngredient.of(IC2Items.ALUMINIUM_CABLE_1X_INSULATED), CraftingIngredient.of(IC2Items.ALUMINIUM_CABLE_2X_INSULATED), CraftingIngredient.of(IC2Items.ALUMINIUM_CABLE_4X_INSULATED), CraftingIngredient.of(IC2Items.SUPER_CABLE)}),

		IC2_LUV(MoreImmersiveWires.IC2_WIRE_LUV, IC2WireDefinition.Types.PLASMA.getInfo(), CraftingIngredient.of(IC2Items.CARBON_PLATE), CraftingIngredient.of(IC2Tags.INGOT_REFINED_IRON), CraftingIngredient.of(IC2Items.PLASMA_CABLE)),
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
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
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

		ArcFurnaceRecipeBuilder.builder(new ItemStack(IC2WireDefinition.ENERGY_ALLOY.get(), 4))
		.addIngredient("input", Tags.Items.GEMS_DIAMOND)
		.addInput(new IngredientWithSize(Tags.Items.DUSTS_REDSTONE, 3))
		.addInput(Ingredient.of(IC2Tags.INGOT_SILVER))
		.addCondition(new ModLoadedCondition(MoreImmersiveWires.IC2))
		.setEnergy(256000)
		.setTime(400)
		.build(consumer, new ResourceLocation(MoreImmersiveWires.modid, "arcfurnace/ic2_energy_alloy"));

		ConditionalRecipeUtil.builder().addCondition(new ModLoadedCondition(MoreImmersiveWires.IC2)).addRecipe(cc -> {
			ShapedRecipeBuilderEx.shaped(IC2WireDefinition.UNCOMPRESSED_INSULATION.get(), 2)
			.pattern(" r ")
			.pattern("rcr")
			.pattern(" r ")
			.define('r', IC2Tags.RUBBER)
			.define('c', IC2Items.CARBON_FIBER)
			.unlockedBy("ic2_uins_connector_unlock", InventoryChangeTrigger.TriggerInstance.hasItems(IC2Items.CARBON_FIBER))
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

	public static class ShapedRecipeBuilderEx extends ShapedRecipeBuilder {
		static Map<ItemLike, Integer> dupHandler = new HashMap<>();

		public ShapedRecipeBuilderEx(ItemLike p_126114_, int p_126115_) {
			super(p_126114_, p_126115_);
		}

		public static ShapedRecipeBuilderEx shaped(ItemLike p_126117_) {
			return shaped(p_126117_, 1);
		}

		public static ShapedRecipeBuilderEx shaped(ItemLike p_126119_, int p_126120_) {
			return new ShapedRecipeBuilderEx(p_126119_, p_126120_);
		}

		@Override
		public void save(Consumer<FinishedRecipe> p_176499_) {
			ResourceLocation r = ForgeRegistries.ITEMS.getKey(getResult());
			int d = dupHandler.merge(getResult(), 1, Integer::sum);
			save(p_176499_, new ResourceLocation(r.getNamespace(), r.getPath() + (d > 1 ? d : "")));
		}
	}
}
