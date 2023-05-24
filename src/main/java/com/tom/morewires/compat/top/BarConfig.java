package com.tom.morewires.compat.top;

import static mcjty.theoneprobe.api.TextStyleClass.PROGRESS;

import net.minecraft.network.chat.Component;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import com.tom.morewires.CompatConfig.ConfigHandler;

import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.items.IEnumConfig;

public class BarConfig implements ConfigHandler {
	public int barFilledColor = 0xffdd0000;
	public int barAlternateFilledColor = 0xff430000;
	public int barBorderColor = 0xff555555;
	public int barMode;
	public IEnumConfig<NumberFormat> barFormat;
	private IntValue defaultBarMode;
	private ConfigValue<String> cfgBarFilledColor;
	private ConfigValue<String> cfgBarAlternateFilledColor;
	private ConfigValue<String> cfgBarBorderColor;
	private final String groupName, type;

	public BarConfig(String groupName, String type) {
		this.groupName = groupName;
		this.type = type;
	}

	@Override
	public void build(Builder builder) {
		builder.push(groupName);
		defaultBarMode = builder.comment("How to display " + type + "s: 0 = do not show, 1 = show in a bar, 2 = show as text")
				.defineInRange("showBar", 1, 0, 2);

		barFormat = addEnumConfig(builder, "zapFormat", "Format for displaying " + type + "s",
				NumberFormat.COMPACT, NumberFormat.COMMAS, NumberFormat.COMPACT, NumberFormat.FULL, NumberFormat.NONE);

		cfgBarFilledColor = builder
				.comment("Color for the " + type + " bar")
				.define("barFilledColor", Integer.toHexString(barFilledColor));
		cfgBarAlternateFilledColor = builder
				.comment("Alternate color for the " + type + " bar")
				.define("barAlternateFilledColor", Integer.toHexString(barAlternateFilledColor));
		cfgBarBorderColor = builder
				.comment("Color for the " + type + " bar border")
				.define("barBorderColor", Integer.toHexString(barBorderColor));

		builder.pop();
	}

	@Override
	public void reload() {
		barFilledColor = parseColor(cfgBarFilledColor.get());
		barAlternateFilledColor = parseColor(cfgBarAlternateFilledColor.get());
		barBorderColor = parseColor(cfgBarBorderColor.get());
		barMode = defaultBarMode.get();
	}

	private static <T extends Enum<T>> IEnumConfig<T> addEnumConfig(Builder builder, String path, String comment,
			T def, T... values) {
		ConfigValue<String> configValue = builder.comment(comment).define(path, def.name());
		return new IEnumConfig<>() {
			@Override
			public T get() {
				String s = configValue.get();
				for (T value : values) {
					if (value.name().equals(s)) {
						return value;
					}
				}
				return null;
			}

			@Override
			public T getDefault() {
				String s = configValue.getDefault();
				for (T value : values) {
					if (value.name().equals(s)) {
						return value;
					}
				}
				return null;
			}
		};
	}

	private static int parseColor(String col) {
		try {
			return (int) Long.parseLong(col, 16);
		} catch (NumberFormatException e) {
			System.out.println("Config.parseColor");
			return 0;
		}
	}

	public void addBar(IProbeInfo probeInfo, long energy, long cap, Component suffix) {
		if(barMode == 1) {
			probeInfo.progress(energy, cap,
					probeInfo.defaultProgressStyle().suffix(suffix).filledColor(barFilledColor)
					.alternateFilledColor(barAlternateFilledColor).borderColor(barBorderColor)
					.numberFormat(barFormat.get()));
		} else if(barMode == 2) {
			probeInfo.text(CompoundText.create().style(PROGRESS).text(suffix.copy().append(": " + ElementProgress.format(energy, barFormat.get(), suffix))));
		}
	}
}
