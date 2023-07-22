package com.calculusmaster.difficultraids.entity.entities.core;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public abstract class AbstractEvokerVariant extends AbstractIllagerVariant
{
    private static final EntityDataAccessor<Byte> SPELL_DATA = SynchedEntityData.defineId(AbstractEvokerVariant.class, EntityDataSerializers.BYTE);
    protected int spellTicks;
    private SpellType activeSpell = SpellType.NONE;

    protected AbstractEvokerVariant(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(SPELL_DATA, (byte)0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);
        this.spellTicks = pCompound.getInt("SpellTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SpellTicks", this.spellTicks);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public IllagerArmPose getArmPose()
    {
        if(this.isCastingSpell()) return AbstractIllager.IllagerArmPose.SPELLCASTING;
        else return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
    }

    public boolean isCastingSpell()
    {
        if(this.level.isClientSide) return this.entityData.get(SPELL_DATA) > 0;
        else return this.spellTicks > 0;
    }

    public void setSpellType(SpellType spellType)
    {
        this.activeSpell = spellType;
        this.entityData.set(SPELL_DATA, (byte)spellType.ID);
    }

    protected SpellType getSpellType()
    {
        return !this.level.isClientSide ? this.activeSpell : SpellType.getFromID(this.entityData.get(SPELL_DATA));
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();

        if(this.spellTicks > 0) this.spellTicks--;
    }

    protected boolean spawnDefaultSpellcastingParticles()
    {
        return true;
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.level.isClientSide && this.isCastingSpell() && this.spawnDefaultSpellcastingParticles())
        {
            SpellType spellType = this.getSpellType();
            double d0 = spellType.spellColor[0];
            double d1 = spellType.spellColor[1];
            double d2 = spellType.spellColor[2];
            float f = this.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double)f2 * 0.6D, d0, d1, d2);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double)f2 * 0.6D, d0, d1, d2);
        }
    }

    protected int getSpellTicks()
    {
        return this.spellTicks;
    }

    protected SoundEvent getSpellSound()
    {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    public enum SpellType
    {
        NONE(0, 0.0D, 0.0D, 0.0D),
        ELECTRO_SUMMON_BASIC_LIGHTNING_BOLTS(1, 0.56D, 0.89D, 0.96D),
        ELECTRO_LIGHTNING_RING(2, 0.20D, 0.90D, 0.80D),
        ELECTRO_CONCENTRATED_BOLT(3, 0.9D, 0.1D, 0.1D),
        ELECTRO_SLOWNESS_BOLT(4, 0.5D , 0.5D, 0.5D),
        NECROMANCER_SUMMON_MINIONS(5, 0.1D, 0.1D, 0.1D),
        NECROMANCER_SUMMON_HORDE(6, 0.5D, 0.05D, 0.5D),
        NECROMANCER_BURY_TARGET(7, 0.0D, 0.9D, 0.2D),
        SHAMAN_ATTACK_BOOST(8, 0.3D, 0.9D, 0.0D),
        SHAMAN_DEFENSE_BOOST(9, 0.0D, 0.9D, 0.3D),
        SHAMAN_DEBUFF(10, 0.1D, 0.1D, 0.1D),
        SHAMAN_INVISIBILITY(11, 0.2D, 0.3D, 0.4D),
        FROST_FREEZE(12, 0.5D, 0.5D, 1.0D),
        FROST_BARRAGE(13, 0.5, 0.6D, 1.0D),
        FROST_SNOWBALL_BLAST(14, 0.9D, 0.3D, 1.0D),

        XYDRAX_WIND_BLAST(15, 0.1D, 0.1D, 0.1D),
        XYDRAX_ARROW_BARRAGE(16, 0.3D, 0.0D, 0.1D),
        XYDRAX_HEAL(17, 0.9D, 0.3D, 0.2D),
        XYDRAX_WIND_COLUMN(18, 1.0D, 1.0D, 1.0D),
        XYDRAX_VORTEX(19, 0.7D, 0.7D, 1.0D),

        MODUR_SUMMON_THUNDER(20, 0.5D, 0.5D, 0.5D),
        MODUR_LIGHTNING_STORM(21, 1.0D, 0.9D, 0.9D),
        MODUR_LIGHTNING_ZAP(22, 1.0D, 0.2D, 1.0D),
        MODUR_CHARGED_BOLT(23, 1.0D, 0.5D, 1.0D),
        MODUR_HOMING_BOLT(24, 0.5D, 0.5D, 0.5D),

        VOLDON_SUMMON_FAMILIARS(25, 0.05D, 0.05D, 0.05D),
        VOLDON_TELEPORT_FAMILIAR(26, 0.8D, 0.1D, 0.5D),
        VOLDON_SACRIFICE_FAMILIAR(27, 0.6D, 0.6D, 0.1D),

        ASHENMANCER_SUMMON_MINIONS(28, 0.1D, 0.1D, 0.1D),
        ASHENMANCER_SHOOT_SKULL(29, 0.1D, 0.1D, 0.4D),
        ASHENMANCER_ASHENADO(30, 0.05, 0.05, 0.05),
        ASHENMANCER_TURRET(31, 0.8, 0.35, 0.07)

        ;

        final int ID;
        final double[] spellColor;

        SpellType(int ID, double spellColor1, double spellColor2, double spellColor3)
        {
            this.ID = ID;
            this.spellColor = new double[]{spellColor1, spellColor2, spellColor3};
        }

        public static SpellType getFromID(int ID)
        {
            for(SpellType type : values()) if(type.ID == ID) return type;
            return NONE;
        }

        public double getColor(int index)
        {
            return this.spellColor[index];
        }
    }

    public abstract class SpellcastingIllagerCastSpellGoal extends Goal
    {
        public SpellcastingIllagerCastSpellGoal()
        {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            return AbstractEvokerVariant.this.getSpellTicks() > 0;
        }

        @Override
        public void start()
        {
            super.start();
            AbstractEvokerVariant.this.navigation.stop();
        }

        @Override
        public void stop()
        {
            super.stop();
            AbstractEvokerVariant.this.setSpellType(SpellType.NONE);
        }

        @Override
        public void tick()
        {
            super.tick();
            if(AbstractEvokerVariant.this.getTarget() != null)
                AbstractEvokerVariant.this.getLookControl().setLookAt(AbstractEvokerVariant.this.getTarget(), (float) AbstractEvokerVariant.this.getMaxHeadYRot(), (float) AbstractEvokerVariant.this.getMaxHeadXRot());
        }
    }

    public abstract class SpellcastingIllagerUseSpellGoal extends Goal
    {
        protected int spellWarmup;
        protected int spellCooldown;

        protected SpellcastingIllagerUseSpellGoal(Flag... flags)
        {
            if(flags.length > 0) this.setFlags(EnumSet.copyOf(List.of(flags)));
        }

        @Override
        public boolean canUse()
        {
            LivingEntity entity = AbstractEvokerVariant.this.getTarget();

            if(entity != null && entity.isAlive())
            {
                if(AbstractEvokerVariant.this.isCastingSpell()) return false;
                else return AbstractEvokerVariant.this.tickCount >= this.spellCooldown;
            }
            else return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity entity = AbstractEvokerVariant.this.getTarget();
            return entity != null && entity.isAlive() && this.spellWarmup > 0;
        }

        @Override
        public void start()
        {
            this.spellWarmup = this.getCastWarmupTime();
            AbstractEvokerVariant.this.spellTicks = this.getCastingTime();
            this.spellCooldown = AbstractEvokerVariant.this.tickCount + this.getCastingInterval();

            SoundEvent soundEvent = this.getSpellPrepareSound();
            if(soundEvent != null) AbstractEvokerVariant.this.playSound(soundEvent, 1.0F, 1.0F);

            AbstractEvokerVariant.this.setSpellType(this.getSpellType());
        }

        @Override
        public void tick()
        {
            this.spellWarmup--;

            if(this.spellWarmup == 0)
            {
                this.castSpell();
                AbstractEvokerVariant.this.playSound(AbstractEvokerVariant.this.getSpellSound(), 1.0F, 1.0F);
            }
        }

        protected abstract void castSpell();

        protected abstract int getCastWarmupTime();

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract SpellType getSpellType();
    }

    //Default Evoker Sounds
    @Override
    public SoundEvent getCelebrateSound()
    {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_)
    {
        return SoundEvents.EVOKER_HURT;
    }
}
