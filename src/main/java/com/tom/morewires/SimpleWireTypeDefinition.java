package com.tom.morewires;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MoreImmersiveWires.Wire;
import com.tom.morewires.WireTypeDefinition.ConnectorInfo;
import com.tom.morewires.WireTypeDefinition.RelayInfo;
import com.tom.morewires.WireTypeDefinition.WireInfo;
import com.tom.morewires.block.OnCableConnectorBlock;
import com.tom.morewires.block.RelayBlock;
import com.tom.morewires.item.ConnectorItemBlock;
import com.tom.morewires.item.WireCoilItem;
import com.tom.morewires.tile.RelayBlockEntity;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireApi;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public abstract class SimpleWireTypeDefinition<T extends BlockEntity & IImmersiveConnectable> implements WireTypeDefinition<T>, WireInfo, ConnectorInfo, RelayInfo {
	public final String name, localized;
	public final ResourceLocation NET_ID;
	public final int color;
	public WireType wireType;
	public RegistryObject<BlockEntityType<RelayBlockEntity>> RELAY_ENTITY;
	public RegistryObject<Block> RELAY;
	public RegistryObject<Item> COIL;
	public RegistryObject<Block> CONNECTOR;
	public RegistryObject<BlockEntityType<T>> CONNECTOR_ENTITY;
	private IntValue lengthCfg;
	private String modid;

	public SimpleWireTypeDefinition(String name, String localized, int color) {
		this.name = name;
		this.localized = localized;
		this.color = color;
		NET_ID = new ResourceLocation(MoreImmersiveWires.modid, name + "_network");
	}

	@Override
	public void init() {
		RELAY = MoreImmersiveWires.blockWithItem(name + "_relay", () -> new RelayBlock<>(RELAY_ENTITY), b -> new ConnectorItemBlock(b, new Item.Properties(), this, null));
		COIL = MoreImmersiveWires.registerItem(name + "_coil", () -> new WireCoilItem(wireType, this));
		RELAY_ENTITY = MoreImmersiveWires.blockEntity(name + "_relay.tile", this::createRelayBE, RELAY);
		CONNECTOR = MoreImmersiveWires.blockWithItem(name + "_connector", () -> this.makeBlock(CONNECTOR_ENTITY), this::makeItemBlock);
		CONNECTOR_ENTITY = MoreImmersiveWires.blockEntity(name + "_connector.tile", this::createBE, CONNECTOR);
		wireType = createWire();
		WireApi.registerWireType(wireType);
		LocalNetworkHandler.register(NET_ID, createLocalHandler());
	}

	protected WireBase createWire() {
		return new WireBase();
	}

	public class WireBase extends WireType {

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
			return isThickWire() ? 1.010 : 1.005;
		}

		@Override
		public double getRenderDiameter() {
			return isThickWire() ? .1 : .0625;
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
			return ImmutableList.of(NET_ID);
		}
	}

	@Override
	public void setup(Wire w) {
		WireApi.registerFeedthroughForWiretype(wireType,
				new ResourceLocation(MoreImmersiveWires.modid, "block/connector/connector_" + name),
				new double[]{0, 4, 8, 12}, isTallConnector() ? 0.875F : 0.75f,
						RELAY.get().defaultBlockState());
		MoreImmersiveWires.WIRE_TYPES.put(RELAY_ENTITY.get(), this);
		this.modid = w.modid;
	}

	@Override
	public void config(Builder builder) {
		builder.comment(localized + " Cable Stettings").translation("config.moreimmersivewires." + name + ".settings").push(name);
		lengthCfg = builder.comment(localized + " Cable Max Length").
				translation("config.moreimmersivewires." + name + ".maxlen").defineInRange(name + "MaxLen", 16, 4, 256);
		builder.pop();
	}

	protected RelayBlockEntity createRelayBE(BlockPos pos, BlockState state) {
		return new RelayBlockEntity(RELAY_ENTITY.get(), pos, state);
	}

	@Override
	public boolean isMatchingWireType(WireType wt) {
		return wt == wireType;
	}

	protected abstract ILocalHandlerConstructor createLocalHandler();

	@Override
	public boolean isTallConnector() {
		return false;
	}

	@Override
	public boolean isThickWire() {
		return isTallConnector();
	}

	@Override
	public boolean isTallRelay() {
		return isTallConnector();
	}

	@Override
	public boolean isExTallRelay() {
		return false;
	}

	@Override
	public List<? extends WireInfo> getWireCoils() {
		return Collections.singletonList(this);
	}

	public abstract T createBE(BlockPos pos, BlockState state);

	public Block makeBlock(RegistryObject<BlockEntityType<T>> type) {
		return new OnCableConnectorBlock<>(type, this::isCable);
	}

	public abstract boolean isCable(BlockGetter level, BlockPos pos);

	public Item makeItemBlock(Block block) {
		return new ConnectorItemBlock(block, new Item.Properties(), this, true);
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
	public String getLocalized() {
		return localized;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public RegistryObject<Item> getCoilItem() {
		return COIL;
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
	public RegistryObject<Block> getRelayBlock() {
		return RELAY;
	}

	@Override
	public RegistryObject<Block> getConnectorBlock() {
		return CONNECTOR;
	}

	@Override
	public String getName() {
		return name;
	}
}
