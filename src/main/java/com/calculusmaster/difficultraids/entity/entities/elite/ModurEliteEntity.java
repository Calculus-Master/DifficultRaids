package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.config.RaiderConfigs;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.Compat;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.stream.Stream;

public class ModurEliteEntity extends AbstractEvokerVariant implements RangedAttackMob
{
    private final Component ELITE_NAME = Component.translatable("com.calculusmaster.difficultraids.elite_event.modur");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);

    private int stormTicks;
    private AABB stormAABB;

    private int chargedBoltWarmup;
    private int chargedBoltWarmupTotal;
    private BlockPos chargedBoltPos;

    private int homingBoltTicks;
    private BlockPos homingBoltPos;

    public ModurEliteEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);

        this.stormTicks = 0;
        this.stormAABB = new AABB(BlockPos.ZERO);

        this.chargedBoltWarmup = 0;
        this.chargedBoltWarmupTotal = 0;
        this.chargedBoltPos = BlockPos.ZERO;

        this.homingBoltTicks = 0;
        this.homingBoltPos = BlockPos.ZERO;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new ModurCastSpellGoal());
        this.goalSelector.addGoal(2, new ModurSummonThunderSpellGoal());
        this.goalSelector.addGoal(3, new ModurLightningStormSpellGoal());
        this.goalSelector.addGoal(3, new ModurChargedBoltSpellGoal());
        this.goalSelector.addGoal(3, new ModurHomingBoltSpellGoal());
        this.goalSelector.addGoal(4, new ModurLightningZapSpellGoal());
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Player.class, 4.0F, 0.6D, 0.75D));
        this.goalSelector.addGoal(6, new RangedAttackGoal(this, 0.7F, 130, 12.0F));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        if(Compat.GUARD_VILLAGERS.isLoaded()) this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Guard.class, 6.0F, 0.6D, 0.75D));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getEntity() instanceof IronGolem || (Compat.GUARD_VILLAGERS.isLoaded() && pSource.getEntity() instanceof Guard))
            pAmount *= this.config().modur.friendlyDamageReduction;

        if(pSource.getDirectEntity() instanceof Projectile) pAmount *= this.config().modur.projectileDamageReduction;

        if(this.isStormActive()) pAmount *= 1.2;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity)
    {
        ModurEliteEntity.this.spawnCustomBolt(pTarget.blockPosition(), this.config().modur.basicLightningStrikeDamage);
    }

    @Override
    public void die(DamageSource pCause)
    {
        super.die(pCause);

        this.spawnCustomBolt(this.blockPosition().offset(0, 0.2, 0), 15.0F);

        if(!this.level.isClientSide && this.level.isThundering()) ((ServerLevel)ModurEliteEntity.this.level).setWeatherParameters(6000, 0, false, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("StormTicks", this.stormTicks);
        pCompound.putIntArray("StormAABB", new int[]{(int)this.stormAABB.minX, (int)this.stormAABB.maxX, (int)this.stormAABB.minY, (int)this.stormAABB.maxY, (int)this.stormAABB.minZ, (int)this.stormAABB.maxZ});
        pCompound.putInt("ChargedBoltWarmup", this.chargedBoltWarmup);
        pCompound.putInt("ChargedBoltWarmupTotal", this.chargedBoltWarmupTotal);
        pCompound.putIntArray("ChargedBoltPos", new int[]{this.chargedBoltPos.getX(), this.chargedBoltPos.getY(), this.chargedBoltPos.getZ()});
        pCompound.putInt("HomingBoltTicks", this.homingBoltTicks);
        pCompound.putIntArray("HomingBoltPos", new int[]{this.homingBoltPos.getX(), this.homingBoltPos.getY(), this.homingBoltPos.getZ()});
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.stormTicks = pCompound.getInt("StormTicks");

        int[] dataAABB = pCompound.getIntArray("StormAABB");
        this.stormAABB = dataAABB.length == 6 ? new AABB(dataAABB[0], dataAABB[1], dataAABB[2], dataAABB[3], dataAABB[4], dataAABB[5]) : new AABB(BlockPos.ZERO);

        this.chargedBoltWarmup = pCompound.getInt("ChargedBoltWarmup");
        this.chargedBoltWarmupTotal = pCompound.getInt("ChargedBoltWarmupTotal");

        int[] dataBoltPos = pCompound.getIntArray("ChargedBoltPos");
        this.chargedBoltPos = dataBoltPos.length == 3 ? new BlockPos(dataBoltPos[0], dataBoltPos[1], dataBoltPos[2]) : BlockPos.ZERO;

        this.homingBoltTicks = pCompound.getInt("HomingBoltTicks");

        int[] dataHomingBoltPos = pCompound.getIntArray("HomingBoltPos");
        this.homingBoltPos = dataHomingBoltPos.length == 3 ? new BlockPos(dataHomingBoltPos[0], dataHomingBoltPos[1], dataHomingBoltPos[2]) : BlockPos.ZERO;
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        this.ELITE_EVENT.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit)
    {
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_LIGHTNING.get()));
    }

    public boolean isInExtendedSpellState()
    {
        return this.stormTicks > 0 || this.chargedBoltWarmup > 0 || this.homingBoltTicks > 0;
    }

    public boolean isStormActive()
    {
        return this.stormTicks > 0;
    }

    private void spawnCustomBolt(BlockPos spawn, float damage)
    {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(this.level); if(bolt == null) return;

        if(this.level.isThundering()) damage *= 1.25F;

        bolt.setCustomName(Component.literal(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG));
        bolt.setDamage(damage);
        bolt.moveTo(spawn, 0.0F, 0.0F);

        this.level.addFreshEntity(bolt);
    }

    @Override
    public void tick()
    {
        super.tick();
        RaiderConfigs.Modur cfg = this.config().modur;

        if(this.isStormActive())
        {
            int strikes = cfg.stormStrikesPerTick;

            if(this.stormTicks % 2 == 0) for(int i = 0; i < strikes; i++)
            {
                int strikeX = this.random.nextInt((int)this.stormAABB.minX, (int)(this.stormAABB.maxX + 1));
                int strikeY = (int)this.stormAABB.minY;
                int strikeZ = this.random.nextInt((int)this.stormAABB.minZ, (int)(this.stormAABB.maxZ + 1));

                BlockPos strikePos = new BlockPos(strikeX, strikeY, strikeZ);
                int tries = 0; while(!this.level.getBlockState(strikePos).isAir() && tries++ < 20) strikePos = strikePos.above(1);

                ModurEliteEntity.this.spawnCustomBolt(strikePos, cfg.stormStrikeDamage);
            }

            this.stormTicks--;
            if(this.stormTicks == 0) this.stormAABB = new AABB(BlockPos.ZERO);
        }

        if(this.chargedBoltWarmup > 0)
        {
            //Sounds
            if(this.chargedBoltWarmup % 30 == 0) this.level.playLocalSound(this.chargedBoltPos.getX(), this.chargedBoltPos.getY(), this.chargedBoltPos.getZ(),
                    SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 2.0F, 0.7F, false);

            //Particles
            int tiers = 2 + (this.chargedBoltWarmupTotal - this.chargedBoltWarmup) / 20;

            for(int i = 0; i < tiers; i++)
            {
                Stream.of(
                        this.chargedBoltPos.offset(0, i, 0),
                        this.chargedBoltPos.offset(1, i, 0),
                        this.chargedBoltPos.offset(0, i, 1),
                        this.chargedBoltPos.offset(1, i, 1)
                ).forEach(pos -> ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.WHITE_WOOL.defaultBlockState()), pos.getX(), pos.getY(), pos.getZ(), 2, 0.15, 0, 0.15, 2.0));
            }

            //Logic
            this.chargedBoltWarmup--;

            if(this.chargedBoltWarmup <= 0)
            {
                for(int i = 0; i < cfg.chargedBoltCount; i++)
                    this.spawnCustomBolt(this.chargedBoltPos, cfg.chargedBoltDamage);

                this.chargedBoltWarmup = 0;
                this.chargedBoltWarmupTotal = 0;
            }
        }

        if(this.homingBoltTicks > 0 && this.homingBoltTicks-- % 10 == 0 && this.getTarget() != null)
        {
            LivingEntity target = this.getTarget();

            double x = target.getX() - this.getX();
            double z = target.getZ() - this.getZ();

            this.homingBoltPos = this.homingBoltPos.offset(x == 0 ? 0 : x < 0 ? -1 : 1, 0, z == 0 ? 0 : z < 0 ? -1 : 1);

            this.spawnCustomBolt(this.homingBoltPos, cfg.homingBoltDamage);
        }
    }

    private class ModurCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        @Override
        public void tick()
        {
            if(ModurEliteEntity.this.getTarget() != null)
                ModurEliteEntity.this.getLookControl().setLookAt(ModurEliteEntity.this.getTarget(), (float)ModurEliteEntity.this.getMaxHeadYRot(), (float)ModurEliteEntity.this.getMaxHeadXRot());
        }
    }

    private class ModurHomingBoltSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        protected void castSpell()
        {
            ModurEliteEntity modur = ModurEliteEntity.this;

            Vec3 v = ModurEliteEntity.this.getLookAngle();
            modur.homingBoltPos = modur.blockPosition().offset(v.x, 0, v.z);

            modur.homingBoltTicks = modur.config().modur.homingBoltTime;
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !ModurEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 70;
        }

        @Override
        protected int getCastingInterval()
        {
            return 500;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 15;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_HOMING_BOLT;
        }
    }

    private class ModurChargedBoltSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        protected void castSpell()
        {
            ModurEliteEntity.this.chargedBoltWarmup = 20 * 10;
            ModurEliteEntity.this.chargedBoltWarmupTotal = ModurEliteEntity.this.chargedBoltWarmup;

            if(ModurEliteEntity.this.getTarget() != null) ModurEliteEntity.this.chargedBoltPos = ModurEliteEntity.this.getTarget().blockPosition();
            else ModurEliteEntity.this.chargedBoltPos = ModurEliteEntity.this.blockPosition().offset(5 - ModurEliteEntity.this.random.nextInt(1, 11), 0, 5 - ModurEliteEntity.this.random.nextInt(1, 11));
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !ModurEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 60;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 15;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.LIGHTNING_BOLT_THUNDER;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_CHARGED_BOLT;
        }
    }

    private class ModurLightningZapSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private BlockPos targetPos;

        private ModurLightningZapSpellGoal() { this.targetPos = BlockPos.ZERO; }

        @Override
        protected void castSpell()
        {
            if(!this.targetPos.equals(BlockPos.ZERO))
            {
                for(int i = 0; i < 3; i++)
                    ModurEliteEntity.this.spawnCustomBolt(this.targetPos.offset(0.5 - Math.random(), 0, 0.5 - Math.random()),
                            ModurEliteEntity.this.config().modur.zapBoltDamage);
            }
        }

        @Override
        public void start()
        {
            super.start();
            if(ModurEliteEntity.this.getTarget() != null) this.targetPos = new BlockPos(ModurEliteEntity.this.getTarget().blockPosition());
        }

        @Override
        public void tick()
        {
            //Capture position just before the cast
            if(this.spellWarmup == 5 && ModurEliteEntity.this.getTarget() != null) this.targetPos = new BlockPos(ModurEliteEntity.this.getTarget().blockPosition());

            super.tick();
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !ModurEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastingInterval()
        {
            return 160;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 15;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.TRIDENT_THUNDER;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_LIGHTNING_ZAP;
        }
    }

    private class ModurLightningStormSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ModurLightningStormSpellGoal() {}

        @Override
        protected void castSpell()
        {
            ModurEliteEntity modur = ModurEliteEntity.this;

            modur.stormTicks = modur.config().modur.stormDuration;

            modur.stormAABB = new AABB(modur.blockPosition())
                    .inflate(modur.config().modur.stormRadius)
                    .setMaxY(modur.getEyeY() + modur.getBbHeight())
                    .setMinY(modur.blockPosition().getY() + 0.4);
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && ModurEliteEntity.this.level.isThundering() && !ModurEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 1400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 15;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.LIGHTNING_BOLT_IMPACT;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_LIGHTNING_STORM;
        }
    }

    private class ModurSummonThunderSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ModurSummonThunderSpellGoal() {}

        @Override
        protected void castSpell()
        {
            ((ServerLevel)ModurEliteEntity.this.level).setWeatherParameters(0, 20 * 60 * 5, true, true);
        }

        @Override
        public boolean canUse()
        {
            return !ModurEliteEntity.this.level.getLevelData().isThundering();
        }

        @Override
        protected int getCastingTime()
        {
            return 100;
        }

        @Override
        protected int getCastingInterval()
        {
            return 500;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 20;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.LIGHTNING_BOLT_THUNDER;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_SUMMON_THUNDER;
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer)
    {
        super.startSeenByPlayer(pPlayer);
        this.ELITE_EVENT.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer)
    {
        super.stopSeenByPlayer(pPlayer);
        this.ELITE_EVENT.removePlayer(pPlayer);
    }
}
