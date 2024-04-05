package com.tom.morewires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CompatConfig {

	public static interface ConfigHandler {
		void build(ModConfigSpec.Builder builder);
		void reload();
	}

	private static final Map<Type, List<CompatConfig.ConfigHandler>> CONFIGS = Arrays.stream(Type.values()).collect(Collectors.toMap(Function.identity(), k -> new ArrayList<>()));

	public static void addConfig(Type cfg, CompatConfig.ConfigHandler b) {
		CONFIGS.get(cfg).add(b);
	}

	public static void build(Type cfg, ModConfigSpec.Builder builder) {
		CONFIGS.get(cfg).forEach(c -> c.build(builder));
	}

	public static void reload(Type type) {
		CONFIGS.get(type).forEach(ConfigHandler::reload);
	}
}
