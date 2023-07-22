package com.calculusmaster.difficultraids.entity.entities.component;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractPillagerVariant;
import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.util.Compat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tallestegg.guardvillagers.entities.Guard;

import java.util.EnumSet;

public class VoldonFamiliarEntity extends AbstractPillagerVariant
{
    private VoldonEliteEntity voldon;

    public VoldonFamiliarEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);

        this.voldon = null;
    }

    public VoldonFamiliarEntity(Level level, VoldonEliteEntity voldon)
    {
        super(DifficultRaidsEntityTypes.VOLDON_FAMILIAR.get(), level);

        this.voldon = voldon;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));

        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.5D, 0.7D) {
            @Override
            public boolean canUse() { return super.canUse() && VoldonFamiliarEntity.this.isInHideState(); }
        });
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, IronGolem.class, 8.0F, 0.5D, 0.7D) {
            @Override
            public boolean canUse() { return super.canUse() && VoldonFamiliarEntity.this.isInHideState(); }
        });
        if(Compat.GUARD_VILLAGERS.isLoaded()) this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Guard.class, 8.0F, 0.5D, 0.7D) {
            @Override
            public boolean canUse() { return super.canUse() && VoldonFamiliarEntity.this.isInHideState(); }
        });

        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.7D, false));
        this.goalSelector.addGoal(5, new VoldonFamiliarReturnGoal());

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        if(Compat.GUARD_VILLAGERS.isLoaded()) this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Guard.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.voldon = pCompound.getInt("VoldonID") == -1 ? null : (VoldonEliteEntity)this.level.getEntity(pCompound.getInt("VoldonID"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("VoldonID", this.voldon == null ? -1 : this.voldon.getId());
    }

    @Override
    public void die(DamageSource pCause)
    {
        super.die(pCause);

        if(this.config().voldon.familiarWeaknessOnDeath &&
                pCause.getEntity() instanceof LivingEntity living && (pCause.getEntity() instanceof Player || (Compat.GUARD_VILLAGERS.isLoaded() && pCause.getEntity() instanceof Guard)))
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3));

        if(this.voldon != null) this.voldon.removeFamiliar(this);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        AttributeModifier attackModifier = new AttributeModifier("VOLDON_FAMILIAR_RAID_ATTACK_BOOST",
                this.config().voldon.familiarAttackDamageMultiplier,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        AttributeModifier healthModifier = new AttributeModifier("VOLDON_FAMILIAR_MAX_HEALTH_INCREASE",
                this.config().voldon.familiarHealthMultiplier,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if(attackDamage != null) attackDamage.addPermanentModifier(attackModifier);

        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if(health != null) health.addPermanentModifier(healthModifier);
    }

    public boolean isInHideState()
    {
        return this.getHealth() < this.getMaxHealth() / 4;
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();

        if(this.voldon == null || this.voldon.isDeadOrDying())
        {
            this.hurt(DamageSource.STARVE, this.getHealth() + 1.0F);
            return;
        }

        if(this.getTarget() != null && !this.isInHideState())
        {
            double distance = this.distanceTo(this.getTarget());
            Vec3 targetPos = this.getTarget().position();

            if(distance > 3.0 && this.random.nextFloat() < 0.02F) this.randomTeleport(targetPos.x, targetPos.y + 0.2, targetPos.z, true);
        }

        if(this.distanceTo(this.voldon) >= 40)
            this.randomTeleport(this.voldon.getX() + (2 - this.random.nextInt(5)), this.voldon.getY(), this.voldon.getZ() + (2 - this.random.nextInt(5)), true);
    }

    private class VoldonFamiliarReturnGoal extends Goal
    {
        private VoldonFamiliarReturnGoal() { this.setFlags(EnumSet.of(Flag.MOVE)); }

        private boolean hasVoldon()
        {
            return VoldonFamiliarEntity.this.voldon != null && VoldonFamiliarEntity.this.voldon.isAlive();
        }

        private double getDistanceToVoldon()
        {
            return VoldonFamiliarEntity.this.distanceTo(VoldonFamiliarEntity.this.voldon);
        }

        @Override
        public boolean canUse()
        {
            return this.hasVoldon() && this.getDistanceToVoldon() > 15.0;
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.hasVoldon() && this.getDistanceToVoldon() > 5.0;
        }

        @Override
        public void start()
        {
            VoldonFamiliarEntity.this.getNavigation().moveTo(VoldonFamiliarEntity.this.voldon, 0.8D);
        }

        @Override
        public void stop()
        {
            VoldonFamiliarEntity.this.getNavigation().stop();
        }
    }
}
