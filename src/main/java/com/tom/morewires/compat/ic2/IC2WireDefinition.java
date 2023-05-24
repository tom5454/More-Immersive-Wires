package com.tom.morewires.compat.ic2;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.MultiWireBase;
import com.tom.morewires.MultiWireTypeDefinition;

import blusunrize.immersiveengineering.api.tool.IElectricEquipment;
import blusunrize.immersiveengineering.api.tool.IElectricEquipment.ElectricSource;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.IEnergyWire;
import blusunrize.immersiveengineering.api.wires.localhandlers.ILocalHandlerConstructor;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler.IShockingWire;
import ic2.core.block.cables.CableBlock;

public class IC2WireDefinition extends MultiWireTypeDefinition<IC2ConnectorBlockEntity> {
	public static RegistryObject<Item> ENERGY_ALLOY;
	public static RegistryObject<Item> UNCOMPRESSED_INSULATION;
	public static RegistryObject<Item> COMPRESSED_INSULATION;
	private final int maxPower, tier;

	public static enum Types {
		TIN(8, 0x999999, "Tin Cable", 0.025D),

		COPPER(32, 8, 0xa4632f, "Copper Cable", 0.3D),
		INS_COPPER(32, 0x222222, "Insulated Copper Cable", 0.2D),

		GOLD(128, 8, 0xfafc47, "Gold Cable", 0.5D),
		INS1_GOLD(128, 32, 0x777822, "1x Insulated Gold Cable", 0.44999998807907104D),
		INS2_GOLD(128, 0x222222, "2x Insulated Gold Cable", 0.4000000059604645D),

		BRONZE(128, 8, 0xc4742f, "Bronze Cable", 0.699999988079071D),
		INS1_BRONZE(128, 32, 0x6a3f19, "1x Insulated Bronze Cable", 0.6499999761581421D),
		INS2_BRONZE(128, 0x222222, "2x Insulated Bronze Cable", 0.6000000238418579D),

		GLASS_FIBRE(512, 0x9999aa, "Glass Fibre Cable", 0.025D),

		IRON(2048, 8, 0x717c85, "Iron Cable", 1D),
		INS1_IRON(2048, 128, 0x484f55, "1x Insulated Iron Cable", 0.95D),
		INS2_IRON(2048, 512, 0x303539, "2x Insulated Iron", 0.9D),
		INS3_IRON(2048, 2048, 0x222222, true, "4x Insulated Iron", 0.8D),

		AL(8192, 8, 0xb7bbb2, "Alunimum Cable", 1.2D),
		INS1_AL(8192, 128, 0x848780, "1x Insulated Alunimum Cable", 1.15D),
		INS2_AL(8192, 512, 0x656762, "2x Insulated Alunimum Cable", 1.1D),
		INS3_AL(8192, 2048, 0x454743, true, "4x Insulated Alunimum Cable", 1.05D),
		INS4_AL(8192, 8192, 0x222222, true, "8x Insulated Alunimum Cable", 1.0D),

		SUPER(8192, 8192, 0x999922, true, "Superconducting Cable", 0.001D),

		PLASMA(32768, 32768, 0x111111, true, "Plasma Cable", 1.2D),
		;
		private final int max, maxIns, color, dmgRange;
		private final String localized;
		private WireTypeSettings settings;
		private final boolean thick;
		private final double loss;

		private Types(int max, int color, String localized, double loss) {
			this(max, max, color, localized, loss);
		}

		private Types(int max, int maxIns, int color, String localized, double loss) {
			this(max, maxIns, color, false, localized, loss);
		}

		private Types(int max, int maxIns, int color, boolean thick, String localized, double loss) {
			this.max = max;
			this.maxIns = maxIns;
			this.color = color;
			this.thick = thick;
			this.loss = loss;
			this.localized = localized;
			this.dmgRange = Mth.log2(max);
		}

		public WireInfo getInfo() {
			return settings;
		}
	}

	private static class Normal extends MultiWireBase implements IEnergyWire {
		private double loss;
		private int max;

		public Normal(WireTypeSettings wireTypeSettings, int max, double loss) {
			super(wireTypeSettings);
			this.loss = loss;
			this.max = max;
		}

		@Override
		public double getBasicLossRate(Connection c) {
			return loss * c.getLength() / 4;
		}

		@Override
		public double getLossRate(Connection arg0, int arg1) {
			return 0;
		}

