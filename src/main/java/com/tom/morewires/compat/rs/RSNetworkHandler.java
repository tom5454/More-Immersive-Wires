package com.tom.morewires.compat.rs;

import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;

import com.tom.morewires.network.SimpleNetworkHandler;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;

public class RSNetworkHandler extends SimpleNetworkHandler<RSConnectorBlockEntity, RSNetworkHandler> {

	protected RSNetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	protected RSConnectorBlockEntity connect(IImmersiveConnectable iic) {
		if(iic instanceof RSConnectorBlockEntity te)return te;
		else return null;
	}

	@Override
	protected void setNetworkHandler(RSConnectorBlockEntity c, RSNetworkHandler handler) {
		c.networkChanged();
	}

	public void addConnections(ConnectionSink sink) {
		visitAll(s -> sink.tryConnect(s.getGlobalPos(), null));
	}
}
