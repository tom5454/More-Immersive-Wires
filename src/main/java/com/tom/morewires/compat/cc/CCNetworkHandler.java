package com.tom.morewires.compat.cc;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.tom.morewires.network.NodeNetworkHandler;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.network.wired.IWiredElement;
import dan200.computercraft.api.network.wired.IWiredNode;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.localhandlers.IWorldTickable;

public class CCNetworkHandler extends NodeNetworkHandler<IWiredNode, IWiredNode> implements IWorldTickable {
	private Level level;
	private final IWiredElement cable = new CableElement();
	private final IWiredNode node = cable.getNode();

	protected CCNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	public void update(Level w) {
		this.level = w;
		super.update(w);
	}

	private class CableElement implements IWiredElement {
		private final IWiredNode node = ComputerCraftAPI.createWiredNodeForElement(this);

		@Override
		public Level getLevel() {
			return level;
		}

		@Override
		public Vec3 getPosition() {
			return Vec3.ZERO;
		}

		@Override
		public IWiredNode getNode() {
			return node;
		}

		@Override
		public String getSenderID() {
			return "miw_internal";
		}
	}

	@Override
	protected void clearConnection(IWiredNode c) {
		node.disconnectFrom(c);
	}

	@Override
	protected IWiredNode getNode() {
		return node;
	}

	@Override
	protected IWiredNode connect(IImmersiveConnectable iic, IWiredNode nodeIn) {
		if(iic instanceof ICCTile te) {
			IWiredNode node = te.getElement().getNode();
			nodeIn.connectTo(node);
			return node;
		}
		return null;
	}

	@Override
	protected void connectFirst(IImmersiveConnectable iic, IWiredNode main) {}
}
