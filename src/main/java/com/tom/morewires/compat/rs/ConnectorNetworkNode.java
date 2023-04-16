package com.tom.morewires.compat.rs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.util.NetworkUtils;

import com.tom.morewires.MoreImmersiveWires;

import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;

public class ConnectorNetworkNode extends NetworkNode {
	public static final ResourceLocation ID = new ResourceLocation(MoreImmersiveWires.modid, "connector");
	private RSNetworkHandler networkHandler;

	protected ConnectorNetworkNode(Level level, BlockPos pos) {
		super(level, pos);
	}

	@Override
	public int getEnergyUsage() {
		return 0;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void visit(Operator operator) {
		Direction facing = getDirection();
		INetworkNode oppositeNode = NetworkUtils.getNodeFromBlockEntity(this.level.getBlockEntity(this.pos.relative(facing)));
		if (oppositeNode != null && oppositeNode.canReceive(facing.getOpposite())) {
			operator.apply(this.level, this.pos.relative(facing), facing.getOpposite());
		}
		if(networkHandler != null) {
			networkHandler.visitAll(operator);
		}
	}

	@Override
	public boolean canConduct(Direction direction) {
		return getDirection() == direction;
	}

	@Override
	public Direction getDirection() {
		BlockState state = this.level.getBlockState(this.pos);
		if(state.hasProperty(ConnectorBlock.DEFAULT_FACING_PROP)) {
			return state.getValue(ConnectorBlock.DEFAULT_FACING_PROP);
		}
		return Direction.DOWN;
	}

	public void setNetworkHandler(RSNetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
		if (network != null) {
			network.getNodeGraph().invalidate(Action.PERFORM, network.getLevel(), network.getPosition());
		}
	}
}
