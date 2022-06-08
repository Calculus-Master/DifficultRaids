package com.calculusmaster.difficultraids.entity.entities.component;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractPillagerVariant;
import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
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

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.33F)
                .add(Attributes.FOLLOW_RANGE, 7.0D)
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
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
        if(DifficultRaidsUtil.isGuardVillagersLoaded()) this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Guard.class, 8.0F, 0.5D, 0.7D) {
            @Override
            public boolean canUse() { return super.canUse() && VoldonFamiliarEntity.this.isInHideState(); }
        });

        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.7D, true));
        this.goalSelector.addGoal(5, new VoldonFamiliarReturnGoal());

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        if(DifficultRaidsUtil.isGuardVillagersLoaded()) this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Guard.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
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

        if(pCause.getEntity() instanceof LivingEntity living && (pCause.getEntity() instanceof Player || (DifficultRaidsUtil.isGuardVillagersLoaded() && pCause.getEntity() instanceof Guard)))
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3));

        if(this.voldon != null) this.voldon.removeFamiliar(this);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        //TODO: Raid Buffs for Familiars
    }

    public boolean isInHideState()
    {
        return this.getHealth() < this.getMaxHealth() / 4;
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();

        if(this.voldon != null && this.voldon.isDeadOrDying())
        {
            this.hurt(DamageSource.STARVE, this.getHealth() + 1.0F);
            return;
        }

        if(this.getTarget() != null && !this.isInHideState())
        {
            double distance = this.distanceTo(this.getTarget());
            Vec3 targetPos = this.getTarget().position();

            if(distance > 3.0 && this.random.nextInt(100) < 2) this.randomTeleport(targetPos.x, targetPos.y + 0.2, targetPos.z, true);
        }

        if(this.voldon != null)
        {
            double voldonDistance = this.distanceTo(this.voldon);

            if(voldonDistance > 15.0) this.getNavigation().moveTo(this.voldon, 0.9D);
        }
        else LogUtils.getLogger().warn("Voldon Familiar (ID {%s}, Pos {%s}) has a null Voldon attribute!".formatted(this.getId(), this.blockPosition()));
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
