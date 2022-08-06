package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class XydraxEliteEntity extends AbstractEvokerVariant
{
    private final TextComponent ELITE_NAME = new TextComponent("Xydrax, Windborne");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private boolean isHealing;

    private int windColumnTicks;
    private BlockPos windColumnCenterPos;
    private AABB windColumnAABB;
    private List<BlockPos> windColumnParticleSpawnPositions;

    public XydraxEliteEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    public static AttributeSupplier.Builder createEliteAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.39F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 85.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new XydraxCastSpellGoal());
        this.goalSelector.addGoal(2, new XydraxAvoidEntityGoal( 4.0F, 0.7D, 0.9D));
        this.goalSelector.addGoal(3, new XydraxHealGoal());
        this.goalSelector.addGoal(4, new XydraxWindBlastGoal());
        this.goalSelector.addGoal(4, new XydraxWindColumnGoal());
        this.goalSelector.addGoal(5, new XydraxBarrageGoal());

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        if(DifficultRaidsUtil.isGuardVillagersLoaded()) this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Guard.class, 8.0F, 0.7D, 1.0D));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        //Armor
        Map<Enchantment, Integer> generalEnchants = new HashMap<>();
        generalEnchants.put(Enchantments.ALL_DAMAGE_PROTECTION, 1);
        generalEnchants.put(Enchantments.VANISHING_CURSE, 1);

        ItemStack helm = new ItemStack(Items.DIAMOND_HELMET);
        ItemStack chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
        ItemStack legs = new ItemStack(Items.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);

        EnchantmentHelper.setEnchantments(generalEnchants, helm);
        EnchantmentHelper.setEnchantments(generalEnchants, chest);
        EnchantmentHelper.setEnchantments(generalEnchants, legs);
        EnchantmentHelper.setEnchantments(generalEnchants, boots);

        this.setItemSlot(EquipmentSlot.HEAD, helm);
        this.setItemSlot(EquipmentSlot.CHEST, chest);
        this.setItemSlot(EquipmentSlot.LEGS, legs);
        this.setItemSlot(EquipmentSlot.FEET, boots);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("IsHealing", this.isHealing);
        pCompound.putInt("WindColumnTicks", this.windColumnTicks);
        pCompound.putIntArray("WindColumnCenterPos", new int[]{this.windColumnCenterPos.getX(), this.windColumnCenterPos.getY(), this.windColumnCenterPos.getZ()});
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.isHealing = pCompound.getBoolean("IsHealing");
        this.windColumnTicks = pCompound.getInt("WindColumnTicks");

        int[] posArray = pCompound.getIntArray("WindColumnCenterPos");
        this.windColumnCenterPos = posArray.length == 3 ? new BlockPos(posArray[0], posArray[1], posArray[2]) : BlockPos.ZERO;

        if(this.windColumnTicks > 0 && !this.windColumnCenterPos.equals(BlockPos.ZERO)) this.buildWindColumn(this.windColumnCenterPos);
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        this.ELITE_EVENT.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getEntity() instanceof IronGolem || (DifficultRaidsUtil.isGuardVillagersLoaded() && pSource.getEntity() instanceof Guard))
            pAmount *= 0.4;

        if(pSource.getDirectEntity() instanceof LivingEntity living && this.random.nextFloat() < 0.2)
            living.push(0.0D, this.random.nextFloat() * 0.7, 0.0D);

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit)
    {
        //TODO: Xydrax Unique Raid Loot - the Totem reward is temporary
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_TELEPORTATION.get()));
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.isHealing())
        {
            if(this.isOnGround())
            {
                this.removeEffect(MobEffects.SLOW_FALLING);
                this.isHealing = false;
            }
            else
            {
                float currentHealth = this.getHealth();
                float maxHealth = this.getMaxHealth();

                if(maxHealth - currentHealth > 0.5F)
                {
                    float healAmount = this.random.nextFloat();
                    this.heal(healAmount);

                    //TODO: How do you slow the descent even more?
                    //if(this.random.nextFloat() < 0.2F) this.push(0, 0.2, 0);
                }
            }
        }
        else if(this.isWindColumnActive())
        {
            this.windColumnTicks--;
            if(this.getNavigation().isInProgress()) this.getNavigation().stop();

            //Particles
            if(this.level.isClientSide) this.windColumnParticleSpawnPositions.forEach(pos -> {
                Supplier<Triple<Double, Double, Double>> particleSpawn = () -> Triple.of(pos.getX() + this.random.nextFloat() - 0.5, pos.getY() + 3.0, pos.getZ() + this.random.nextFloat() - 0.5);
                for(int i = 0; i < 3; i++)
                {
                    //FIXME: Particles not working
                    Triple<Double, Double, Double> spawn = particleSpawn.get();
                    this.level.addParticle(ParticleTypes.EXPLOSION, spawn.getLeft(), spawn.getMiddle(), spawn.getRight(), 0.05, 0.6, 0.05);
                }
            });

            //Wind Column Logic
            List<EntityType<?>> validTypes = new ArrayList<>(List.of(EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.PLAYER));
            if(DifficultRaidsUtil.isGuardVillagersLoaded()) validTypes.add(GuardEntityType.GUARD.get());

            List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, this.windColumnAABB, e -> {
                if(e instanceof Player player) return !player.isSpectator() && !player.isCreative();
                else return validTypes.contains(e.getType());
            });

            targets.forEach(living -> living.push(this.random.nextFloat() / 3, 0.7, this.random.nextFloat() / 3));

            //Despawn Wind Column
            if(this.windColumnTicks == 0)
            {
                this.windColumnCenterPos = BlockPos.ZERO;
                this.windColumnAABB = new AABB(BlockPos.ZERO);
                this.windColumnParticleSpawnPositions = List.of();
            }
        }
    }

    private Vec3 vectorTo(LivingEntity other)
    {
        Vec3 thisPos = this.getEyePosition();
        Vec3 otherPos = other.getEyePosition();

        return new Vec3(otherPos.x - thisPos.x, otherPos.y - thisPos.y, otherPos.z - thisPos.z);
    }

    public boolean isHealing()
    {
        return this.isHealing;
    }

    public boolean isWindColumnActive()
    {
        return this.windColumnTicks > 0;
    }

    private void buildWindColumn(BlockPos thisPos)
    {
        this.windColumnCenterPos = new BlockPos(thisPos);

        this.windColumnAABB = new AABB(this.windColumnCenterPos).inflate(2.0);

        double yOffset = 0.3;
        this.windColumnParticleSpawnPositions = List.of(
                this.windColumnCenterPos.offset(2, yOffset, 0),
                this.windColumnCenterPos.offset(-2, yOffset, 0),
                this.windColumnCenterPos.offset(0, yOffset, 2),
                this.windColumnCenterPos.offset(0, yOffset, -2),
                this.windColumnCenterPos.offset(2, yOffset, 2),
                this.windColumnCenterPos.offset(-2, yOffset, 2),
                this.windColumnCenterPos.offset(2, yOffset, -2),
                this.windColumnCenterPos.offset(-2, yOffset, -2)
        );
    }

    //For spells that last beyond their goals, like healing or wind column
    private boolean isInExtendedSpellState()
    {
        return this.isHealing() || this.isWindColumnActive();
    }

    private class XydraxAvoidEntityGoal extends AvoidEntityGoal<LivingEntity>
    {
        public XydraxAvoidEntityGoal(float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier)
        {
            super(XydraxEliteEntity.this, LivingEntity.class, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier, e -> (e instanceof Player player && !player.isCreative() && !player.isSpectator()) || (DifficultRaidsUtil.isGuardVillagersLoaded() && e instanceof Guard));
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !XydraxEliteEntity.this.isInExtendedSpellState() && !XydraxEliteEntity.this.isCastingSpell();
        }
    }

    private class XydraxCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        private XydraxCastSpellGoal() {}

        @Override
        public void tick()
        {
            if(XydraxEliteEntity.this.getTarget() != null)
                XydraxEliteEntity.this.getLookControl().setLookAt(XydraxEliteEntity.this.getTarget(), (float)XydraxEliteEntity.this.getMaxHeadYRot(), (float)XydraxEliteEntity.this.getMaxHeadXRot());
        }
    }

    private class XydraxWindColumnGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxWindColumnGoal() {}

        @Override
        protected void castSpell()
        {
            XydraxEliteEntity xydrax = XydraxEliteEntity.this;

            xydrax.getNavigation().stop();

            xydrax.windColumnTicks = xydrax.random.nextInt(20 * 5, 20 * 20 + 1);

            xydrax.buildWindColumn(xydrax.blockPosition());
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !XydraxEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 60;
        }

        @Override
        protected int getCastingInterval()
        {
            return 700;
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
            return SoundEvents.GLASS_BREAK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.XYDRAX_WIND_COLUMN;
        }
    }

    private class XydraxHealGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxHealGoal() {}

        @Override
        protected void castSpell()
        {
            XydraxEliteEntity xydrax = XydraxEliteEntity.this;

            xydrax.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 1, false, false));

            Vec3 pos = xydrax.getEyePosition();
            xydrax.teleportTo(pos.x, pos.y + 3 + xydrax.random.nextFloat() * 5, pos.z);
            xydrax.setOnGround(false);

            xydrax.isHealing = true;
        }

        @Override
        public boolean canUse()
        {
            XydraxEliteEntity e = XydraxEliteEntity.this;
            return XydraxEliteEntity.this.tickCount >= this.spellCooldown && !XydraxEliteEntity.this.isCastingSpell() && !e.isInExtendedSpellState() && e.getHealth() < e.getMaxHealth() * 0.5;
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 640;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 6;
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
            return SpellType.XYDRAX_HEAL;
        }
    }

    private class XydraxWindBlastGoal extends SpellcastingIllagerUseSpellGoal
    {
        private final List<EntityType<?>> targetTypes;
        private final Supplier<Double> radius;

        private XydraxWindBlastGoal()
        {
            this.targetTypes = new ArrayList<>(List.of(EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.PLAYER));
            if(DifficultRaidsUtil.isGuardVillagersLoaded()) this.targetTypes.add(GuardEntityType.GUARD.get());

            this.radius = () -> switch(XydraxEliteEntity.this.level.getDifficulty()) {
                case PEACEFUL -> 0.0;
                case EASY -> 4.0;
                case NORMAL -> 7.0;
                case HARD -> 10.0;
            };
        }

        private List<LivingEntity> getNearbyEntities(double radius)
        {
            AABB aabb = new AABB(XydraxEliteEntity.this.blockPosition()).inflate(radius);

            return XydraxEliteEntity.this.level.getEntitiesOfClass(LivingEntity.class, aabb, e -> {
                if(e instanceof Player player) return !player.isCreative() && !player.isSpectator();
                else return this.targetTypes.contains(e.getType());
            });
        }

        @Override
        protected void castSpell()
        {
            this.getNearbyEntities(this.radius.get()).forEach(entity -> {
                Vec3 vecToNormalized = XydraxEliteEntity.this.vectorTo(entity).normalize();

                double force = switch(XydraxEliteEntity.this.level.getDifficulty()) {
                    case PEACEFUL -> 0.0;
                    case EASY -> 0.5;
                    case NORMAL -> 1.25;
                    case HARD -> 2.0;
                };

                Vec3 pushVector = new Vec3(vecToNormalized.x * force, 0.5, vecToNormalized.z * force);
                LogUtils.getLogger().warn("Xydrax Entity Push Vector: " + pushVector);
                entity.push(pushVector.x, pushVector.y, pushVector.z);
            });
        }

        @Override
        public boolean canUse()
        {
            return XydraxEliteEntity.this.tickCount >= this.spellCooldown && !XydraxEliteEntity.this.isCastingSpell() && !this.getNearbyEntities(this.radius.get()).isEmpty() && !XydraxEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 600;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 5;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.AMBIENT_UNDERWATER_EXIT;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.XYDRAX_WIND_BLAST;
        }
    }

    private class XydraxBarrageGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxBarrageGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = XydraxEliteEntity.this.getTarget();

            if(target != null)
            {
                for(int i = 0; i < 8; i++)
                {
                    Arrow arrow = new Arrow(XydraxEliteEntity.this.level, XydraxEliteEntity.this)
                    {
                        @Override
                        protected void onHitBlock(BlockHitResult p_36755_) { super.onHitBlock(p_36755_); this.discard(); }

                        @Override
                        protected void onHitEntity(EntityHitResult pResult) { if(!(pResult.getEntity() instanceof Raider)) super.onHitEntity(pResult); }
                    };

                    arrow.setPos(XydraxEliteEntity.this.eyeBlockPosition().getX(), XydraxEliteEntity.this.eyeBlockPosition().getY() - 0.2, XydraxEliteEntity.this.eyeBlockPosition().getZ());

                    double targetY = target.getEyeY() - 1.1D;
                    double targetX = target.getX() - XydraxEliteEntity.this.getX();
                    double targetArrowY = targetY - arrow.getY();
                    double targetZ = target.getZ() - XydraxEliteEntity.this.getZ();
                    double distanceY = Math.sqrt(targetX * targetX + targetZ * targetZ) * (double)0.2F;

                    arrow.shoot(targetX, targetArrowY + distanceY, targetZ, 2.0F, 7.0F);
                    XydraxEliteEntity.this.level.addFreshEntity(arrow);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !XydraxEliteEntity.this.isInExtendedSpellState();
        }

        @Override
        protected int getCastingTime()
        {
            return 50;
        }

        @Override
        protected int getCastingInterval()
        {
            return 100;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 10;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.EVOKER_FANGS_ATTACK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.XYDRAX_ARROW_BARRAGE;
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
