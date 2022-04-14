package com.calculusmaster.difficultraids.entity.entities.component;

import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.List;

public class ShamanDebuffBulletEntity extends ShulkerBullet
{
    private List<MobEffectInstance> debuffs;

    public ShamanDebuffBulletEntity(EntityType<? extends ShulkerBullet> p_37319_, Level p_37320_)
    {
        super(p_37319_, p_37320_);
        this.debuffs = new ArrayList<>();
    }

    private ShamanDebuffBulletEntity(Level p_37330_, LivingEntity p_37331_, Entity p_37332_, Direction.Axis p_37333_)
    {
        super(p_37330_, p_37331_, p_37332_, p_37333_);
        this.debuffs = new ArrayList<>();
    }

    public static ShamanDebuffBulletEntity create(Level level, LivingEntity owner, Entity target, Direction.Axis axis)
    {
        return new ShamanDebuffBulletEntity(level, owner, target, axis);
    }

    public void loadDebuff(MobEffectInstance debuff)
    {
        this.debuffs.add(debuff);
    }

    @Override
    public boolean canCollideWith(Entity pEntity)
    {
        return super.canCollideWith(pEntity) && !(pEntity instanceof Raider);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult)
    {
        if(this.getOwner() instanceof LivingEntity owner && pResult.getEntity() instanceof LivingEntity hitEntity && !(hitEntity instanceof Raider))
        {
            boolean hitSuccess = hitEntity.hurt(DamageSource.indirectMobAttack(this, owner), 3.0F);
            if(hitSuccess) this.doEnchantDamageEffects(owner, this);

            this.getModifiedDebuffs(hitSuccess ? 1.0 : 0.5).forEach(d -> hitEntity.addEffect(d, this));
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult p_37343_)
    {
        BlockPos pos = p_37343_.getBlockPos();
        AABB applyRange = new AABB(pos).inflate(2.0);

        if(this.level.isClientSide)
        {
            this.level.addParticle(ParticleTypes.ASH, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.2D, 0.0D);
        }

        List<LivingEntity> targets = new ArrayList<>();
        targets.addAll(this.level.getEntitiesOfClass(Player.class, applyRange));
        targets.addAll(this.level.getEntitiesOfClass(IronGolem.class, applyRange));
        targets.addAll(this.level.getEntitiesOfClass(AbstractVillager.class, applyRange));
        if(DifficultRaidsUtil.isGuardVillagersLoaded()) targets.addAll(this.level.getEntitiesOfClass(Guard.class, applyRange));

        List<MobEffectInstance> debuffs = this.getModifiedDebuffs(0.75);
        targets.forEach(living -> debuffs.forEach(living::addEffect));
    }

    private List<MobEffectInstance> getModifiedDebuffs(double durationModifier)
    {
        return this.debuffs.stream().map(i -> {
            int duration = (int)(i.getDuration() * durationModifier);
            return new MobEffectInstance(i.getEffect(), duration, i.getAmplifier(), i.isAmbient(), i.isVisible(), i.showIcon());
        }).toList();
    }
}
