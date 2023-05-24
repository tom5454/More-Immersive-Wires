package com.tom.morewires.compat.ftbic;

import static com.tom.morewires.compat.ftbic.FTBICWireDefinition.ZAP_MULT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import com.google.common.collect.ImmutableList;

import com.tom.morewires.tile.IOnCable.IOnCableConnector;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.EnergyConnector;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler.IEnergyWire;
import blusunrize.immersiveengineering.common.blocks.generic.ImmersiveConnectableBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BurntCableBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import dev.ftb.mods.ftbic.util.CachedEnergyStorageOrigin;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.ForgeEnergyHandler;

public class FTBICConnectorBlockEntity extends ImmersiveConnectableBlockEntity implements IOnCableConnector, EnergyHandler, EnergyConnector, IEServerTickableBE {
	private final FTBICWireDefinition def;
	private final MutableEnergyStorage storageToNet;
	private final MutableEnergyStorage storageToMachine;
	public int currentTickToNet = 0;
	private long currentElectricNetwork = -1L;
	private CachedEnergyStorage[] connectedEnergyBlocks;

	public FTBICConnectorBlockEntity(FTBICWireDefinition w, BlockPos p_155229_, BlockState p_155230_) {
		super(w.CONNECTOR_ENTITY.get(), p_155229_, p_155230_);
		this.def = w;
		this.storageToMachine = new MutableEnergyStorage(w.energyCapacity * ZAP_MULT, w.energyCapacity * ZAP_MULT, w.energyCapacity * ZAP_MULT);
		this.storageToNet = new MutableEnergyStorage(w.energyCapacity * ZAP_MULT, w.energyCapacity * ZAP_MULT, w.energyCapacity * ZAP_MULT);
	}

