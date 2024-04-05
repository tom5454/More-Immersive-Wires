package com.tom.morewires;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
	public static class Server {

		private Server(ModConfigSpec.Builder builder) {
			builder.comment("Wire Settings").translation("config.moreimmersivewires.wires").push("wires");
			MoreImmersiveWires.ALL_WIRES.forEach(w -> w.config(builder));
			builder.pop();
			CompatConfig.build(ModConfig.Type.SERVER, builder);
		}
	}

	public static class Common {

		public Common(ModConfigSpec.Builder builder) {
			builder.comment("IMPORTANT NOTICE:",
					"THIS IS ONLY THE COMMON CONFIG. It does not contain all the values adjustable for More Immersive Wires",
					"The settings have been moved to more_immersive_wires-server.toml",
					"That file is PER WORLD, meaning you have to go into 'saves/<world name>/serverconfig' to adjust it. Those changes will then only apply for THAT WORLD.",
					"You can then take that config file and put it in the 'defaultconfigs' folder to make it apply automatically to all NEW worlds you generate FROM THERE ON.",
					"This may appear confusing to many of you, but it is a new sensible way to handle configuration, because the server configuration is synced when playing multiplayer.").
			define("importantInfo", true);

			CompatConfig.build(ModConfig.Type.COMMON, builder);
		}
	}

	public static class Client {

		public Client(ModConfigSpec.Builder builder) {
			CompatConfig.build(ModConfig.Type.CLIENT, builder);
		}
	}

	static final ModConfigSpec commonSpec;
	public static final Common COMMON;
	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	static final ModConfigSpec serverSpec;
	public static final Server SERVER;
	static {
		final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
		serverSpec = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	static final ModConfigSpec clientSpec;
	public static final Client CLIENT;
	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	private static void load(ModConfig config) {
		if(config.getType() == ModConfig.Type.SERVER)
			MoreImmersiveWires.ALL_WIRES.forEach(w -> w.wireTypeDef.configReload());
		CompatConfig.reload(config.getType());
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		MoreImmersiveWires.LOGGER.info("Loaded More Immersive Wires config file {}", configEvent.getConfig().getFileName());
		load(configEvent.getConfig());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		MoreImmersiveWires.LOGGER.info("More Immersive Wires config just got changed on the file system!");
		load(configEvent.getConfig());
	}
}
