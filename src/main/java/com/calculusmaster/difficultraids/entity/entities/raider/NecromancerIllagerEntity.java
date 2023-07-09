package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.util.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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

        if(Compat.GUARD_VILLAGERS.isLoaded()) this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Guard.class, 8.0F, 0.7D, 1.0D));
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
            float deflectedDamage = pAmount * this.config().necromancer.reflectedDamagePercentage;
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

                int summons = NecromancerIllagerEntity.this.config().necromancer.minionSummonCount;

                for(int i = 0; i < summons; i++)
                {
                    Entity entity = NecromancerIllagerEntity.this.config().necromancer.getMinionType().create(level);
                    if(!(entity instanceof Mob minion)) continue;

                    BlockPos summonPos = target.blockPosition().offset(-4 + random.nextInt(9), 0, -4 + random.nextInt(9));

                    List<Item> armor = switch(level.getDifficulty())
                    {
                        case PEACEFUL -> List.of();
                        case EASY -> List.of(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);
                        case NORMAL -> List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
                        case HARD -> List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
                    };

                    int protectionLevel = NecromancerIllagerEntity.this.config().necromancer.minionMaxProtectionLevel;

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
                    if(Compat.GUARD_VILLAGERS.isLoaded()) minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, Guard.class, true));

                    level.addFreshEntity(minion);
                    if(minion instanceof Monster monster) NecromancerIllagerEntity.this.activeMinions.add(monster);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            int minionThreshold = 2;

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

                int size = NecromancerIllagerEntity.this.config().necromancer.hordeSize;
                if(size > 6) size = NecromancerIllagerEntity.this.random.nextInt(size - 4, size + 5);

                int life = NecromancerIllagerEntity.this.config().necromancer.hordeLifetime;

                NecromancerIllagerEntity.this.playSound(SoundEvents.ENDERMAN_DEATH, 7.0F, 1.0F);

                BlockPos currentPos = NecromancerIllagerEntity.this.blockPosition();
                for(int i = 0; i < size; i++)
                {
                    Skeleton hordeMember = EntityType.SKELETON.create(level); if(hordeMember == null) continue;

                    hordeMember.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, life, 2));
                    hordeMember.setHealth(hordeMember.getMaxHealth() / 2);

                    hordeMember.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                    hordeMember.setTarget(target);
                    hordeMember.getLookControl().setLookAt(target);

                    hordeMember.targetSelector.removeAllGoals();
                    hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, Player.class, true));
                    hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, AbstractVillager.class, true));
                    hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, IronGolem.class, true));
                    if(Compat.GUARD_VILLAGERS.isLoaded()) hordeMember.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(hordeMember, Guard.class, true));

                    BlockPos summonPos = currentPos.offset(-10 + random.nextInt(21), 0, -10 + random.nextInt(21));
                    hordeMember.moveTo(summonPos, 0, 0);

                    level.addFreshEntity(hordeMember);
                    NecromancerIllagerEntity.this.activeHorde.add(hordeMember);
                }

                NecromancerIllagerEntity.this.hordeLifetimeTicks = life;
            }
        }

        @Override
        public boolean canUse()
        {
            int minionThreshold = 2;

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

            if(target != null)
            {
                //Blindness
                if(raid) target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));

                //Bury Logic
                target.playSound(SoundEvents.DROWNED_AMBIENT_WATER, 5.0F, 0.75F);

                int buryDistance = 2 + Mth.ceil(target.getBbHeight()) * (target.isOnGround() ? 1 : 2);

                target.moveTo(target.getBlockX(), target.getBlockY() - buryDistance, target.getBlockZ());

                if(!level.getBlockState(new BlockPos(target.getEyePosition())).isAir() && target instanceof AbstractVillager villager)
                    villager.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1));
            }
        }

        @Override
        public boolean canUse()
        {
            Level level = NecromancerIllagerEntity.this.getLevel();
            LivingEntity target = NecromancerIllagerEntity.this.getTarget();
            return super.canUse() && level.getBlockState(new BlockPos(target.getEyePosition())).isAir() && Math.pow(NecromancerIllagerEntity.this.blockPosition().distSqr(target.blockPosition()), 0.5) < 4;
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