	@Override
	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset) {
		return cableType == def.wireType;
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		CompoundTag toNet = new CompoundTag();
		EnergyHelper.serializeTo(storageToNet, toNet);
		nbt.put("toNet", toNet);
		CompoundTag toMachine = new CompoundTag();
		EnergyHelper.serializeTo(storageToMachine, toMachine);
		nbt.put("toMachine", toMachine);
	}

	@Override
	public void readCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		CompoundTag toMachine = nbt.getCompound("toMachine");
		EnergyHelper.deserializeFrom(storageToMachine, toMachine);
		CompoundTag toNet = nbt.getCompound("toNet");
		EnergyHelper.deserializeFrom(storageToNet, toNet);
	}

	@Override
	public void tickServer() {
		this.handleEnergyOutput();
		currentTickToNet = 0;
	}

	@Override
	public final double getEnergyCapacity() {
		return def.energyCapacity / (double) ZAP_MULT;
	}

	@Override
	public final double getEnergy() {
		return storageToNet.getEnergyStored() / (double) ZAP_MULT;
	}

	@Override
	public final void setEnergyRaw(double e) {
		storageToNet.setStoredEnergy(Mth.floor(e * ZAP_MULT));
	}

	@Override
	public double insertEnergy(double maxInsert, boolean simulate) {
		if(maxInsert > def.energyCapacity / (double) ZAP_MULT) {
			if (!simulate) {
				this.setBurnt(true);
			}

			return maxInsert;
		} else {
			int maxReceive = Mth.floor(Math.min(def.energyCapacity - currentTickToNet, maxInsert * ZAP_MULT));
			if(maxReceive <= 0)
				return 0;

			int accepted = Math.min(def.energyCapacity, maxReceive);
			accepted = Math.min(def.energyCapacity - storageToNet.getEnergyStored(), accepted);
			if(accepted <= 0)
				return 0;

			if(!simulate) {
				storageToNet.modifyEnergyStored(accepted);
				currentTickToNet += accepted;
				setChanged();
			}

			return accepted / (double) ZAP_MULT;
		}
	}

	@Override
	public boolean isEnergyHandlerInvalid() {
		return this.isBurnt() || this.isRemoved();
	}

	@Override
	public final double getMaxInputEnergy() {
		return def.energyCapacity / (double) ZAP_MULT;
	}

	public double getTotalPossibleEnergyCapacity() {
		return this.def.energyCapacity / (double) ZAP_MULT;
	}

	@Override
	public final boolean canBurn() {
		return true;
	}

	@Override
	public final void setBurnt(boolean b) {
		if (b && !this.level.isClientSide()) {
			globalNet.getLocalNet(worldPosition).getConnections(worldPosition).forEach(c -> {
				if(c instanceof IEnergyWire w)w.burn(c, 1, globalNet, level);
			});
			level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);//TODO burn
			ElectricBlockEntity.electricNetworkUpdated(this.level, this.worldPosition);
			this.level.levelEvent(1502, this.worldPosition, 0);
		}
	}

	@Override
	public final boolean isBurnt() {
		return false;
	}

	public void syncBlock() {
		this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 11);
		this.setChanged();
	}

	public void handleEnergyOutput() {
		if (!this.level.isClientSide()) {
			double e;

			double tenergy = this.storageToMachine.getEnergyStored() / (double) ZAP_MULT;
			if (tenergy > 0.0D) {
				CachedEnergyStorage[] blocks = this.getConnectedEnergyBlocks();
				int validBlocks = 0;
				CachedEnergyStorage[] var16 = blocks;
				int var6 = blocks.length;

				for (int var7 = 0; var7 < var6; ++var7) {
					CachedEnergyStorage storage = var16[var7];
					if (storage.isInvalid()) {
						ElectricBlockEntity.electricNetworkUpdated(this.level, storage.blockEntity.getBlockPos());
					} else if (storage.shouldReceiveEnergy()) {
						++validBlocks;
					}
				}

				if (validBlocks > 0) {
					e = tenergy / validBlocks;
					CachedEnergyStorage[] var17 = blocks;
					int var18 = blocks.length;

					for (int var9 = 0; var9 < var18; ++var9) {
						CachedEnergyStorage storage = var17[var9];
						if (!storage.isInvalid() && storage.shouldReceiveEnergy()) {
							if (storage.origin.cableTier != null && storage.origin.cableTier.transferRate.get() < e) {
								this.level.setBlock(storage.origin.cablePos, BurntCableBlock
										.getBurntCable(this.level.getBlockState(storage.origin.cablePos)), 3);
								this.level.levelEvent(1502, storage.origin.cablePos, 0);
								storage.origin.cableBurnt = true;
							} else {
								double a = storage.energyHandler.insertEnergy(Math.min(e, this.storageToMachine.getEnergyStored() / (double)ZAP_MULT), true);
								if (a > 0.0D) {
									int ex = this.storageToMachine.extractEnergy(Mth.floor(a * ZAP_MULT), false);
									storage.energyHandler.insertEnergy(ex / (double) ZAP_MULT, false);
									this.setChanged();
								}

								if (this.storageToMachine.getEnergyStored() < e * ZAP_MULT) {
									break;
								}
							}
						}
					}
				}

			}
		}
	}

	public boolean isValidEnergyOutputSide(Direction direction) {
		return getFacing() == direction;
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return getFacing() == direction;
	}

	public CachedEnergyStorage[] getConnectedEnergyBlocks() {
		if (this.level != null && !this.level.isClientSide()) {
			long currentId = ElectricBlockEntity.getCurrentElectricNetwork(this.level, this.getBlockPos());
			if (this.connectedEnergyBlocks == null || this.currentElectricNetwork == -1L
					|| this.currentElectricNetwork != currentId) {
				Set<CachedEnergyStorage> set = new HashSet<>();
				Set<BlockPos> traversed = new HashSet<>();
				traversed.add(this.worldPosition);
				Direction[] var5 = FTBICUtils.DIRECTIONS;
				int var6 = var5.length;

				for (int var7 = 0; var7 < var6; ++var7) {
					Direction direction = var5[var7];
					if (this.isValidEnergyOutputSide(direction)) {
						CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
						origin.direction = direction;
						this.find(traversed, set, origin, 0, this.worldPosition, direction);
					}
				}

				this.connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
				this.currentElectricNetwork = currentId;
			}

			return this.connectedEnergyBlocks;
		} else {
			return CachedEnergyStorage.EMPTY;
		}
	}

	private void find(Set<BlockPos> traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin,
			int distance, BlockPos currentPos, Direction direction) {
		if (this.level != null && distance <= FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get()) {
			BlockPos pos = currentPos.relative(direction);
			if (traversed.add(pos)) {
				BlockState state = this.level.getBlockState(pos);
				Block var10 = state.getBlock();
				if (var10 instanceof CableBlock) {
					CableBlock cableBlock = (CableBlock) var10;
					if (origin.cableTier == null || cableBlock.tier.transferRate.get() < origin.cableTier.transferRate.get()) {
						origin.cableTier = cableBlock.tier;
						origin.cablePos = pos;
					}

					Direction[] var15 = FTBICUtils.DIRECTIONS;
					int var11 = var15.length;

					for (int var12 = 0; var12 < var11; ++var12) {
						Direction dir = var15[var12];
						if (state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
							this.find(traversed, set, origin, distance + 1, pos, dir);
						}
					}
				} else if (state.hasBlockEntity()) {
					BlockEntity entity = this.level.getBlockEntity(pos);
					EnergyHandler handler = entity instanceof EnergyHandler ? (EnergyHandler) entity : null;
					if (handler != null) {
						if (handler != this && handler.getMaxInputEnergy() > 0.0D && !handler.isBurnt()
								&& handler.isValidEnergyInputSide(direction.getOpposite())) {
							CachedEnergyStorage s = new CachedEnergyStorage();
							s.origin = origin;
							s.distance = distance;
							s.blockEntity = entity;
							s.energyHandler = handler;
							set.add(s);
						}
					} else if (FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get() > 0.0D) {
						if (entity == null) {
							return;
						}

						LazyOptional<IEnergyStorage> energyCap = entity.getCapability(ForgeCapabilities.ENERGY,
								direction.getOpposite());
						IEnergyStorage feStorage = energyCap.orElse((IEnergyStorage) null);
						if (feStorage != null && feStorage.canReceive()) {
							CachedEnergyStorage s = new CachedEnergyStorage();
							s.origin = origin;
							s.distance = distance;
							s.blockEntity = entity;
							s.energyHandler = new ForgeEnergyHandler(energyCap, feStorage);
							set.add(s);
						}
					}
				}

			}
		}
	}

	@Override
	public boolean isSource(ConnectionPoint cp) {
		return true;
	}

	@Override
	public boolean isSink(ConnectionPoint cp) {
		return true;
	}

	@Override
	public int getAvailableEnergy() {
		return storageToNet.getEnergyStored();
	}

	@Override
	public int getRequestedEnergy() {
		return storageToMachine.getMaxEnergyStored()-storageToMachine.getEnergyStored();
	}

	@Override
	public void insertEnergy(int amount) {
		storageToMachine.receiveEnergy(amount, false);
	}

	@Override
	public void extractEnergy(int amount) {
		storageToNet.extractEnergy(amount, false);
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers() {
		return ImmutableList.of(def.NET_ID);
	}
}
