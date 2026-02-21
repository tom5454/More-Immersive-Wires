package com.tom.morewires.compat.id;

import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedNetworkElement;
import org.cyclops.integrateddynamics.core.network.NetworkElementBase;

public class IDConnectorNetworkElement extends NetworkElementBase implements IPositionedNetworkElement {
	private DimPos pos;

	public IDConnectorNetworkElement(DimPos pos) {
		this.pos = pos;
	}

	@Override
	public void setPriorityAndChannel(INetwork p0, int p1, int p2) {

	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getChannel() {
		return IPositionedAddonsNetwork.DEFAULT_CHANNEL;
	}

	@Override
	public boolean canRevalidate(INetwork network) {
		return canRevalidatePositioned(network, pos);
	}

	@Override
	public void revalidate(INetwork network) {
		super.revalidate(network);
		revalidatePositioned(network, pos);
	}

	@Override
	public int compareTo(INetworkElement o) {
		if(o instanceof IDConnectorNetworkElement e) {
			return getPosition().compareTo(e.getPosition());
		}
		return this.getClass().getCanonicalName().compareTo(o.getClass().getCanonicalName());
	}

	@Override
	public DimPos getPosition() {
		return pos;
	}

	@Override
	public boolean isLoaded() {
		return INetworkElement.shouldTick(this.getPosition());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (getClass() != obj.getClass()) return false;
		IDConnectorNetworkElement other = (IDConnectorNetworkElement) obj;
		if (pos == null) {
			if (other.pos != null) return false;
		} else if (!pos.equals(other.pos)) return false;
		return true;
	}
}