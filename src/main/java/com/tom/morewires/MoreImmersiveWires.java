package com.tom.morewires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.WireTypeDefinition.RelayInfo;
import com.tom.morewires.compat.ae.AEDenseWireDefinition;
import com.tom.morewires.compat.ae.AEWireDefinition;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.id.IntegratedDynamicsWireDefinition;
import com.tom.morewires.compat.rs.RSWireDefinition;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MoreImmersiveWires.modid)
public class MoreImmersiveWires {
	public static final String modid = "more_immersive_wires";

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modid);
	public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);

	public static final Map<BlockEntityType<?>, RelayInfo> WIRE_TYPES = new HashMap<>();
	public static final List<Wire> ALL_WIRES = new ArrayList<>();

	public static final String AE = "ae2";
	public static final String RS = "refinedstorage";
	public static final String ID = "integrateddynamics";
	public static final String CC = "computercraft";
	public static final String FTBIC = "ftbic";
	public static final String IC2 = "ic2";

	public static final Wire AE_WIRE = new Wire(AE, () -> AEWireDefinition::new);
	public static final Wire AE_DENSE_WIRE = new Wire(AE, () -> AEDenseWireDefinition::new);
	public static final Wire RS_WIRE = new Wire(RS, () -> RSWireDefinition::new);
	public static final Wire ID_WIRE = new Wire(ID, () -> IntegratedDynamicsWireDefinition::new);
	public static final Wire CC_WIRE = new Wire(CC, () -> CCWireDefinition::new);

	public static class Wire {
		public final String modid;
		public WireTypeDefinition<?> wireTypeDef;

		public Wire(String modid, Supplier<Supplier<WireTypeDefinition<?>>> createDef) {
			this.modid = modid;
			if(ModList.get().isLoaded(modid)) {
				ALL_WIRES.add(this);
				wireTypeDef = createDef.get().get();
			}
		}

		public void init() {
			wireTypeDef.init();
		}

		public void setup() {
			wireTypeDef.setup(this);
		}

		public void config(Builder builder) {
			wireTypeDef.config(builder);
		}

		public SimpleWireTypeDefinition<?> simple() {
			return (SimpleWireTypeDefinition<?>) wireTypeDef;
		}
	}

	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public static Map<String, String> MODID_NAME_LOOKUP = Collections.emptyMap();

	public MoreImmersiveWires() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MoreImmersiveWiresClient::preInit);

		ALL_WIRES.forEach(Wire::init);

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
		FMLJavaModLoadingContext.get().getModEventBus().register(Config.class);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITIES.register(bus);
		TAB.register(bus);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("More Immersive Wires Setup starting");
		ALL_WIRES.forEach(Wire::setup);
		MODID_NAME_LOOKUP = ModList.get().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, IModInfo::getDisplayName));
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		MoreImmersiveWiresClient.clientSetup();
	}

	public void enqueueIMC(InterModEnqueueEvent e) {
	}

	private static List<Item> tabItems = new ArrayList<>();
	public static final RegistryObject<CreativeModeTab> STORAGE_MOD_TAB = TAB.register("tab", () ->
	CreativeModeTab.builder()
	.title(Component.translatable("itemGroup.more_immersive_wires.tab"))
	.icon(() -> {
		return ALL_WIRES.stream().flatMap(w -> w.wireTypeDef.getWireCoils().stream()).map(w -> new ItemStack(w.getCoilItem().get())).findFirst().orElse(new ItemStack(Items.REDSTONE));
	})
	.displayItems((p, out) -> {
		tabItems.forEach(out::accept);
	})
	.build()
			);

	public static <I extends Item> I addItemToTab(I item) {
		tabItems.add(item);
		return item;
	}

	public static <B extends Block> RegistryObject<B> blockWithItem(String name, Supplier<B> create) {
		RegistryObject<B> re = BLOCKS.register(name, create);
		ITEMS.register(name, () -> addItemToTab(new BlockItem(re.get(), new Item.Properties())));
		return re;
	}

	public static <B extends Block, I extends Item> RegistryObject<B> blockWithItem(String name, Supplier<B> create, Function<Block, I> createItem) {
		RegistryObject<B> re = BLOCKS.register(name, create);
		ITEMS.register(name, () -> addItemToTab(createItem.apply(re.get())));
		return re;
	}

	public static RegistryObject<Item> materialItem(String name) {
		return ITEMS.register(name, () -> addItemToTab(new Item(new Item.Properties())));
	}

	@SafeVarargs
	public static <BE extends BlockEntity> RegistryObject<BlockEntityType<BE>> blockEntity(String name, BlockEntitySupplier<? extends BE> create, RegistryObject<? extends Block>... blocks) {
		return BLOCK_ENTITIES.register(name, () -> {
			return BlockEntityType.Builder.<BE>of(create, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null);
		});
	}

	public static <I extends Item> RegistryObject<Item> registerItem(String name, Supplier<I> object) {
		return ITEMS.register(name, () -> addItemToTab(object.get()));
	}
}
