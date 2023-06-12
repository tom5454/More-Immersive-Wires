package com.tom.morewires.network;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.localhandlers.IWorldTickable;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;

public abstract class NodeNetworkHandler<C, N> extends LocalNetworkHandler implements IWorldTickable {
	protected boolean gridInitialized, needRefresh;
	private Set<C> connections = new HashSet<>();
	private Set<ChunkPos> proxyChunks = new HashSet<>();

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
				proxyChunks.clear();
				connections.forEach(this::clearConnection);
				connections.clear();
				for(ConnectionPoint cp : localNet.getConnectionPoints()) {
					IImmersiveConnectable iic = localNet.getConnector(cp);
					if(iic.isProxy()) {
						proxyChunks.add(new ChunkPos(iic.getPosition()));
						continue;
					}
					if(first) {
						first = false;
						connectFirst(iic, main);
					}
					C c = connect(iic, main);
					if(c != null)
						connections.add(c);
				}
			}
		} else if(w.getGameTime() % 10 == 0) {
			for (ChunkPos p : proxyChunks) {
				if(w.getChunkSource().hasChunk(p.x, p.z)) {
					reset();
					break;
				}
			}
		}
	}

	protected void initNode(Level level) {}
	protected abstract N getNode();
	protected abstract C connect(IImmersiveConnectable iic, N node);
	protected abstract void connectFirst(IImmersiveConnectable iic, N main);
}
