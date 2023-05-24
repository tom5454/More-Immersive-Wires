package com.tom.morewires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

public class CompatConfig {

	public static interface ConfigHandler {
		void build(ForgeConfigSpec.Builder builder);
		void reload();
	}

	private static final Map<Type, List<CompatConfig.ConfigHandler>> CONFIGS = Arrays.stream(Type.values()).collect(Collectors.toMap(Function.identity(), k -> new ArrayList<>()));

	public static void addConfig(Type cfg, CompatConfig.ConfigHandler b) {
		CONFIGS.get(cfg).add(b);
	}

	public static void build(Type cfg, ForgeConfigSpec.Builder builder) {
		CONFIGS.get(cfg).forEach(c -> c.build(builder));
	}

	public static void reload(Type type) {
		CONFIGS.get(type).forEach(ConfigHandler::reload);
	}
}
