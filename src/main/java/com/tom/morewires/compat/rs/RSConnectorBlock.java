package com.tom.morewires.compat.rs;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;

import com.tom.morewires.block.OnCableConnectorBlock;

public class RSConnectorBlock extends OnCableConnectorBlock<RSConnectorBlockEntity> {

	public RSConnectorBlock(RegistryObject<BlockEntityType<RSConnectorBlockEntity>> type,
			BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(type, isOnCable);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
		if (!level.isClientSide) {
			INetworkNode node = API.instance().getNetworkNodeManager((ServerLevel) level).getNode(pos);
			if (node instanceof NetworkNode) {
				((NetworkNode) node).setRedstonePowered(level.hasNeighborSignal(pos));
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof NetworkNodeBlockEntity) {
				IItemHandler handler = ((NetworkNodeBlockEntity) blockEntity).getNode().getDrops();
				if (handler != null) {
					NonNullList<ItemStack> drops = NonNullList.create();

					for (int i = 0; i < handler.getSlots(); ++i) {
						drops.add(handler.getStackInSlot(i));
					}

					Containers.dropContents(level, pos, drops);
				}
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}
}
