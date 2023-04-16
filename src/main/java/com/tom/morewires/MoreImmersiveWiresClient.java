package com.tom.morewires;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraftforge.common.MinecraftForge;

public class MoreImmersiveWiresClient {

	public static void preInit() {
	}

	public static void clientSetup() {
		MinecraftForge.EVENT_BUS.register(MoreImmersiveWiresClient.class);
	}

	public static void tooltip(String key, List<Component> tooltip, Object... args) {
		tooltip(key, true, tooltip, args);
	}

	public static void tooltip(String key, boolean addShift, List<Component> tooltip, Object... args) {
		if(Screen.hasShiftDown()) {
			String[] sp = I18n.get("tooltip.more_immersive_wires." + key, args).split("\\\\");
			for (int i = 0; i < sp.length; i++) {
				tooltip.add(new TextComponent(sp[i]));
			}
		} else if(addShift) {
			tooltip.add(new TranslatableComponent("tooltip.more_immersive_wires.hold_shift_for_info").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
		}
	}
}
