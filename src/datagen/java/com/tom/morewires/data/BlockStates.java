package com.tom.morewires.data;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.google.common.collect.ImmutableMap;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.block.OnCableConnectorBlock;
import com.tom.morewires.compat.ae.AEDenseConnectorBlock;
import com.tom.morewires.compat.cc.CCWireDefinition;

import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.data.blockstates.ExtendedBlockstateProvider;

public class BlockStates extends ExtendedBlockstateProvider {

	public BlockStates(PackOutput gen, ExistingFileHelper exFileHelper) {
		super(gen, MoreImmersiveWires.modid, exFileHelper);
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

		createAllRotatedBlock(MoreImmersiveWires.AE_DENSE_WIRE.simple().CONNECTOR, d -> {
			boolean p = (boolean) d.getSetStates().get(AEDenseConnectorBlock.POWERED);
			return models().withExistingParent("ae_dense_connector" + (p ? "_p" : ""), modLoc("block/ae_dense_connector_base")).
					texture("base", modLoc("block/ae_dense_connector_base" + (p ? "_powered" : "")));
		}, List.of(AEDenseConnectorBlock.POWERED));

		createAllRotatedBlock(CCWireDefinition.CC_MODEM_CONNECTOR, d -> {
			boolean p = d.getSetStates().get(WiredModemFullBlock.PERIPHERAL_ON) == Boolean.TRUE;
			boolean m = d.getSetStates().get(WiredModemFullBlock.MODEM_ON) == Boolean.TRUE;
			return models().withExistingParent("cc_modem_connector" + (m ? "_on" : "") + (p ? "_p" : ""), modLoc("block/modem_connector"))
					.texture("front", ResourceLocation.parse("computercraft:block/wired_modem_face" + (p ? "_peripheral" : "") + (m ? "_on" : "")));
		}, List.of(WiredModemFullBlock.MODEM_ON, WiredModemFullBlock.PERIPHERAL_ON));
	}

	private ResourceLocation ieLoc(String string) {
		return ResourceLocation.tryBuild(Lib.MODID, string);
	}

	protected <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider, @Nullable RenderType layer) {
		final var m = obj(name, model, textures, provider);
		if(layer != null)setRenderType(layer, m);
		return m;
	}
}
