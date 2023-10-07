package com.tom.morewires.compat.cc;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.tom.morewires.network.NodeNetworkHandler;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.network.wired.WiredNode;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.localhandlers.IWorldTickable;

public class CCNetworkHandler extends NodeNetworkHandler<WiredNode, WiredNode> implements IWorldTickable {
	private Level level;
	private final WiredElement cable = new CableElement();
	private final WiredNode node = cable.getNode();

	protected CCNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	public void update(Level w) {
		this.level = w;
		super.update(w);
	}

	private class CableElement implements WiredElement {
		private final WiredNode node = ComputerCraftAPI.createWiredNodeForElement(this);

		@Override
		public Level getLevel() {
			return level;
		}

		@Override
		public Vec3 getPosition() {
			return Vec3.ZERO;
		}

		@Override
		public WiredNode getNode() {
			return node;
		}

		@Override
		public String getSenderID() {
			return "miw_internal";
		}
	}

	@Override
	protected void clearConnection(WiredNode c) {
		node.disconnectFrom(c);
	}

	@Override
	protected WiredNode getNode() {
		return node;
	}

	@Override
	protected WiredNode connect(IImmersiveConnectable iic, WiredNode nodeIn) {
		if(iic instanceof ICCTile te) {
			WiredNode node = te.getElement().getNode();
			nodeIn.connectTo(node);
			return node;
		}
		return null;
	}

	@Override
	protected void connectFirst(IImmersiveConnectable iic, WiredNode main) {}
}
