package com.tom.morewires.data;

import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.google.common.collect.ImmutableMap;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.block.OnCableConnectorBlock;

import dan200.computercraft.shared.peripheral.modem.wired.BlockWiredModemFull;

import blusunrize.immersiveengineering.data.blockstates.ExtendedBlockstateProvider;

public class BlockStates extends ExtendedBlockstateProvider {

	public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(w -> {
			createAllRotatedBlock(w.RELAY, obj(
					"block/relay_" + w.name, w.tall ? modLoc("block/connector_hv.obj") : ieLoc("block/connector/connector_mv.obj"),
							ImmutableMap.of("texture", modLoc("block/relay_" + w.name)),
							models()
					));

			if(w == MoreImmersiveWires.AE_DENSE_WIRE) {
				createAllRotatedBlock(w.CONNECTOR, models().getExistingFile(modLoc("block/ae_dense_connector")));
			} else {
				ModelFile def = obj(
						"block/connector_" + w.name, w.tall ? modLoc("block/connector_hv.obj") : ieLoc("block/connector/connector_mv.obj"),
								ImmutableMap.of("texture", modLoc("block/connector_" + w.name)),
								models()
						);

				if(w.CONNECTOR.get() instanceof OnCableConnectorBlock) {
					ModelFile onCable = obj(
							"block/connector_" + w.name + "_c", w.tall ? modLoc("block/connector_hv.obj") : modLoc("block/connector_mv_cable.obj"),
									ImmutableMap.of("texture", modLoc("block/connector_" + w.name)),
									models()
							);
					createAllRotatedBlock(w.CONNECTOR, d -> {
						if(d.getSetStates().get(OnCableConnectorBlock.ON_CABLE) == Boolean.TRUE)return onCable;
						else return def;
					}, List.of(OnCableConnectorBlock.ON_CABLE));
				} else {
					createAllRotatedBlock(w.CONNECTOR, def);
				}
			}
		});

		createAllRotatedBlock(MoreImmersiveWires.CC_MODEM_CONNECTOR, d -> {
			boolean p = d.getSetStates().get(BlockWiredModemFull.PERIPHERAL_ON) == Boolean.TRUE;
			boolean m = d.getSetStates().get(BlockWiredModemFull.MODEM_ON) == Boolean.TRUE;
			return models().withExistingParent("cc_modem_connector" + (m ? "_on" : "") + (p ? "_p" : ""), modLoc("block/modem_connector"))
					.texture("front", new ResourceLocation("computercraft:block/wired_modem_face" + (p ? "_peripheral" : "") + (m ? "_on" : "")));
		}, List.of(BlockWiredModemFull.MODEM_ON, BlockWiredModemFull.PERIPHERAL_ON));
	}

}
