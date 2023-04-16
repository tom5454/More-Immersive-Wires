package com.tom.morewires.compat.cc;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.registries.RegistryObject;

import com.tom.morewires.MoreImmersiveWires;

import dan200.computercraft.shared.peripheral.modem.wired.BlockWiredModemFull;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;

public class CCModemConnectorBlock extends ConnectorBlock<CCModemConnectorBlockEntity> {

	public CCModemConnectorBlock(RegistryObject<BlockEntityType<BlockEntity>> entityType) {
		super(ConnectorBlock.PROPERTIES.get(), (RegistryObject) entityType);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.FACING_ALL, BlockStateProperties.WATERLOGGED, BlockWiredModemFull.MODEM_ON, BlockWiredModemFull.PERIPHERAL_ON);
	}

	@Override
	public final void onRemove(BlockState block, Level world, BlockPos pos, BlockState replace, boolean bool) {
		if (block.getBlock() == replace.getBlock()) return;

		BlockEntity tile = world.getBlockEntity(pos);
		super.onRemove(block, world, pos, replace, bool);
		world.removeBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.destroy();
	}

	@Override
	public final InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if(player.getItemInHand(hand).is(MoreImmersiveWires.CC_WIRE.COIL.get()))return InteractionResult.PASS;
		BlockEntity tile = world.getBlockEntity(pos);
		return tile instanceof CCBlockEntity generic ? generic.onActivate(player, hand, hit) : InteractionResult.PASS;
	}

	@Override
	public final void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbourBlock,
			BlockPos neighbourPos, boolean isMoving) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.onNeighbourChange(neighbourPos);
	}

	@Override
	public final void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbour) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof CCBlockEntity generic) generic.onNeighbourTileEntityChange(neighbour);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof CCBlockEntity generic) generic.blockTick();
	}
}
