package com.calculusmaster.difficultraids.entity.entities.component;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class FrostSnowballEntity extends Snowball
{
    private float damage = 1.0F;

    public FrostSnowballEntity(EntityType<? extends Snowball> p_37391_, Level p_37392_)
    {
        super(p_37391_, p_37392_);
    }

    public void setDamage(float damage)
    {
        this.damage = damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult)
    {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        //Blaze stuff is handled by the super call
        if(!(entity instanceof Blaze) && !(entity instanceof Raider))
        {
            entity.hurt(DamageSource.thrown(this, this.getOwner()), this.damage);
        }
    }
}
