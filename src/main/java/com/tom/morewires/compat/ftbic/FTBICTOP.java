package com.tom.morewires.compat.ftbic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fml.config.ModConfig;

import com.tom.morewires.CompatConfig;
import com.tom.morewires.compat.top.BarConfig;
import com.tom.morewires.compat.top.ProbeInfo;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;

public class FTBICTOP implements ProbeInfo {
	private static final BarConfig bar = new BarConfig("FTBIC TOP", "Zap");

	public FTBICTOP() {
		CompatConfig.addConfig(ModConfig.Type.COMMON, bar);
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState,
			IProbeHitData data, BlockEntity te) {
		if(te instanceof EnergyHandler h) {
			bar.addBar(probeInfo, (long) h.getEnergy(), (long) h.getEnergyCapacity(), FTBICConfig.ENERGY_FORMAT);
		}
	}
}
