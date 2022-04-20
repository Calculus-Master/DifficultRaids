package com.calculusmaster.difficultraids.entity.entities.core;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPillagerVariant extends AbstractIllagerVariant
{
    public AbstractPillagerVariant(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
    }

    //Default Arm Pose
    @Override
    public IllagerArmPose getArmPose()
    {
        return this.isAggressive() ? IllagerArmPose.ATTACKING : IllagerArmPose.NEUTRAL;
    }

    //Default Sounds
    @Override
    public SoundEvent getCelebrateSound()
    {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_)
    {
        return SoundEvents.PILLAGER_HURT;
    }
}
