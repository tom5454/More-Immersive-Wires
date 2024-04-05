package com.tom.morewires.block;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.tom.morewires.tile.IConnector;

import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.BasicConnectorBlock;

public class OnCableConnectorBlock<T extends BlockEntity & IImmersiveConnectable> extends BasicConnectorBlock<T> {
	public static final BooleanProperty ON_CABLE = BooleanProperty.create("on_cable");
	private BiPredicate<BlockGetter, BlockPos> isOnCable;

	public OnCableConnectorBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> type, BiPredicate<BlockGetter, BlockPos> isOnCable) {
		super(ConnectorBlock.PROPERTIES.get(), type);
		this.isOnCable = isOnCable;
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof IConnector connector) {
			if(world.isEmptyBlock(pos.relative(connector.getFacing()))) {
				popResource(world, pos, new ItemStack(this));
				connector.getLevelNonnull().removeBlock(pos, false);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ON_CABLE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.replacingClickedOnBlock() ? context.getClickedPos() : context.getClickedPos().relative(context.getClickedFace(), -1);
		return super.getStateForPlacement(context).setValue(ON_CABLE, isOnCable.test(context.getLevel(), pos));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		BlockState state = super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		return state.setValue(ON_CABLE, isOnCable.test(worldIn, currentPos.relative(state.getValue(ConnectorBlock.DEFAULT_FACING_PROP))));
	}
}
