package com.tom.morewires.compat.id;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.cyclops.integrateddynamics.core.helper.CableHelpers;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.block.OnCableConnectorBlock;

public class IDConnectorBlock extends OnCableConnectorBlock<IDConnectorBlockEntity> {

	public IDConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<IDConnectorBlockEntity>> type,
			BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(type, isOnCable);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState oldState, boolean isMoving) {
		super.onPlace(blockState, world, blockPos, oldState, isMoving);
		if (!world.isClientSide()) {
			CableHelpers.onCableAdded(world, blockPos);
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, placer, itemStack);
		if (!world.isClientSide()) {
			CableHelpers.onCableAddedByPlayer(world, pos, placer);
		}
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
		CableHelpers.onCableRemoving((Level) world, blockPos, true, false, blockState);
		Collection<Direction> connectedCables = CableHelpers.getExternallyConnectedCables((Level) world, blockPos);
		super.destroy(world, blockPos, blockState);
		CableHelpers.onCableRemoved((Level) world, blockPos, connectedCables);
	}

	@Override
	public void onBlockExploded(BlockState state, Level world, BlockPos blockPos, Explosion explosion) {
		CableHelpers.setRemovingCable(true);
		CableHelpers.onCableRemoving(world, blockPos, true, false, state);
		Collection<Direction> connectedCables = CableHelpers.getExternallyConnectedCables(world, blockPos);
		super.onBlockExploded(state, world, blockPos, explosion);
		CableHelpers.onCableRemoved(world, blockPos, connectedCables);
		CableHelpers.setRemovingCable(false);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, neighborBlock, fromPos, isMoving);
		NetworkHelpers.onElementProviderBlockNeighborChange(world, pos, neighborBlock, null, fromPos);
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);
		if (world instanceof Level) {
			NetworkHelpers.onElementProviderBlockNeighborChange((Level) world, pos, world.getBlockState(neighbor).getBlock(), null, neighbor);
		}
	}

	@Override
	public void onRemove(BlockState oldState, Level world, BlockPos blockPos, BlockState newState, boolean isMoving) {
		if (oldState.getBlock() != newState.getBlock()) {
			Collection<Direction> connectedCables = null;
			if (!CableHelpers.isRemovingCable()) {
				CableHelpers.onCableRemoving(world, blockPos, true, false, oldState);
				connectedCables = CableHelpers.getExternallyConnectedCables(world, blockPos);
			}
			super.onRemove(oldState, world, blockPos, newState, isMoving);
			if (!CableHelpers.isRemovingCable()) {
				CableHelpers.onCableRemoved(world, blockPos, connectedCables);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : (blockEntityType == MoreImmersiveWires.ID_WIRE.simple().CONNECTOR_ENTITY.get() ? (BlockEntityTicker<T>) new IDConnectorBlockEntity.Ticker() : null);
	}
}