		@Override
		public int getTransferRate() {
			return max;
		}

		@Override
		public boolean shouldBurn(Connection c, double power) {
			return false;
		}
	}

	private static class Shocking extends Normal implements IShockingWire {
		private final IElectricEquipment.ElectricSource eSource;
		private final int dmg;

		public Shocking(WireTypeSettings wireTypeSettings, int max, double loss, int dmg) {
			super(wireTypeSettings, max, loss);
			eSource = new IElectricEquipment.ElectricSource(.15F * dmg);
			this.dmg = dmg;
		}

		@Override
		public double getDamageRadius() {
			return .1 * dmg;
		}

		@Override
		public float getDamageAmount(Entity e, Connection c, int energy) {
			return dmg;
		}

		@Override
		public ElectricSource getElectricSource() {
			return eSource;
		}
	}

	public static IC2WireDefinition ulv() {
		return new IC2WireDefinition("ulv", "ULV", 8, 0);
	}

	public static IC2WireDefinition lv() {
		return new IC2WireDefinition("lv", "LV", 32, 1);
	}

	public static IC2WireDefinition mv() {
		return new IC2WireDefinition("mv", "MV", 128, 2);
	}

	public static IC2WireDefinition hv() {
		return new IC2WireDefinition("hv", "HV", 512, 3);
	}

	public static IC2WireDefinition ev() {
		return new IC2WireDefinition("ev", "EV", 2048, 4);
	}

	public static IC2WireDefinition iv() {
		return new IC2WireDefinition("iv", "IV", 8192, 5);
	}

	public static IC2WireDefinition luv() {
		return new IC2WireDefinition("luv", "LuV", 32768, 6) {

			@Override
			public void init() {
				super.init();
				ENERGY_ALLOY = MoreImmersiveWires.materialItem("ic2_energy_alloy");
				UNCOMPRESSED_INSULATION = MoreImmersiveWires.materialItem("ic2_uncompressed_insulation");
				COMPRESSED_INSULATION = MoreImmersiveWires.materialItem("ic2_compressed_insulation");
			}
		};
	}

	public IC2WireDefinition(String name, String localized, int maxPower, int tier) {
		super("ic2", name, localized);
		this.maxPower = maxPower;
		this.tier = tier;
	}

	@Override
	protected ILocalHandlerConstructor createLocalHandler() {
		return IC2EnergyTransferHandler::new;
	}

	@Override
	public IC2ConnectorBlockEntity createBE(BlockPos pos, BlockState state) {
		return new IC2ConnectorBlockEntity(pos, state, maxPower, tier) {

			@Override
			public BlockEntityType<?> createType() {
				return CONNECTOR_ENTITY.get();
			}

			@Override
			public boolean canConnectCable(WireType arg0, ConnectionPoint arg1, Vec3i arg2) {
				return isMatchingWireType(arg0);
			}

			@Override
			ResourceLocation getNetId() {
				return NET_ID;
			}
		};
	}

	@Override
	public Block makeBlock(RegistryObject<BlockEntityType<IC2ConnectorBlockEntity>> type) {
		return new IC2ConnectorBlock(type, this::isCable);
	}

	@Override
	public boolean isCable(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof CableBlock;
	}

	@Override
	protected void wires(Function<String, WireTypeSettings> create) {
		for (Types t : Types.values()) {
			if(t.max >= maxPower) {
				WireTypeSettings wts = create.apply(t.name().toLowerCase(Locale.ROOT));
				if(t.settings != null)wts.linked(t.settings);
				else {
					t.settings = wts.multiwire();
					wts.color(t.color).localized(t.localized);
					if(t.thick)wts.thick();
				}
				if(t.maxIns >= maxPower) {
					wts.setFactory(w -> new Normal(w, t.max, t.loss / maxPower));
				} else {
					wts.setFactory(w -> new Shocking(w, t.max, t.loss / maxPower, t.dmgRange));
				}
			}
		}
	}

	@Override
	public void addTranslations(BiConsumer<String, String> addTranslation) {
		addTranslation.accept("tooltip.more_immersive_wires.network_type." + name, localized);
	}

	@Override
	public boolean isTallConnector() {
		return maxPower >= 512;
	}

	@Override
	public boolean isExTallRelay() {
		return maxPower >= 512;
	}

	@Override
	public boolean datagenConnectorBlock() {
		return false;
	}
}
