package com.tom.morewires.compat.ic2;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.block.OnCableConnectorBlock;
import com.tom.morewires.tile.IOnCable;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.EnergyConnector;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalBE;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IScrewdriverInteraction;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.tiles.readers.IEUStorage;
import ic2.core.block.base.tiles.BaseTileEntity;

public abstract class IC2ConnectorBlockEntity extends BaseTileEntity implements IImmersiveConnectable, IOnCable, IBlockBounds, IDirectionalBE, EnergyConnector, IEServerTickableBE, IMultiEnergySource, IEUStorage, IEnergySink, IScrewdriverInteraction {
	protected GlobalWireNetwork globalNet;
	private final int cap, tier;
	private final MutableEnergyStorage storageToNet;
	private final MutableEnergyStorage storageToMachine;
	public int currentTickToNet = 0;
	public boolean addedToEnergyNet = false;
	private int lastIn = 1;

	protected IC2ConnectorBlockEntity(BlockPos pos, BlockState state, int cap, int tier) {
		super(pos, state);
		this.cap = cap;
		this.tier = tier;
		this.storageToMachine = new MutableEnergyStorage(cap, cap, cap);
		this.storageToNet = new MutableEnergyStorage(cap, cap, cap);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		CompoundTag toNet = new CompoundTag();
		EnergyHelper.serializeTo(storageToNet, toNet);
		nbt.put("toNet", toNet);
		CompoundTag toMachine = new CompoundTag();
		EnergyHelper.serializeTo(storageToMachine, toMachine);
		nbt.put("toMachine", toMachine);
		nbt.putInt("lastPacket", lastIn);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		CompoundTag toMachine = nbt.getCompound("toMachine");
		EnergyHelper.deserializeFrom(storageToMachine, toMachine);
		CompoundTag toNet = nbt.getCompound("toNet");
		EnergyHelper.deserializeFrom(storageToNet, toNet);
		lastIn = nbt.getInt("lastPacket");
	}

	@Override
	public void tickServer() {
		currentTickToNet = 0;
	}

	@Override
	public void setFacing(Direction facing) {
		super.setFacing(facing);
		BlockState oldState = getBlockState();
		BlockState newState = oldState.setValue(ConnectorBlock.DEFAULT_FACING_PROP, facing);
		setState(newState);
	}

	@Override
	public Direction getFacing() {
		BlockState state = getBlockState();
		if(state.hasProperty(ConnectorBlock.DEFAULT_FACING_PROP))
			return state.getValue(ConnectorBlock.DEFAULT_FACING_PROP);
		else
			return Direction.NORTH;
	}

	@Override
	public boolean isSink(ConnectionPoint arg0) {
		return isOutput();
	}

	@Override
	public boolean isSource(ConnectionPoint arg0) {
		return !isOutput();
	}

	@Override
	public void consumeEnergy(int var1) {
		storageToMachine.extractEnergy(var1, false);
	}

	@Override
	public int getMaxEnergyOutput() {
		return cap;
	}

	@Override
	public int getProvidedEnergy() {
		return isOutput() ? storageToMachine.getEnergyStored() : 0;
	}

	@Override
	public int getSourceTier() {
		return tier;
	}

	@Override
	public boolean canEmitEnergy(IEnergyAcceptor var1, Direction var2) {
		return isOutput() && getFacing() == var2;
	}

	@Override
	public int getStoredEU() {
		return storageToMachine.getEnergyStored();
	}

	@Override
	public int getMaxEU() {
		return cap;
	}

	@Override
	public int getTier() {
		return tier;
	}

	@Override
	public int getPacketCount() {
		return 1;
	}

	@Override
	public boolean hasMultiplePackets() {
		return false;
	}

	@Override
	public boolean canConnect() {
		return true;
	}

