package com.tom.morewires.compat.rs;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.PlayerAwareBlockEntity;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;

import com.tom.morewires.MoreImmersiveWires;
import com.tom.morewires.block.OnCableConnectorBlock;

public class RSConnectorBlock extends OnCableConnectorBlock<RSConnectorBlockEntity> {
	private static final AbstractBlockEntityTicker<RSConnectorBlockEntity> TICKER = new NetworkNodeBlockEntityTicker(MoreImmersiveWires.RS_WIRE.simple().CONNECTOR_ENTITY);

	public RSConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<RSConnectorBlockEntity>> type,
			BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(type, isOnCable);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState,
			final BlockEntityType<T> type) {
		return TICKER.get(level, (BlockEntityType) type);
	}

	@Override
	public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state,
			final LivingEntity entity, final ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);
		if (entity instanceof final Player player) {
			final BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof final PlayerAwareBlockEntity playerAware) {
				playerAware.setPlacedBy(player.getGameProfile().getId());
			}
		}
	}
}
