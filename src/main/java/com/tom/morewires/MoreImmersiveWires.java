package com.tom.morewires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.block.RelayBlock;
import com.tom.morewires.compat.ae.AEDenseWireDefinition;
import com.tom.morewires.compat.ae.AEWireDefinition;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.id.IntegratedDynamicsWireDefinition;
import com.tom.morewires.compat.rs.RSWireDefinition;
import com.tom.morewires.item.WireCoilItem;
import com.tom.morewires.tile.RelayBlockEntity;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.WireApi;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MoreImmersiveWires.modid)
public class MoreImmersiveWires {
	public static final String modid = "more_immersive_wires";

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modid);
	public static final Map<BlockEntityType<?>, Wire> WIRE_TYPES = new HashMap<>();
	public static final List<Wire> ALL_WIRES = new ArrayList<>();

	public static final String AE = "ae2";
	public static final String RS = "refinedstorage";
	public static final String ID = "integrateddynamics";
	public static final String CC = "computercraft";

	public static final Wire AE_WIRE = new Wire("ae", AE, 0x331166, "ME Glass Cable", false, () -> AEWireDefinition::new);
	public static final Wire AE_DENSE_WIRE = new Wire("ae_dense", AE, 0x220055, "ME Dense Cable", true, () -> AEDenseWireDefinition::new);
	public static final Wire RS_WIRE = new Wire("rs", RS, 0x222222, "RS Cable", false, () -> RSWireDefinition::new);
	public static final Wire ID_WIRE = new Wire("id", ID, 0x335566, "Logic Cable", false, () -> IntegratedDynamicsWireDefinition::new);
	public static final Wire CC_WIRE = new Wire("cc", CC, 0x888888, "Networking Cable", false, () -> CCWireDefinition::new);

	public static class Wire {
		public final String name, localized, modid;
		public final int color;
		public final boolean tall;
		public WireType wireType;
		public RegistryObject<BlockEntityType<RelayBlockEntity>> ENTITY;
		public RegistryObject<Block> RELAY;
		public RegistryObject<Item> COIL;
		public RegistryObject<Block> CONNECTOR;
		public RegistryObject<BlockEntityType<?>> CONNECTOR_ENTITY;
		public WireTypeDefinition<?> wireTypeDef;
		private IntValue lengthCfg;
		private Supplier<Supplier<WireTypeDefinition<?>>> createDef;

		public Wire(String name, String modid, int color, String localized, boolean tall, Supplier<Supplier<WireTypeDefinition<?>>> createDef) {
			this.name = name;
			this.color = color;
			this.localized = localized;
			this.tall = tall;
			this.createDef = createDef;
			this.modid = modid;
			if(ModList.get().isLoaded(modid))ALL_WIRES.add(this);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void init() {
			wireTypeDef = createDef.get().get();
			RELAY = blockWithItem(name + "_relay", () -> new RelayBlock<>(ENTITY), b -> new BlockItemIE(b, new Item.Properties().tab(MOD_TAB)));
			COIL = ITEMS.register(name + "_coil", () -> new WireCoilItem(wireType));
			ENTITY = blockEntity(name + "_relay.tile", (p, s) -> new RelayBlockEntity(this, p, s), RELAY);
			CONNECTOR = blockWithItem(name + "_connector", () -> wireTypeDef.makeBlock(CONNECTOR_ENTITY), wireTypeDef::makeItemBlock);
			CONNECTOR_ENTITY = (RegistryObject) blockEntity(name + "_connector.tile", wireTypeDef::createBE, CONNECTOR);
			wireType = new WireType() {

				@Override
				public ItemStack getWireCoil(Connection var1) {
					return new ItemStack(COIL.get());
				}

				@Override
				public String getUniqueName() {
					return "miw:" + name;
				}

				@Override
				public double getSlack() {
					return tall ? 1.010 : 1.005;
				}

				@Override
				public double getRenderDiameter() {
					return tall ? .1 : .0625;
				}

				@Override
				public int getMaxLength() {
					return lengthCfg.get();
				}

				@Override
				public int getColour(Connection var1) {
					return color;
				}

				@Override
				public String getCategory() {
					return "MODDED";
				}

				@Override
				public Collection<ResourceLocation> getRequestedHandlers() {
					return wireTypeDef.getRequestedHandlers();
				}
			};
			WireApi.registerWireType(wireType);
			wireTypeDef.init();
		}

		public void setup() {
			WireApi.registerFeedthroughForWiretype(wireType,
					new ResourceLocation(modid, "block/connector/connector_" + name),
					new double[]{0, 4, 8, 12}, tall ? 0.875F : 0.75f,
							RELAY.get().defaultBlockState());
			WIRE_TYPES.put(ENTITY.get(), this);
		}

		public void config(Builder builder) {
			builder.comment(localized + " Cable Stettings").translation("config.moreimmersivewires." + name + ".settings").push(name);
			lengthCfg = builder.comment(localized + " Cable Max Length").
					translation("config.moreimmersivewires." + name + ".maxlen").defineInRange(name + "MaxLen", 16, 4, 256);
			builder.pop();
		}
	}

	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public MoreImmersiveWires() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MoreImmersiveWiresClient::preInit);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
		FMLJavaModLoadingContext.get().getModEventBus().register(Config.class);

		ALL_WIRES.forEach(Wire::init);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITIES.register(bus);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("More Immersive Wires Setup starting");
		ALL_WIRES.forEach(Wire::setup);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		MoreImmersiveWiresClient.clientSetup();
	}

	public static final CreativeModeTab MOD_TAB = new CreativeModeTab("more_immersive_wires.tab") {

		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return ALL_WIRES.stream().map(w -> new ItemStack(w.COIL.get())).findFirst().orElse(new ItemStack(Items.REDSTONE));
		}
	};

	public static <B extends Block> RegistryObject<B> blockWithItem(String name, Supplier<B> create) {
		RegistryObject<B> re = BLOCKS.register(name, create);
		ITEMS.register(name, () -> new BlockItem(re.get(), new Item.Properties().tab(MOD_TAB)));
		return re;
	}

	public static <B extends Block, I extends Item> RegistryObject<B> blockWithItem(String name, Supplier<B> create, Function<Block, I> createItem) {
		RegistryObject<B> re = BLOCKS.register(name, create);
		ITEMS.register(name, () -> createItem.apply(re.get()));
		return re;
	}

	@SafeVarargs
	public static <BE extends BlockEntity> RegistryObject<BlockEntityType<BE>> blockEntity(String name, BlockEntitySupplier<? extends BE> create, RegistryObject<? extends Block>... blocks) {
		return BLOCK_ENTITIES.register(name, () -> {
			return BlockEntityType.Builder.<BE>of(create, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null);
		});
	}
}
