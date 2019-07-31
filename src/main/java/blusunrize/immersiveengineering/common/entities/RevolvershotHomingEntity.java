/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.entities;

import blusunrize.immersiveengineering.api.tool.BulletHandler.IBullet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RevolvershotHomingEntity extends RevolvershotEntity
{
	public int trackCountdown = 5;
	public double redirectionSpeed = .25;
	public LivingEntity targetOverride;

	public RevolvershotHomingEntity(World world)
	{
		super(world);
	}

	public RevolvershotHomingEntity(World world, double x, double y, double z, double ax, double ay, double az, IBullet type)
	{
		super(world, x, y, z, ax, ay, az, type);
	}

	public RevolvershotHomingEntity(World world, LivingEntity living, double ax, double ay, double az, IBullet type, ItemStack stack)
	{
		super(world, living, ax, ay, az, type);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(!world.isRemote&&this.ticksExisted > trackCountdown)
		{
			LivingEntity target = getTarget();
			if(target!=null)
			{
				Vec3d oldMotion = getMotion();
				Vec3d newMotion = new Vec3d(
						oldMotion.x*(1-redirectionSpeed)+(target.posX-this.posX)*redirectionSpeed,
						oldMotion.y*(1-redirectionSpeed)+((target.posY+target.getHeight()/2)-this.posY)*redirectionSpeed,
						oldMotion.z*(1-redirectionSpeed)+(target.posZ-this.posZ)*redirectionSpeed).normalize();

				setMotion(newMotion);
			}
		}
	}

	public LivingEntity getTarget()
	{
		if(targetOverride!=null&&targetOverride.isAlive())
			return targetOverride;
		double r = 20D;
		AxisAlignedBB aabb = new AxisAlignedBB(posX-r, posY-r, posZ-r, posX+r, posY+r, posZ+r);
		LivingEntity target = null;
		for(Object o : world.getEntitiesWithinAABB(LivingEntity.class, aabb))
			if(o instanceof LivingEntity&&!o.equals(this.shootingEntity))
				if(target==null||((LivingEntity)o).getDistanceSq(this) < target.getDistanceSq(this))
					target = (LivingEntity)o;
		return target;
	}
}