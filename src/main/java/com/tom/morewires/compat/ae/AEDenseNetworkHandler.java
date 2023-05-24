package com.tom.morewires.compat.ae;

import com.tom.morewires.MoreImmersiveWires;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;

public class AEDenseNetworkHandler extends AENetworkHandler {

	@Override
	protected IManagedGridNode createMainNode() {
		return GridHelper.createManagedNode(this, this).setFlags(GridFlags.DENSE_CAPACITY).setVisualRepresentation(MoreImmersiveWires.AE_DENSE_WIRE.simple().COIL.get());
	}

	protected AEDenseNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}
}
