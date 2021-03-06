package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NecromancerIllagerEntity extends AbstractEvokerVariant
{
    private List<Monster> activeHorde = new ArrayList<>();
    private int hordeLifetimeTicks = 0;
    private List<Monster> activeMinions = new ArrayList<>();

    public NecromancerIllagerEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new NecromancerCastSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new NecromancerSummonMinionsSpellGoal());
        this.goalSelector.addGoal(4, new NecromancerBuryTargetGoal());
        this.goalSelector.addGoal(5, new NecromancerSummonHordeSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.3D));
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

    }

    @Override
    public IllagerArmPose getArmPose()
    {
        if(this.isCastingSpell()) return IllagerArmPose.SPELLCASTING;
        else return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        List<Integer> hordeIDs = this.activeHorde.stream().filter(Monster::isAlive).map(Entity::getId).toList();
        pCompound.putIntArray("ActiveHorde", hordeIDs);
        pCompound.putInt("ActiveHordeLifetimeTicks", this.hordeLifetimeTicks);

        List<Integer> minionIDs = this.activeMinions.stream().filter(Monster::isAlive).map(Entity::getId).toList();
        pCompound.putIntArray("ActiveMinions", minionIDs);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        int[] hordeIDs = pCompound.getIntArray("ActiveHorde");
        for(int ID : hordeIDs)
        {
            Entity entity = this.level.getEntity(ID);
            if(entity instanceof Monster monster && monster.isAlive()) this.activeHorde.add(monster);
        }
        this.hordeLifetimeTicks = pCompound.getInt("ActiveHordeLifetimeTicks");

        int[] minionIDs = pCompound.getIntArray("ActiveMinions");
        for(int ID : minionIDs)
        {
            Entity entity = this.level.getEntity(ID);
            if(entity instanceof Monster monster && monster.isAlive()) this.activeMinions.add(monster);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getEntity() instanceof LivingEntity && !this.activeMinions.isEmpty() && pAmount > 1.0F && this.random.nextFloat() < 0.33F)
        {
            float deflectedDamage = pAmount * 0.4F;
            Monster target = this.activeMinions.get(this.random.nextInt(this.activeMinions.size()));

            if(target.getHealth() > deflectedDamage)
            {
                target.hurt(pSource, deflectedDamage);
                pAmount -= deflectedDamage;
                this.playSound(SoundEvents.GLASS_PLACE, 1.5F, 0.9F);
            }
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void die(DamageSource pCause)
    {
        super.die(pCause);

        this.activeHorde.forEach(m -> m.hurt(DamageSource.STARVE, m.getHealth() + 1.0F));
        this.activeMinions.forEach(m -> m.hurt(DamageSource.STARVE, m.getHealth() + 1.0F));
    }

    @Override
    public void tick()
    {
        super.tick();

        //Remove dead Horde members
        if(!this.activeHorde.isEmpty())
        {
            this.activeHorde.removeIf(LivingEntity::isDeadOrDying);

            if(this.activeHorde.isEmpty()) this.hordeLifetimeTicks = 0;
        }

        //Remove dead Minions
        if(!this.activeMinions.isEmpty()) this.activeMinions.removeIf(LivingEntity::isDeadOrDying);

        //If the Horde lifetime expires, kill remaining alive Horde members
        if(this.hordeLifetimeTicks > 0)
        {
            this.hordeLifetimeTicks--;

            if(this.hordeLifetimeTicks == 0)
            {
                this.activeHorde.forEach(m -> m.hurt(DamageSource.STARVE, m.getHealth() + 1.0F));
                this.activeHorde.clear();
            }
        }

        //If Minions aren't targeting anything, make them target whatever the Necromancer is targeting
        if(!this.activeMinions.isEmpty() && this.getTarget() != null) for(Monster minion : this.activeMinions) if(minion.getTarget() == null) minion.setTarget(this.getTarget());
    }

    private class NecromancerCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        private NecromancerCastSpellGoal() {}

        @Override
        public void tick()
        {
            if(NecromancerIllagerEntity.this.getTarget() != null)
                NecromancerIllagerEntity.this.getLookControl().setLookAt(NecromancerIllagerEntity.this.getTarget(), (float)NecromancerIllagerEntity.this.getMaxHeadYRot(), (float)NecromancerIllagerEntity.this.getMaxHeadXRot());
        }
    }

    private class NecromancerSummonMinionsSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private NecromancerSummonMinionsSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = NecromancerIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)NecromancerIllagerEntity.this.getLevel();
            boolean raid = NecromancerIllagerEntity.this.isInRaid();

            if(target != null)
            {
                Random random = new Random();

                int summons = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 2;
                    case NORMAL -> 3;
                    case HARD -> 4;
                };

                List<EntityType<? extends Monster>> minionPool = switch(level.getDifficulty()) {
                    case PEACEFUL -> new ArrayList<>();
                    case EASY -> new ArrayList<>(List.of(EntityType.ZOMBIE));
                    case NORMAL -> new ArrayList<>(List.of(EntityType.ZOMBIE, EntityType.SKELETON));
                    case HARD -> new ArrayList<>(List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.HUSK, EntityType.STRAY));
                };

                if(raid)
                {
                    RaidDifficulty raidDifficulty = NecromancerIllagerEntity.this.getRaidDifficulty();

                    summons = raidDifficulty.config().necromancer().minionSummonCount();
                    if(level.getDifficulty().equals(Difficulty.EASY)) summons--;
                    else if(level.getDifficulty().equals(Difficulty.HARD)) summons++;

                    switch(raidDifficulty)
                    {
                        case HERO -> minionPool.add(EntityType.DROWNED);
                        case LEGEND -> minionPool.addAll(List.of(EntityType.DROWNED, EntityType.WITHER_SKELETON));
                        case MASTER -> minionPool.addAll(List.of(EntityType.DROWNED, EntityType.WITHER_SKELETON, EntityType.ZOMBIE_VILLAGER));
                    }
                }

                for(int i = 0; i < summons; i++)
                {
                    EntityType<? extends Monster> type = minionPool.get(random.nextInt(minionPool.size()));
                    Monster minion = type.create(level); if(minion == null) continue;
                    BlockPos summonPos = target.blockPosition().offset(-4 + random.nextInt(9), 0, -4 + random.nextInt(9));

                    List<Item> armor = switch(level.getDifficulty()) {
                        case PEACEFUL -> List.of();
                        case EASY -> List.of(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);
                        case NORMAL -> List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
                        case HARD -> List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
                    };

                    int protectionLevel = 0;
                    if(raid) protectionLevel = NecromancerIllagerEntity.this.getRaidDifficulty().config().necromancer().minionProtectionLevel();

                    List<EquipmentSlot> slots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                    for(Item item : armor)
                    {
                        ItemStack armorStack = new ItemStack(item);

                        if(protectionLevel != 0) armorStack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, protectionLevel == 1 ? 1 : random.nextInt(1, protectionLevel));

                        minion.setItemSlot(slots.get(armor.indexOf(item)), armorStack);
                    }

                    minion.moveTo(summonPos, 0, 0);
                    minion.setTarget(target);
                    minion.getLookControl().setLookAt(target);

                    minion.targetSelector.removeAllGoals();
                    minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, Player.class, true));
                    minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, AbstractVillager.class, true));
                    minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, IronGolem.class, true));
                    if(DifficultRaidsUtil.isGuardVillagersLoaded()) minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, Guard.class, true));

                    level.addFreshEntity(minion);
                    NecromancerIllagerEntity.this.activeMinions.add(minion);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            int minionThreshold = switch(NecromancerIllagerEntity.this.getLevel().getDifficulty()) {
                case PEACEFUL -> 0;
                case EASY -> 1;
                case NORMAL, HARD -> 2;
            };

            return super.canUse() && NecromancerIllagerEntity.this.activeMinions.size() <= minionThreshold && NecromancerIllagerEntity.this.hordeLifetimeTicks < 20 * 2;
        }

        @Override
        protected int getCastingTime()
        {
            return 160;
        }

        @Override
        protected int getCastingInterval()
        {
            return 600;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 30;
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
            return SpellType.NECROMANCER_SUMMON_MINIONS;
        }
    }

    private class NecromancerSummonHordeSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private NecromancerSummonHordeSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = NecromancerIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)NecromancerIllagerEntity.this.getLevel();
            boolean raid = NecromancerIllagerEntity.this.isInRaid();

            if(target != null)
            {
                Random random = new Random();

                int size;

                if(raid)
                {
                    RaidDifficulty raidDifficulty = NecromancerIllagerEntity.this.getRaidDifficulty();

                    size = raidDifficulty.config().necromancer().hordeSize() + switch(level.getDifficulty()) {
                        case PEACEFUL -> -raidDifficulty.config().necromancer().hordeSize();
                        case EASY -> -5;
                        case NORMAL -> 0;
                        case HARD -> 5;
                    };
                }
                else size = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 5;
                    case NORMAL -> 10;
                    case HARD -> 18;
                };

                NecromancerIllagerEntity.this.playSound(SoundEvents.ENDERMAN_DEATH, 10.0F, 1.0F);

                BlockPos currentPos = NecromancerIllagerEntity.this.blockPosition();
                for(int i = 0; i < size; i++)
                {
                    Skeleton hordeMember = EntityType.SKELETON.create(level);

                    hordeMember.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 3));
                    hordeMember.addEffect(new MobEffectInstance(MobEffects.POISON, 500, 1));

                    hordeMember.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                    hordeMember.setTarget(target);
                    hordeMember.getLookControl().setLookAt(target);

                    hordeMember.targetSelector.removeAllGoals();
                    hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, Player.class, true));
                    hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, AbstractVillager.class, true));
                    hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, IronGolem.class, true));
                    if(DifficultRaidsUtil.isGuardVillagersLoaded()) hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, Guard.class, true));

                    BlockPos summonPos = currentPos.offset(-10 + random.nextInt(21), 0, -10 + random.nextInt(21));
                    hordeMember.moveTo(summonPos, 0, 0);

                    level.addFreshEntity(hordeMember);
                    NecromancerIllagerEntity.this.activeHorde.add(hordeMember);
                }

                NecromancerIllagerEntity.this.hordeLifetimeTicks = raid ? NecromancerIllagerEntity.this.getRaidDifficulty().config().necromancer().hordeLifetime() : switch(level.getDifficulty()) {
                    case PEACEFUL -> 1;
                    case EASY -> 20 * 15;
                    case NORMAL -> 20 * 30;
                    case HARD -> 20 * 45;
                };
            }
        }

        @Override
        public boolean canUse()
        {
            int minionThreshold = switch(NecromancerIllagerEntity.this.getLevel().getDifficulty()) {
                case PEACEFUL -> 0;
                case EASY -> 1;
                case NORMAL -> 2;
                case HARD -> 3;
            };

            return super.canUse() && NecromancerIllagerEntity.this.activeMinions.size() <= minionThreshold && NecromancerIllagerEntity.this.activeHorde.isEmpty();
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
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.NECROMANCER_SUMMON_HORDE;
        }
    }

    private class NecromancerBuryTargetGoal extends SpellcastingIllagerUseSpellGoal
    {
        private NecromancerBuryTargetGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = NecromancerIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)NecromancerIllagerEntity.this.getLevel();
            boolean raid = NecromancerIllagerEntity.this.isInRaid();
            Random random = new Random();

            if(target != null)
            {
                //Blindness
                if(raid) target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));

                //Bury Logic
                target.playSound(SoundEvents.DROWNED_AMBIENT_WATER, 5.0F, 0.75F);

                int buryDistance = 1;
                int fullBuryChance = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 20;
                    case NORMAL -> 35;
                    case HARD -> 50;
                };

                if(random.nextInt(100) < fullBuryChance)
                    buryDistance = 1 + Mth.ceil(target.getBbHeight()) * (target.isOnGround() ? 1 : 2);
                else buryDistance *= target.isOnGround() ? 1 : 2;

                target.moveTo(target.getBlockX(), target.getBlockY() - buryDistance, target.getBlockZ());

                if(!level.getBlockState(target.eyeBlockPosition()).isAir() && target instanceof AbstractVillager villager)
                    villager.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1));
            }
        }

        @Override
        public boolean canUse()
        {
            Level level = NecromancerIllagerEntity.this.getLevel();
            LivingEntity target = NecromancerIllagerEntity.this.getTarget();
            return super.canUse() && level.getBlockState(target.eyeBlockPosition()).isAir() && Math.pow(NecromancerIllagerEntity.this.blockPosition().distSqr(target.blockPosition()), 0.5) < 4;
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastingInterval()
        {
            return NecromancerIllagerEntity.this.getHealth() < NecromancerIllagerEntity.this.getMaxHealth() / 5 ? 100 : 600;
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
            return SoundEvents.EVOKER_FANGS_ATTACK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.NECROMANCER_BURY_TARGET;
        }
    }
}
