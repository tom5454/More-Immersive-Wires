package com.tom.morewires.compat.ftbic;

import static mcjty.theoneprobe.api.TextStyleClass.PROGRESS;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.config.ModConfig;

import com.tom.morewires.CompatConfig;
import com.tom.morewires.CompatConfig.ConfigHandler;
import com.tom.morewires.compat.top.ProbeInfo;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.items.IEnumConfig;

public class FTBICTOP implements ProbeInfo {
	public static int zapbarFilledColor = 0xffdd0000;
	public static int zapbarAlternateFilledColor = 0xff430000;
	public static int zapbarBorderColor = 0xff555555;
	public static int zapMode;
	public static IEnumConfig<NumberFormat> zapFormat;
	private static IntValue defaultZapMode;
	private static ConfigValue<String> cfgZapbarFilledColor;
	private static ConfigValue<String> cfgZapbarAlternateFilledColor;
	private static ConfigValue<String> cfgZapbarBorderColor;

	public FTBICTOP() {
		CompatConfig.addConfig(ModConfig.Type.COMMON, new CompatConfig.ConfigHandler() {

			@Override
			public void reload() {
				reloadConfig();
			}

			@Override
			public void build(Builder builder) {
				config(builder);
			}
		});
	}

	public static void config(Builder builder) {
		builder.push("FTBIC TOP");
		defaultZapMode = builder.comment("How to display FTBIC Zaps: 0 = do not show, 1 = show in a bar, 2 = show as text")
				.defineInRange("showZaps", 1, 0, 2);

		zapFormat = addEnumConfig(builder, "zapFormat", "Format for displaying Zaps",
				NumberFormat.COMPACT, NumberFormat.COMMAS, NumberFormat.COMPACT, NumberFormat.FULL, NumberFormat.NONE);

		cfgZapbarFilledColor = builder
				.comment("Color for the Zap bar")
				.define("zapbarFilledColor", Integer.toHexString(zapbarFilledColor));
		cfgZapbarAlternateFilledColor = builder
				.comment("Alternate color for the Zap bar")
				.define("zapbarAlternateFilledColor", Integer.toHexString(zapbarAlternateFilledColor));
		cfgZapbarBorderColor = builder
				.comment("Color for the Zap bar border")
				.define("zapbarBorderColor", Integer.toHexString(zapbarBorderColor));

		builder.pop();
	}

	public static void reloadConfig() {
		zapbarFilledColor = parseColor(cfgZapbarFilledColor.get());
		zapbarAlternateFilledColor = parseColor(cfgZapbarAlternateFilledColor.get());
		zapbarBorderColor = parseColor(cfgZapbarBorderColor.get());
		zapMode = defaultZapMode.get();
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState,
			IProbeHitData data, BlockEntity te) {
		if(te instanceof EnergyHandler h) {
			if(zapMode == 1) {
				probeInfo.progress((long) h.getEnergy(), (long) h.getEnergyCapacity(),
						probeInfo.defaultProgressStyle().suffix(FTBICConfig.ENERGY_FORMAT).filledColor(zapbarFilledColor)
						.alternateFilledColor(zapbarAlternateFilledColor).borderColor(zapbarBorderColor)
						.numberFormat(zapFormat.get()));
			} else if(zapMode == 2) {
				probeInfo.text(CompoundText.create().style(PROGRESS).text(FTBICConfig.ENERGY_FORMAT.copy().append(": " + ElementProgress.format((long) h.getEnergy(), zapFormat.get(), FTBICConfig.ENERGY_FORMAT))));
			}
		}
	}

	private static <T extends Enum<T>> IEnumConfig<T> addEnumConfig(Builder builder, String path, String comment,
			T def, T... values) {
		ConfigValue<String> configValue = builder.comment(comment).define(path, def.name());
		return () -> {
			String s = configValue.get();
			for (T value : values) {
				if (value.name().equals(s)) {
					return value;
				}
			}
			return null;
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
}
