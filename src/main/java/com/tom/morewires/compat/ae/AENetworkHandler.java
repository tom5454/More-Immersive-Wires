package com.tom.morewires.compat.ae;

import net.minecraft.world.level.Level;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.network.NodeNetworkHandler;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.me.service.helpers.ConnectionWrapper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;

public class AENetworkHandler extends NodeNetworkHandler<ConnectionWrapper, IGridNode> implements IGridNodeListener<AENetworkHandler> {
	private final IManagedGridNode mainNode = this.createMainNode()
			.setInWorldNode(false)
			.setTagName("proxy")
			.setIdlePowerUsage(0.0D);

	protected IManagedGridNode createMainNode() {
		return GridHelper.createManagedNode(this, this).setFlags(new GridFlags[0]).setVisualRepresentation(MoreImmersiveWires.AE_WIRE.simple().COIL.get());
	}

	protected AENetworkHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	public void onSaveChanges(AENetworkHandler var1, IGridNode var2) {

	}

	@Override
	protected void clearConnection(ConnectionWrapper c) {
		if(c.getConnection() != null)c.getConnection().destroy();
	}

	@Override
	protected void initNode(Level level) {
		mainNode.create(level, null);
	}

	@Override
	protected IGridNode getNode() {
		return mainNode.getNode();
	}

	@Override
	protected ConnectionWrapper connect(IImmersiveConnectable iic, IGridNode nodeIn) {
		if(iic instanceof AENetworkedBlockEntity te) {
			IGridNode node = te.getActionableNode();
			if(node != null) {
				return new ConnectionWrapper(GridHelper.createConnection(nodeIn, node));
			}
			needRefresh = true;
		}
		return null;
	}

	@Override
	protected void connectFirst(IImmersiveConnectable iic, IGridNode main) {
		if(iic instanceof AENetworkedBlockEntity te) {
			IGridNode node = te.getActionableNode();
			if(node != null) {
				mainNode.setOwningPlayerId(node.getOwningPlayerId());
			}
		}
	}
}
