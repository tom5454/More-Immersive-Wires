package com.tom.morewires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires.Wire;
import com.tom.morewires.WireTypeDefinition.ConnectorInfo;
import com.tom.morewires.WireTypeDefinition.RelayInfo;
import com.tom.morewires.block.OnCableConnectorBlock;
import com.tom.morewires.block.RelayBlock;
import com.tom.morewires.item.ConnectorItemBlock;
import com.tom.morewires.item.MultiCoilItem;
import com.tom.morewires.item.MultiCoilItem.MultiWireInfo;
import com.tom.morewires.item.MultiCoilItem.MultiWireInfo.ConnectorTypeInfo;
import com.tom.morewires.tile.RelayBlockEntity;

import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireApi;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public abstract class MultiWireTypeDefinition<T extends BlockEntity & IImmersiveConnectable> implements WireTypeDefinition<T>, ConnectorInfo, RelayInfo {
	public final String name, pf, localized;
	public final ResourceLocation NET_ID;
	public RegistryObject<BlockEntityType<RelayBlockEntity>> RELAY_ENTITY;
	public RegistryObject<Block> RELAY;
	public RegistryObject<Block> CONNECTOR;
	public RegistryObject<BlockEntityType<T>> CONNECTOR_ENTITY;
	private String modid;
	protected List<WireTypeSettings> wires;
	protected Set<WireType> wireTypes;

	public MultiWireTypeDefinition(String pf, String name, String localized) {
		this.name = pf + "_" + name;
		this.pf = pf;
		this.localized = localized;
		NET_ID = new ResourceLocation(MoreImmersiveWires.modid, this.name + "_network");
	}

	@Override
	public void init() {
		wires = new ArrayList<>();
		wires(id -> {
			WireTypeSettings w = new WireTypeSettings(this, name + "_" + id, pf + "_" + id);
			wires.add(w);
			return w;
		});
		RELAY = MoreImmersiveWires.blockWithItem(name + "_relay", () -> new RelayBlock<>(RELAY_ENTITY), b -> new ConnectorItemBlock(b, new Item.Properties().tab(MoreImmersiveWires.MOD_TAB), this, null));
		RELAY_ENTITY = MoreImmersiveWires.blockEntity(name + "_relay.tile", this::createRelayBE, RELAY);
		CONNECTOR = MoreImmersiveWires.blockWithItem(name + "_connector", () -> this.makeBlock(CONNECTOR_ENTITY), this::makeItemBlock);
		CONNECTOR_ENTITY = MoreImmersiveWires.blockEntity(name + "_connector.tile", this::createBE, CONNECTOR);
		wires.forEach(w -> {
			if(w.linked == null)
				w.ITEM = MoreImmersiveWires.ITEMS.register(w.itemName + "_coil", () -> new MultiCoilItem(w));
			else
				w.ITEM = w.linked.ITEM;
			w.wireType = w.createWire();
			WireApi.registerWireType(w.wireType);
		});
		LocalNetworkHandler.register(NET_ID, createLocalHandler());
		wireTypes = wires.stream().map(w -> w.wireType).collect(Collectors.toSet());
	}

	protected abstract ILocalHandlerConstructor createLocalHandler();

	public abstract T createBE(BlockPos pos, BlockState state);

	public Block makeBlock(RegistryObject<BlockEntityType<T>> type) {
		return new OnCableConnectorBlock<>(type, this::isCable);
	}

	public abstract boolean isCable(BlockGetter level, BlockPos pos);

	public Item makeItemBlock(Block block) {
		return new ConnectorItemBlock(block, new Item.Properties().tab(MoreImmersiveWires.MOD_TAB), this, true);
	}

	protected RelayBlockEntity createRelayBE(BlockPos pos, BlockState state) {
		return new RelayBlockEntity(RELAY_ENTITY.get(), pos, state);
	}

	protected abstract void wires(Function<String, WireTypeSettings> create);

	@Override
	public boolean isMatchingWireType(WireType wt) {
		return wireTypes.contains(wt);
	}

	@Override
	public void config(Builder builder) {
		builder.comment(name + " Cable Stettings").translation("config.moreimmersivewires." + name + ".settings").push(name);
		wires.forEach(w -> {
			if(w.linked == null)
				w.lengthCfg = builder.comment(w.localized + " Cable Max Length").
				translation("config.moreimmersivewires." + name + "." + w.name + ".maxlen").defineInRange(name + "_" + w.name + "MaxLen", 16, 4, 256);
			else
				w.lengthCfg = w.linked.lengthCfg;
		});
		builder.pop();
	}

	@Override
	public void setup(Wire w) {
		MoreImmersiveWires.WIRE_TYPES.put(RELAY_ENTITY.get(), this);
		wires.forEach(wts -> {
			WireApi.registerFeedthroughForWiretype(wts.wireType,
					new ResourceLocation(MoreImmersiveWires.modid, "block/connector/connector_" + name),
					new double[]{0, 4, 8, 12}, isTallRelay() ? 0.875F : 0.75f,
							RELAY.get().defaultBlockState());
			if(wts.linked != null) {
				wts.linked.type2linked.put(new ConnectorTypeInfo(CONNECTOR.get(), 0), wts);
				wts.linked.type2linked.put(new ConnectorTypeInfo(RELAY.get(), 0), wts);
			}
		});
		this.modid = w.modid;
	}

	@Override
	public List<? extends WireInfo> getWireCoils() {
		return wires;
	}

	@Override
	public List<? extends ConnectorInfo> getConnectors() {
		return Collections.singletonList(this);
	}

	@Override
	public List<? extends RelayInfo> getRelays() {
		return Collections.singletonList(this);
	}

	@Override
	public void appendHoverTextCoil(WireType type, ItemStack stack, Level world, List<Component> list,
			TooltipFlag flag) {
		appendHoverText(list);
		list.add(Component.translatable("tooltip.more_immersive_wires.mod_id", MoreImmersiveWires.MODID_NAME_LOOKUP.getOrDefault(modid, modid)).withStyle(ChatFormatting.BLUE));
	}

	@Override
	public void appendHoverTextConnector(Object id, ItemStack stack, Level world, List<Component> tooltip,
			TooltipFlag advanced) {
		appendHoverText(tooltip);
		tooltip.add(Component.translatable("tooltip.more_immersive_wires.mod_id", MoreImmersiveWires.MODID_NAME_LOOKUP.getOrDefault(modid, modid)).withStyle(ChatFormatting.BLUE));
	}

	protected void appendHoverText(List<Component> tooltip) {
	}

	@Override
	public void configReload() {

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocalized() {
		return localized;
	}

	@Override
	public boolean isTallConnector() {
		return false;
	}

	@Override
	public boolean isTallRelay() {
		return isTallConnector();
	}

	@Override
	public boolean isExTallRelay() {
		return false;
	}

	public static class WireTypeSettings implements WireInfo, MultiWireInfo {
		private final MultiWireTypeDefinition<?> impl;
		private RegistryObject<Item> ITEM;
		private boolean thick;
		private int color;
		private String localized;
		private final String name, itemName;
		protected IntValue lengthCfg;
		private WireType wireType;
		private Supplier<MultiWireBase> create = () -> new MultiWireBase(this);
		private WireTypeSettings linked;
		private boolean multiwire;
		private Map<ConnectorTypeInfo, WireTypeSettings> type2linked;
		private Map<String, WireTypeSettings> id2linked;

		public WireTypeSettings(MultiWireTypeDefinition<?> impl, String name, String itemName) {
			this.impl = impl;
			this.name = name;
			this.itemName = itemName;
		}

		public WireType createWire() {
			return create.get();
		}

		public WireTypeSettings setFactory(Function<WireTypeSettings, MultiWireBase> create) {
			this.create = () -> create.apply(this);
			return this;
		}

		public WireTypeSettings localized(String localized) {
			this.localized = localized;
			return this;
		}

		public WireTypeSettings color(int color) {
			this.color = color;
			return this;
		}

		public WireTypeSettings thick() {
			thick = true;
			return this;
		}

		public WireTypeSettings multiwire() {
			this.multiwire = true;
			this.type2linked = new HashMap<>();
			this.id2linked = new HashMap<>();
			return this;
		}

		@Override
		public boolean isThickWire() {
			return thick;
		}

		@Override
		public int getColor() {
			return color;
		}

		@Override
		public String getLocalized() {
			return localized;
		}

		@Override
		public RegistryObject<Item> getCoilItem() {
			return ITEM;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getItemName() {
			return itemName;
		}

		public WireTypeSettings linked(WireTypeSettings wts) {
			if(!wts.multiwire)throw new IllegalArgumentException();
			this.linked = wts;
			color = wts.color;
			localized = wts.localized;
			thick = wts.thick;
			create = wts.create;
			wts.id2linked.put(impl.name, this);
			return this;
		}

		public MultiWireTypeDefinition<?> impl() {
			return impl;
		}

		@Override
		public WireType getWireTypeById(String id) {
			return id2linked.getOrDefault(id, this).wireType;
		}

		@Override
		public void appendHoverTextCoil(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
			impl.appendHoverText(tooltip);
			tooltip.add(Component.translatable("tooltip.more_immersive_wires.mod_id", MoreImmersiveWires.MODID_NAME_LOOKUP.getOrDefault(impl.modid, impl.modid)).withStyle(ChatFormatting.BLUE));
		}

		@Override
		public WireType getDefWire() {
			return wireType;
		}

		@Override
		public String getTypeId(Level world, ConnectorTypeInfo cpHere) {
			return type2linked.getOrDefault(cpHere, this).impl.name;
		}

		@Override
		public boolean doDatagen() {
			return linked == null;
		}
	}

	@Override
	public RegistryObject<Block> getConnectorBlock() {
		return CONNECTOR;
	}

	@Override
	public RegistryObject<Block> getRelayBlock() {
		return RELAY;
	}
}
