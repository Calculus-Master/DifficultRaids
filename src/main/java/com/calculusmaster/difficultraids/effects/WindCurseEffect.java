package com.calculusmaster.difficultraids.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class WindCurseEffect extends MobEffect
{
    private final Random random = new Random();

    public WindCurseEffect(MobEffectCategory pCategory, int pColor)
    {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return pDuration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier)
    {
        if(this.random.nextFloat() < 0.2F && !pLivingEntity.isInvulnerable())
        {
            float limit = 0.4F + (0.25F * (pAmplifier - 1));
            float yLimit = 1.1F + (0.1F * (pAmplifier - 1));
            float modifier = 1.0F + (0.25F * (pAmplifier - 1));

            float dX = (this.random.nextFloat() * limit) * (this.random.nextBoolean() ? -1 : 1);
            float dZ = (this.random.nextFloat() * limit) * (this.random.nextBoolean() ? -1 : 1);
            float dY = 0.1F + this.random.nextFloat() * yLimit;

            float damage = 1.25F * pAmplifier;

            pLivingEntity.push(dX * modifier, dY, dZ * modifier);
            pLivingEntity.hurt(DamageSource.MAGIC, damage);
        }
    }
}
