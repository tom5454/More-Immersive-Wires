package com.tom.morewires.network;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.localhandlers.IWorldTickable;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public abstract class NodeNetworkHandler<C, N> extends LocalNetworkHandler implements IWorldTickable {
	private boolean gridInitialized, needRefresh;
	private Set<C> connections = new HashSet<>();

	protected NodeNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	private void reset() {
		connections.forEach(this::clearConnection);
		connections.clear();
		needRefresh = true;
	}

	protected abstract void clearConnection(C c);

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

	@Override
	public void update(Level w) {
		if(!gridInitialized) {
			gridInitialized = true;
			initNode(w);
			needRefresh = true;
		} else if(needRefresh) {
			needRefresh = false;
			N main = getNode();
			if(main != null) {
				boolean first = true;
				for(ConnectionPoint cp : localNet.getConnectionPoints()) {
					IImmersiveConnectable iic = localNet.getConnector(cp);
					if(first) {
						first = false;
						connectFirst(iic, main);
					}
					C c = connect(iic, main);
					if(c != null)
						connections.add(c);
				}
			}
		}
	}

	protected void initNode(Level level) {}
	protected abstract N getNode();
	protected abstract C connect(IImmersiveConnectable iic, N node);
	protected abstract void connectFirst(IImmersiveConnectable iic, N main);
}
