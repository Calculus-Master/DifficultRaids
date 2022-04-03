package com.calculusmaster.difficultraids.entity.entities;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NecromancerIllagerEntity extends AbstractSpellcastingIllager
{
    private List<Monster> activeHorde = new ArrayList<>();
    private int hordeLifetimeTicks = 0;
    private List<Monster> activeMinions = new ArrayList<>();

    public NecromancerIllagerEntity(EntityType<? extends AbstractSpellcastingIllager> p_33724_, Level p_33725_)
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
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {

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
            boolean raid = NecromancerIllagerEntity.this.getCurrentRaid() != null;

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
                    RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

                    summons += switch(raidDifficulty) {
                        case HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 5;
                        case APOCALYPSE -> 10;
                        default -> 0;
                    };

                    switch(raidDifficulty)
                    {
                        case HERO -> minionPool.add(EntityType.DROWNED);
                        case LEGEND -> minionPool.addAll(List.of(EntityType.DROWNED, EntityType.WITHER_SKELETON));
                        case MASTER -> minionPool.addAll(List.of(EntityType.DROWNED, EntityType.WITHER_SKELETON, EntityType.WITCH));
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
                    if(raid) protectionLevel = switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
                        case HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case APOCALYPSE -> 4;
                        default -> 0;
                    };

                    List<EquipmentSlot> slots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
                    for(Item item : armor)
                    {
                        ItemStack armorStack = new ItemStack(item);

                        if(protectionLevel != 0) armorStack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, protectionLevel == 1 ? 1 : random.nextInt(1, protectionLevel));

                        minion.setItemSlot(slots.get(armor.indexOf(item)), armorStack);
                    }

                    minion.moveTo(summonPos, 0, 0);
                    minion.setTarget(target);

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
            return 120;
        }

        @Override
        protected int getCastingInterval()
        {
            return 560;
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
            boolean raid = NecromancerIllagerEntity.this.getCurrentRaid() != null;

            if(target != null)
            {
                Random random = new Random();

                int size;

                if(raid)
                {
                    size = switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
                        case HERO -> 10;
                        case LEGEND -> 15;
                        case MASTER -> 20;
                        case APOCALYPSE -> 30;
                        default -> 5;
                    };

                    size += switch(level.getDifficulty()) {
                        case PEACEFUL -> -size;
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

                    BlockPos summonPos = currentPos.offset(-10 + random.nextInt(21), 0, -10 + random.nextInt(21));
                    hordeMember.moveTo(summonPos, 0, 0);

                    level.addFreshEntity(hordeMember);
                    NecromancerIllagerEntity.this.activeHorde.add(hordeMember);
                }

                NecromancerIllagerEntity.this.hordeLifetimeTicks = raid ? switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
                    case HERO -> 20 * 30;
                    case LEGEND -> 20 * 60;
                    case MASTER -> 20 * 90;
                    case APOCALYPSE -> 20 * 180;
                    default -> 20 * 15;
                } : switch(level.getDifficulty()) {
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
            return 460;
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
            boolean raid = NecromancerIllagerEntity.this.getCurrentRaid() != null;
            RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();
            Random random = new Random();

            if(target != null)
            {
                int slownessLevel = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 1;
                    case NORMAL -> 2;
                    case HARD -> 3;
                };

                int slownessDuration;
                if(raid)
                {
                    slownessDuration = switch(raidDifficulty) {
                        case HERO -> 20 * 5;
                        case LEGEND -> 20 * 7;
                        case MASTER -> 20 * 10;
                        case APOCALYPSE -> 20 * 15;
                        default -> 0;
                    };

                    slownessDuration += switch(level.getDifficulty()) {
                        case PEACEFUL -> -slownessDuration;
                        case EASY -> -20 * 2;
                        case NORMAL -> 20 * (-1 + random.nextInt(3));
                        case HARD -> 20 * 2;
                    };
                }
                else slownessDuration = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 20 * 5;
                    case NORMAL -> 20 * 6;
                    case HARD -> 20 * 7;
                };

                //Slowness and Blindness
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slownessDuration, slownessLevel));

                if(raid && raidDifficulty.is(RaidDifficulty.MASTER, RaidDifficulty.APOCALYPSE))
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));

                //Bury Logic
                target.playSound(SoundEvents.DROWNED_AMBIENT_WATER, 5.0F, 0.75F);

                int buryDistance = 1;
                int fullBuryChance = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 10;
                    case NORMAL -> 15;
                    case HARD -> 20;
                };

                if(random.nextInt(100) < fullBuryChance)
                    buryDistance = 1 + Mth.ceil(target.getBbHeight()) * (target.isOnGround() ? 1 : 2);
                else buryDistance *= target.isOnGround() ? 1 : 2;

                target.moveTo(target.getBlockX(), target.getBlockY() - buryDistance, target.getBlockZ());

                if(!level.getBlockState(target.eyeBlockPosition()).isAir() && target instanceof AbstractVillager villager)
                {
                    villager.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 1));
                    System.out.println("A villager is being buried by a Necromancer!");
                }
            }
        }

        @Override
        public boolean canUse()
        {
            Level level = NecromancerIllagerEntity.this.getLevel();
            LivingEntity target = NecromancerIllagerEntity.this.getTarget();
            return super.canUse() && level.getBlockState(target.eyeBlockPosition()).isAir();
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastingInterval()
        {
            return 200;
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
