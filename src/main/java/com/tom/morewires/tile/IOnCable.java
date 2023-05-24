package com.tom.morewires.tile;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.tom.morewires.block.OnCableConnectorBlock;

import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;

public interface IOnCable extends IBlockBounds {
	public static interface IOnCableConnector extends IConnector, IBlockBounds, IOnCable {

		default boolean isOnCable() {
			return getState().getValue(OnCableConnectorBlock.ON_CABLE);
		}

		default float getLength() {
			return isOnCable() ? 0.25F : 0.5625F;
		}

		@Override
		public default Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) {
			Direction side = getFacing().getOpposite();
			double lengthFromHalf = getLength() - type.getRenderDiameter()/2-.5;
			return new Vec3(.5+lengthFromHalf*side.getStepX(),
					.5+lengthFromHalf*side.getStepY(),
					.5+lengthFromHalf*side.getStepZ());
		}

		@Override
		public default VoxelShape getBlockBounds(@Nullable CollisionContext ctx) {
			return EnergyConnectorBlockEntity.getConnectorBounds(getFacing(), getLength());
		}
	}
}