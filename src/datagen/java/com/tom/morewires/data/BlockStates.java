package com.tom.morewires.data;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.google.common.collect.ImmutableMap;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.MoreImmersiveWires.Wire;
import com.tom.morewires.block.OnCableConnectorBlock;
import com.tom.morewires.compat.cc.CCWireDefinition;
import com.tom.morewires.compat.ic2.IC2ConnectorBlock;

import dan200.computercraft.shared.peripheral.modem.wired.BlockWiredModemFull;

import blusunrize.immersiveengineering.data.blockstates.ExtendedBlockstateProvider;

public class BlockStates extends ExtendedBlockstateProvider {

	public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		MoreImmersiveWires.ALL_WIRES.forEach(wt -> {
			wt.wireTypeDef.getRelays().forEach(r -> {
				createAllRotatedBlock(r.getRelayBlock(), obj(
						"block/relay_" + r.getName(), r.isExTallRelay() ? modLoc("block/relay_hv.obj") : r.isTallRelay() ? modLoc("block/connector_hv.obj") : ieLoc("block/connector/connector_mv.obj"),
								ImmutableMap.of("texture", modLoc("block/relay_" + r.getName())),
								models()
						));
			});
			wt.wireTypeDef.getConnectors().forEach(c -> {
				if(c.datagenConnectorBlock()) {
					ModelFile def = obj(
							"block/connector_" + c.getName(), c.isTallConnector() ? modLoc("block/connector_hv.obj") : ieLoc("block/connector/connector_mv.obj"),
									ImmutableMap.of("texture", modLoc("block/connector_" + c.getName())),
									models()
							);

					if(c.getConnectorBlock().get() instanceof OnCableConnectorBlock) {
						ModelFile onCable = obj(
								"block/connector_" + c.getName() + "_c", c.isTallConnector() ? modLoc("block/connector_hv_cable.obj") : modLoc("block/connector_mv_cable.obj"),
										ImmutableMap.of("texture", modLoc("block/connector_" + c.getName())),
										models()
								);
						createAllRotatedBlock(c.getConnectorBlock(), d -> {
							if(d.getSetStates().get(OnCableConnectorBlock.ON_CABLE) == Boolean.TRUE)return onCable;
							else return def;
						}, List.of(OnCableConnectorBlock.ON_CABLE));
					} else {
						createAllRotatedBlock(c.getConnectorBlock(), def);
					}
				}
			});
		});

		createAllRotatedBlock(MoreImmersiveWires.AE_DENSE_WIRE.simple().CONNECTOR, models().getExistingFile(modLoc("block/ae_dense_connector")));

		createAllRotatedBlock(CCWireDefinition.CC_MODEM_CONNECTOR, d -> {
			boolean p = d.getSetStates().get(BlockWiredModemFull.PERIPHERAL_ON) == Boolean.TRUE;
			boolean m = d.getSetStates().get(BlockWiredModemFull.MODEM_ON) == Boolean.TRUE;
			return models().withExistingParent("cc_modem_connector" + (m ? "_on" : "") + (p ? "_p" : ""), modLoc("block/modem_connector"))
					.texture("front", new ResourceLocation("computercraft:block/wired_modem_face" + (p ? "_peripheral" : "") + (m ? "_on" : "")));
		}, List.of(BlockWiredModemFull.MODEM_ON, BlockWiredModemFull.PERIPHERAL_ON));

		createIC2(MoreImmersiveWires.IC2_WIRES);
	}

	private void createIC2(Wire... ws) {
		for (Wire w : ws) {
			w.wireTypeDef.getConnectors().forEach(c -> {
				ModelFile defI = obj(
						"block/connector_" + c.getName() + "_i", c.isTallConnector() ? modLoc("block/connector_hv_io.obj") : modLoc("block/connector_mv_io.obj"),
								ImmutableMap.of("texture", modLoc("block/connector_" + c.getName()), "io", modLoc("block/input")),
								models()
						);

				ModelFile onCableI = obj(
						"block/connector_" + c.getName() + "_ci", c.isTallConnector() ? modLoc("block/connector_hv_cable_io.obj") : modLoc("block/connector_mv_cable_io.obj"),
								ImmutableMap.of("texture", modLoc("block/connector_" + c.getName()), "io", modLoc("block/input")),
								models()
						);

				ModelFile defO = obj(
						"block/connector_" + c.getName(), c.isTallConnector() ? modLoc("block/connector_hv_io.obj") : modLoc("block/connector_mv_io.obj"),
								ImmutableMap.of("texture", modLoc("block/connector_" + c.getName()), "io", modLoc("block/output")),
								models()
						);

				ModelFile onCableO = obj(
						"block/connector_" + c.getName() + "_c", c.isTallConnector() ? modLoc("block/connector_hv_cable_io.obj") : modLoc("block/connector_mv_cable_io.obj"),
								ImmutableMap.of("texture", modLoc("block/connector_" + c.getName()), "io", modLoc("block/output")),
								models()
						);

				createAllRotatedBlock(c.getConnectorBlock(), d -> {
					if(d.getSetStates().get(IC2ConnectorBlock.OUTPUT) == Boolean.TRUE) {
						if(d.getSetStates().get(OnCableConnectorBlock.ON_CABLE) == Boolean.TRUE)return onCableO;
						else return defO;
					} else {
						if(d.getSetStates().get(OnCableConnectorBlock.ON_CABLE) == Boolean.TRUE)return onCableI;
						else return defI;
					}
				}, List.of(OnCableConnectorBlock.ON_CABLE, IC2ConnectorBlock.OUTPUT));
			});
		}
	}

	protected <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider, @Nullable RenderType layer) {
		final var m = obj(name, model, textures, provider);
		if(layer != null)setRenderType(layer, m);
		return m;
	}
}
