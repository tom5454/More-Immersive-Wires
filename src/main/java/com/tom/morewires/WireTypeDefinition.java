package com.tom.morewires;

import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires.Wire;

import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireType;

public interface WireTypeDefinition<T extends BlockEntity & IImmersiveConnectable> {
	void init();
	boolean isMatchingWireType(WireType wt);
	void config(Builder builder);
	void setup(Wire wire);
	List<? extends WireInfo> getWireCoils();
	List<? extends ConnectorInfo> getConnectors();
	List<? extends RelayInfo> getRelays();
	void appendHoverTextCoil(WireType type, ItemStack stack, Level world, List<Component> list, TooltipFlag flag);
	void appendHoverTextConnector(Object id, ItemStack stack, Level world, List<Component> tooltip, TooltipFlag advanced);
	void configReload();
	String getName();
	String getLocalized();

	public static interface WireInfo {
		boolean isThickWire();
		int getColor();
		String getLocalized();
		RegistryObject<Item> getCoilItem();
		String getName();

		default String getItemName() {
			return getName();
		}

		default boolean doDatagen() {
			return true;
		}
	}

	public static interface ConnectorInfo {
		boolean isTallConnector();
		String getLocalized();
		RegistryObject<Block> getConnectorBlock();
		String getName();

		default boolean datagenConnectorBlock() { return true; }
	}

	public static interface RelayInfo {
		boolean isExTallRelay();
		boolean isTallRelay();
		String getLocalized();
		RegistryObject<Block> getRelayBlock();
		String getName();
		boolean isMatchingWireType(WireType cableType);
	}

	default void addTranslations(BiConsumer<String, String> addTranslation) {}
}