	@Override
	public void connectCable(WireType var1, ConnectionPoint var2, IImmersiveConnectable var3, ConnectionPoint var4) {

	}

	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}

	@Override
	public void removeCable(Connection var1, ConnectionPoint var2) {
		setChanged();
	}

	public boolean isOnCable() {
		return getBlockState().getValue(OnCableConnectorBlock.ON_CABLE);
	}

	public float getLength() {
		return isOnCable() ? 0.25F : 0.5625F;
	}

	@Override
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type) {
		Direction side = getFacing().getOpposite();
		double lengthFromHalf = getLength() - type.getRenderDiameter()/2-.5;
		return new Vec3(.5+lengthFromHalf*side.getStepX(),
				.5+lengthFromHalf*side.getStepY(),
				.5+lengthFromHalf*side.getStepZ());
	}

	@Override
	public VoxelShape getBlockBounds(@Nullable CollisionContext ctx) {
		return EnergyConnectorBlockEntity.getConnectorBounds(getFacing(), getLength());
	}

	@Override
	public boolean mirrorFacingOnPlacement(LivingEntity placer) {
		return true;
	}

	@Override
	public boolean canHammerRotate(Direction side, Vec3 hit, LivingEntity entity) {
		return false;
	}

	@Override
	public Collection<ConnectionPoint> getConnectionPoints() {
		return ImmutableList.of(new ConnectionPoint(getPosition(), 0));
	}

	@Override
	public BlockPos getConnectionMaster(WireType cableType, TargetingInfo target) {
		return getPosition();
	}

	@Override
	public ConnectionPoint getTargetedPoint(TargetingInfo info, Vec3i offset) {
		return new ConnectionPoint(getPosition(), 0);
	}

	@Override
	public PlacementLimitation getFacingLimitation() {
		return PlacementLimitation.SIDE_CLICKED;
	}

	@Override
	public void onLoaded() {
		super.onLoaded();
		if (isSimulating() && !this.addedToEnergyNet) {
			this.addedToEnergyNet = true;
			EnergyNet.INSTANCE.addTile(this);
		}
	}

	@Override
	public void onUnloaded(boolean chunk) {
		if (isSimulating() && this.addedToEnergyNet) {
			this.addedToEnergyNet = false;
			EnergyNet.INSTANCE.removeTile(this);
		}
		super.onUnloaded(chunk);
	}

	private boolean isUnloaded = false;

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		ConnectorBlockEntityHelper.onChunkUnload(globalNet, this);
		isUnloaded = true;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ConnectorBlockEntityHelper.onChunkLoad(this, level);
		isUnloaded = false;
	}

	@Override
	public void setLevel(Level worldIn) {
		super.setLevel(worldIn);
		globalNet = GlobalWireNetwork.getNetwork(worldIn);
	}

	public void setRemovedIE() {
		ConnectorBlockEntityHelper.remove(level, this);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if(!isUnloaded)
			setRemovedIE();
	}

	@Override
	public int getRequestedEnergy() {
		if(isOutput())return storageToMachine.getEnergyStored() > 0 ? 0 : storageToMachine.getMaxEnergyStored();//-storageToMachine.getEnergyStored();
		else return this.cap - this.storageToNet.getEnergyStored();
	}

	@Override
	public int getSinkTier() {
		return tier;
	}

	@Override
	public int acceptEnergy(Direction var1, int amount, int voltage) {
		int maxReceive = Mth.floor(Math.min(cap - currentTickToNet, amount));
		if(maxReceive <= 0)
			return amount;

		int accepted = Math.min(cap, maxReceive);
		accepted = Math.min(cap - storageToNet.getEnergyStored(), accepted);
		if(accepted <= 0)
			return amount;

		storageToNet.modifyEnergyStored(accepted);
		currentTickToNet += accepted;
		lastIn = voltage;
		setChanged();

		return amount - accepted;
	}

	@Override
	public boolean canAcceptEnergy(IEnergyEmitter var1, Direction var2) {
		return !isOutput() && getFacing() == var2;
	}

	public boolean isOutput() {
		return getBlockState().getValue(IC2ConnectorBlock.OUTPUT);
	}

	@Override
	public InteractionResult screwdriverUseSide(Direction arg0, Player arg1, InteractionHand arg2, Vec3 arg3) {
		if(!level.isClientSide) {
			setState(getBlockState().setValue(IC2ConnectorBlock.OUTPUT, !isOutput()));
			EnergyNet.INSTANCE.updateTile(this);
			globalNet.getLocalNet(worldPosition).getHandler(getNetId(), IC2EnergyTransferHandler.class).onConnectorLoaded(new ConnectionPoint(getPosition(), 0), this);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public int getAvailableEnergy() {
		return Math.min(lastIn, storageToNet.getEnergyStored());
	}

	@Override
	public void insertEnergy(int amount) {
		storageToMachine.receiveEnergy(amount, false);
	}

	@Override
	public void extractEnergy(int amount) {
		storageToNet.extractEnergy(amount, false);
	}

	abstract ResourceLocation getNetId();

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(getNetId());
	}
}
