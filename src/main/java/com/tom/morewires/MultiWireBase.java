package com.tom.morewires;

import java.util.Collection;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.MultiWireTypeDefinition.WireTypeSettings;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.WireType;

public class MultiWireBase extends WireType {
	protected final WireTypeSettings wireTypeSettings;

	public MultiWireBase(WireTypeSettings wireTypeSettings) {
		this.wireTypeSettings = wireTypeSettings;
	}

	@Override
	public ItemStack getWireCoil(Connection var1) {
		return new ItemStack(this.wireTypeSettings.getCoilItem().get());
	}

	@Override
	public String getUniqueName() {
		return "miw:" + this.wireTypeSettings.getName();
	}

	@Override
	public double getSlack() {
		return this.wireTypeSettings.isThickWire() ? 1.010 : 1.005;
	}

	@Override
	public double getRenderDiameter() {
		return this.wireTypeSettings.isThickWire() ? .1 : .0625;
	}

	@Override
	public int getMaxLength() {
		return this.wireTypeSettings.lengthCfg.get();
	}

	@Override
	public int getColour(Connection var1) {
		return this.wireTypeSettings.getColor();
	}

	@Override
	public String getCategory() {
		return "MODDED";
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(this.wireTypeSettings.impl().NET_ID);
	}
}