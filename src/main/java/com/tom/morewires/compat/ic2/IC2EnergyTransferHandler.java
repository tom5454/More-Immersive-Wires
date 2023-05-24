package com.tom.morewires.compat.ic2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireCollisionData.CollisionInfo;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.ICollisionHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler.IShockingWire;
import blusunrize.immersiveengineering.api.wires.utils.IElectricDamageSource;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

public class IC2EnergyTransferHandler extends EnergyTransferHandler implements ICollisionHandler {
	private static final double KNOCKBACK_PER_DAMAGE = 10;

	public IC2EnergyTransferHandler(LocalWireNetwork net, GlobalWireNetwork global) {
		super(net, global);
	}

	@Override
	public void onCollided(LivingEntity e, BlockPos pos, CollisionInfo info) {
		WireType wType = info.connection().type;
		if(!(wType instanceof IShockingWire shockWire))
			return;
		double extra = shockWire.getDamageRadius();
		AABB eAabb = e.getBoundingBox();
		AABB includingExtra = eAabb.inflate(extra).move(-pos.getX(), -pos.getY(), -pos.getZ());
		boolean collides = includingExtra.contains(info.intersectA())||includingExtra.contains(info.intersectB());
		if(!collides&&includingExtra.clip(info.intersectA(), info.intersectB()).isEmpty())
			return;
		final ConnectionPoint target = info.connection().getEndA();//TODO less random choice?
		final List<SourceData> available = getAvailableEnergy(this, target);
		if(available.isEmpty())
			return;
		int totalAvailable = 0;
		for(SourceData source : available)
			totalAvailable += source.amountAvailable*(1-source.pathToSource.loss);
		totalAvailable = Math.min(totalAvailable, shockWire.getTransferRate());

		final float maxPossibleDamage = shockWire.getDamageAmount(e, info.connection(), totalAvailable);
		if(maxPossibleDamage <= 0)
			return;
		IElectricDamageSource dmg = WireDamageHandler.GET_WIRE_DAMAGE.getValue()
				.apply(maxPossibleDamage, shockWire.getElectricSource());
		if(!dmg.apply(e))
			return;
		final float actualDamage = dmg.getDamage();
		Vec3 v = e.getLookAngle();
		ApiUtils.knockbackNoSource(e, actualDamage/KNOCKBACK_PER_DAMAGE, v.x, v.z);
		//Consume energy
		final double factor = actualDamage/maxPossibleDamage;
		Object2DoubleMap<Connection> transferred = this.getTransferredNextTick();
		for(SourceData source : available) {
			final double energyFromSource = source.amountAvailable*factor;
			source.source.extractEnergy(Mth.ceil(energyFromSource));
			for(Connection c : source.pathToSource.conns)
				transferred.mergeDouble(c, energyFromSource, Double::sum);
		}
	}

	private List<SourceData> getAvailableEnergy(EnergyTransferHandler energyHandler, ConnectionPoint target) {
		List<SourceData> ret = new ArrayList<>();
		Map<ConnectionPoint, Path> paths = null;
		for(Entry<ConnectionPoint, EnergyConnector> c : energyHandler.getSources().entrySet())
		{
			final int energy = c.getValue().getAvailableEnergy();
			if(energy <= 0)
				continue;
			if(paths==null)
				paths = energyHandler.getPathsFromSource(target);
			final Path path = paths.get(c.getKey());
			if(path!=null)
				ret.add(new SourceData(energy, path, c.getValue()));
		}
		return ret;
	}

	private record SourceData(int amountAvailable, Path pathToSource, EnergyConnector source)
	{
	}
}
