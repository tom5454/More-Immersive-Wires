package com.tom.morewires.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.localhandlers.IWorldTickable;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public abstract class SimpleNetworkHandler<C, T extends SimpleNetworkHandler<C, T>> extends LocalNetworkHandler implements IWorldTickable {
	private Set<C> allConnectors = new HashSet<>();
	private boolean needsUpdate = true;

	protected SimpleNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	public LocalNetworkHandler merge(LocalNetworkHandler other) {
		reset();
		return this;
	}

	@Override
	public void onConnectorLoaded(ConnectionPoint p, IImmersiveConnectable iic) {
		reset();
	}

	@Override
	public void onConnectorUnloaded(BlockPos p, IImmersiveConnectable iic) {
		reset();
	}

	@Override
	public void onConnectorRemoved(BlockPos p, IImmersiveConnectable iic) {
		reset();
	}

	@Override
	public void onConnectionAdded(Connection c) {
		reset();
	}

	@Override
	public void onConnectionRemoved(Connection c) {
		reset();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Level w) {
		if(needsUpdate) {
			needsUpdate = false;
			for(ConnectionPoint cp : localNet.getConnectionPoints()) {
				IImmersiveConnectable iic = localNet.getConnector(cp);
				C c = connect(iic);
				if(c != null) {
					allConnectors.add(c);
				}
			}
			allConnectors.forEach(c -> setNetworkHandler(c, (T) this));
		}
	}

	protected abstract C connect(IImmersiveConnectable iic);

	private void reset() {
		allConnectors.forEach(c -> setNetworkHandler(c, null));
		allConnectors.clear();
		needsUpdate = true;
	}

	public void visitAll(Consumer<C> forEach) {
		allConnectors.forEach(forEach);
	}

	protected abstract void setNetworkHandler(C c, T handler);
}
