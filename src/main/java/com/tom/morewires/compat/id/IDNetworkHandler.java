package com.tom.morewires.compat.id;

import java.util.Set;

import org.cyclops.integrateddynamics.api.path.ISidedPathElement;
import org.cyclops.integrateddynamics.capability.path.SidedPathElement;

import com.tom.morewires.network.SimpleNetworkHandler;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;

public class IDNetworkHandler extends SimpleNetworkHandler<IDConnectorBlockEntity, IDNetworkHandler> {

	protected IDNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	protected IDConnectorBlockEntity connect(IImmersiveConnectable iic) {
		if(iic instanceof IDConnectorBlockEntity te)return te;
		else return null;
	}

	@Override
	protected void setNetworkHandler(IDConnectorBlockEntity c, IDNetworkHandler handler) {
		c.setNetworkHandler(handler);
	}

	public void visitAll(Set<ISidedPathElement> elements) {
		visitAll(e -> elements.add(SidedPathElement.of(e.getPathElement(), null)));
	}
}
