package com.tom.morewires.compat.ftbic;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.MoreImmersiveWires.Wire;
import com.tom.morewires.SimpleWireTypeDefinition;
import com.tom.morewires.compat.top.TheOneProbeHandler;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.IEnergyWire;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICUtils;

public class FTBICWireDefinition extends SimpleWireTypeDefinition<FTBICConnectorBlockEntity> {
	public static final int ZAP_MULT = 10;
	public static RegistryObject<Item> ENERGY_ALLOY;
	private boolean tall, thick;
	private final Supplier<EnergyTier> tierSup;
	private EnergyTier tier;
	public int energyCapacity;

	private FTBICWireDefinition(String name, String localized, int color, boolean tall, boolean thick, Supplier<EnergyTier> tier) {
		super(name, localized, color);
		this.tall = tall;
		this.thick = thick;
		this.tierSup = tier;
	}

	static {
		if(ModList.get().isLoaded("theoneprobe")) {
			TheOneProbeHandler.add(new FTBICTOP());
		}
	}

	public static FTBICWireDefinition lv() {
		return new FTBICWireDefinition("ftbic_lv", "LV", 0x111111, false, false, () -> EnergyTier.LV);
	}

	public static FTBICWireDefinition mv() {
		return new FTBICWireDefinition("ftbic_mv", "MV", 0x222222, false, true, () -> EnergyTier.MV);
	}

	public static FTBICWireDefinition hv() {
		return new FTBICWireDefinition("ftbic_hv", "HV", 0x333311, true, false, () -> EnergyTier.HV);
	}

	public static FTBICWireDefinition ev() {
		return new FTBICWireDefinition("ftbic_ev", "EV", 0x111144, true, true, () -> EnergyTier.EV);
	}

	public static FTBICWireDefinition iv() {
		return new FTBICWireDefinition("ftbic_iv", "IV", 0xeeeeee, true, false, () -> EnergyTier.IV) {

			@Override
			public void init() {
				super.init();
				ENERGY_ALLOY = MoreImmersiveWires.materialItem("ftbic_energy_alloy");
			}
		};
	}

	@Override
	public void setup(Wire w) {
		super.setup(w);
		this.tier = this.tierSup.get();
		this.energyCapacity = Mth.floor(tier.transferRate.get() * ZAP_MULT);
	}

	@Override
	protected WireBase createWire() {
		return new FTBICWire();
	}

	private class FTBICWire extends WireBase implements IEnergyWire {

		@Override
		public int getTransferRate() {
			return energyCapacity;
		}

		@Override
		public double getBasicLossRate(Connection c) {
			return 0;
		}

		@Override
		public double getLossRate(Connection c, int transferred) {
			return 0;
		}

		@Override
		public boolean shouldBurn(Connection c, double power) {
			return false;
		}
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return EnergyTransferHandler::new;
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof CableBlock;
	}

	@Override
	public Block makeBlock(RegistryObject<BlockEntityType<FTBICConnectorBlockEntity>> type) {
		return new FTBICConnectorBlock(type, this::isCable);
	}

	@Override
	public FTBICConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new FTBICConnectorBlockEntity(this, pos, state);
	}

	@Override
	public boolean isTallConnector() {
		return tall;
	}

	@Override
	public boolean isThickWire() {
		return thick;
	}

	@Override
	public boolean isExTallRelay() {
		return tall;
	}

	@Override
	protected void appendHoverText(List<Component> list) {
		list.add(Component.translatable("ftbic.max_input", new Object[]{FTBICUtils
				.formatEnergy(this.tier.transferRate.get()).append("/t").withStyle(ChatFormatting.GRAY)})
				.withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	public void appendHoverTextConnector(Object id, ItemStack stack, Level world, List<Component> tooltip,
			TooltipFlag advanced) {
		super.appendHoverTextConnector(id, stack, world, tooltip, advanced);
		if(id != null) {
			Double feRatio = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
			if (feRatio > 0.0D) {
				tooltip.add(Component
						.translatable("ftbic.zap_to_fe_conversion", new Object[]{FTBICConfig.ENERGY_FORMAT, feRatio})
						.withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}
}
