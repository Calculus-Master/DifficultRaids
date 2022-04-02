package com.calculusmaster.difficultraids.entity.entities;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class ShamanIllagerEntity extends AbstractSpellcastingIllager
{
    public ShamanIllagerEntity(EntityType<? extends AbstractSpellcastingIllager> p_33724_, Level p_33725_)
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
        this.goalSelector.addGoal(1, new ShamanCastSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new ShamanAttackBoostSpellGoal());
        this.goalSelector.addGoal(4, new ShamanDefenseBoostSpellGoal());
        this.goalSelector.addGoal(5, new ShamanDebuffSpellGoal());
        this.goalSelector.addGoal(6, new ShamanInvisibilitySpellGoal());
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

    private class ShamanCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        private ShamanCastSpellGoal() {}

        @Override
        public void tick()
        {
            if(ShamanIllagerEntity.this.getTarget() != null)
                ShamanIllagerEntity.this.getLookControl().setLookAt(ShamanIllagerEntity.this.getTarget(), (float)ShamanIllagerEntity.this.getMaxHeadYRot(), (float)ShamanIllagerEntity.this.getMaxHeadXRot());
        }
    }

    private class ShamanInvisibilitySpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();

            int duration = switch(level.getDifficulty()) {
                case PEACEFUL -> 0;
                case EASY -> 60;
                case NORMAL -> 80;
                case HARD -> 100;
            };

            ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 1));

            if(level.getDifficulty().equals(Difficulty.HARD) && ShamanIllagerEntity.this.random.nextInt(100) < 20)
                ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 2));
        }

        @Override
        public boolean canUse()
        {
            if(ShamanIllagerEntity.this.getHealth() < ShamanIllagerEntity.this.getMaxHealth() / 2)
            {
                if(ShamanIllagerEntity.this.isCastingSpell()) return false;
                else return ShamanIllagerEntity.this.tickCount >= this.spellCooldown;
            }
            else return false;
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
            return 20;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_INVISIBILITY;
        }
    }

    private class ShamanDebuffSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ShamanDebuffSpellGoal() {}

        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();
            LivingEntity target = ShamanIllagerEntity.this.getTarget();
            boolean raid = ShamanIllagerEntity.this.getCurrentRaid() != null;
            Random random = new Random();

            List<MobEffect> debuffPool = List.of(MobEffects.BLINDNESS, MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN, MobEffects.POISON, MobEffects.LEVITATION, MobEffects.WEAKNESS);

            if(raid && target != null)
            {
                RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

                int debuffCount = switch(raidDifficulty) {
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 4;
                    case APOCALYPSE -> 5;
                    default -> 1;
                };

                Set<MobEffect> apply = new HashSet<>();
                for(int i = 0; i < debuffCount; i++) apply.add(debuffPool.get(random.nextInt(debuffPool.size())));

                apply.forEach(effect -> {
                    int duration = 0;
                    int amplifier = 0;

                    if(effect.equals(MobEffects.BLINDNESS))
                    {
                        duration = switch(raidDifficulty) {
                            case HERO -> 60;
                            case LEGEND -> 80;
                            case MASTER -> 100;
                            case APOCALYPSE -> 160;
                            default -> 40;
                        };
                        amplifier = 1;
                    }
                    else if(effect.equals(MobEffects.CONFUSION))
                    {
                        duration = switch(raidDifficulty) {
                            case HERO -> 40;
                            case LEGEND -> 60;
                            case MASTER -> 80;
                            case APOCALYPSE -> 160;
                            default -> 20;
                        };
                        amplifier = 1;
                    }
                    else if(effect.equals(MobEffects.MOVEMENT_SLOWDOWN))
                    {
                        switch(raidDifficulty)
                        {
                            case HERO -> {
                                duration = 100;
                                amplifier = 1;
                            }
                            case LEGEND -> {
                                duration = 100;
                                amplifier = 2;
                            }
                            case MASTER -> {
                                duration = 160;
                                amplifier = 2;
                            }
                            case APOCALYPSE -> {
                                duration = 240;
                                amplifier = 3;
                            }
                            default -> {
                                duration = 60;
                                amplifier = 1;
                            }
                        }
                    }
                    else if(effect.equals(MobEffects.DIG_SLOWDOWN))
                    {
                        switch(raidDifficulty)
                        {
                            case HERO -> {
                                duration = 100;
                                amplifier = 1;
                            }
                            case LEGEND -> {
                                duration = 160;
                                amplifier = 1;
                            }
                            case MASTER -> {
                                duration = 160;
                                amplifier = 2;
                            }
                            case APOCALYPSE -> {
                                duration = 280;
                                amplifier = 3;
                            }
                            default -> {
                                duration = 40;
                                amplifier = 1;
                            }
                        }
                    }
                    else if(effect.equals(MobEffects.POISON))
                    {
                        duration = 60;
                        amplifier = switch(raidDifficulty) {
                            case HERO -> 2;
                            case LEGEND -> 3;
                            case MASTER -> 4;
                            case APOCALYPSE -> 5;
                            default -> 1;
                        };
                    }
                    else if(effect.equals(MobEffects.LEVITATION))
                    {
                        duration = switch(raidDifficulty) {
                            case HERO -> 40;
                            case LEGEND -> 80;
                            case MASTER -> 100;
                            case APOCALYPSE -> 160;
                            default -> 20;
                        };
                        amplifier = 1;
                    }
                    else if(effect.equals(MobEffects.WEAKNESS))
                    {
                        switch(raidDifficulty)
                        {
                            case HERO -> {
                                duration = 60;
                                amplifier = 2;
                            }
                            case LEGEND -> {
                                duration = 100;
                                amplifier = 2;
                            }
                            case MASTER -> {
                                duration = 120;
                                amplifier = 3;
                            }
                            case APOCALYPSE -> {
                                duration = 200;
                                amplifier = 4;
                            }
                            default -> {
                                duration = 20;
                                amplifier = 1;
                            }
                        }
                    }

                    //General Difficulty Changes
                    duration += switch(level.getDifficulty()) {
                        case PEACEFUL -> -duration;
                        case EASY -> -40;
                        case NORMAL -> 0;
                        case HARD -> 40;
                    };

                    target.addEffect(new MobEffectInstance(effect, duration, amplifier));
                });
            }
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastingInterval()
        {
            return 320;
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
            return SoundEvents.WITCH_DRINK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_DEBUFF;
        }
    }

    private class ShamanDefenseBoostSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();
            boolean raid = ShamanIllagerEntity.this.getCurrentRaid() != null;
            Random random = new Random();

            //In a Raid, Shaman will boost others. If not, it'll boost itself (but the Shaman is a Raid-only mob at the moment)
            if(raid)
            {
                RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

                double buffRadius = switch(raidDifficulty) {
                    case HERO -> 5.0;
                    case LEGEND -> 8.0;
                    case MASTER -> 12.0;
                    case APOCALYPSE -> 20.0;
                    default -> 3.0;
                };

                AABB buffAABB = new AABB(ShamanIllagerEntity.this.blockPosition()).inflate(buffRadius);
                Predicate<AbstractIllager> canReceiveBuff = illager -> !illager.is(ShamanIllagerEntity.this) && !illager.hasEffect(MobEffects.DAMAGE_RESISTANCE);
                List<AbstractIllager> raiders = level.getEntitiesOfClass(AbstractIllager.class, buffAABB, canReceiveBuff);

                //0: Duration, 1: Amplifier
                int[] resistanceData = switch(raidDifficulty) {
                    case HERO -> new int[]{80, 1};
                    case LEGEND -> new int[]{160, 1};
                    case MASTER -> new int[]{160, 2};
                    case APOCALYPSE -> new int[]{360, 3};
                    default -> new int[]{40, 1};
                };

                resistanceData[0] += switch(level.getDifficulty()) {
                    case PEACEFUL -> -resistanceData[0];
                    case EASY -> -20;
                    case NORMAL -> -20 + random.nextInt(41);
                    case HARD -> +20;
                };

                raiders.forEach(r -> {
                    r.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_RESISTANCE,
                            random.nextInt(resistanceData[0] - 20, resistanceData[0] + 21),
                            resistanceData[1]));
                    r.playSound(SoundEvents.BREWING_STAND_BREW, 0.5F, 1.0F);
                });

                ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 2));
            }
            else if(!ShamanIllagerEntity.this.hasEffect(MobEffects.DAMAGE_RESISTANCE))
            {
                int resistanceLevel = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 1;
                    case NORMAL -> 2;
                    case HARD -> 3;
                };

                int resistanceDuration = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 120;
                    case NORMAL -> 200;
                    case HARD -> 240;
                };

                ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, resistanceDuration, resistanceLevel));
            }
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 40;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.WITCH_DRINK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_DEFENSE_BOOST;
        }
    }

    private class ShamanAttackBoostSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();
            boolean raid = ShamanIllagerEntity.this.getCurrentRaid() != null;
            Random random = new Random();

            //In a Raid, Shaman will boost others. If not, it'll boost itself (but the Shaman is a Raid-only mob at the moment)
            if(raid)
            {
                RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

                double buffRadius = switch(raidDifficulty) {
                    case HERO -> 5.0;
                    case LEGEND -> 8.0;
                    case MASTER -> 12.0;
                    case APOCALYPSE -> 20.0;
                    default -> 3.0;
                };

                AABB buffAABB = new AABB(ShamanIllagerEntity.this.blockPosition()).inflate(buffRadius);
                Predicate<AbstractIllager> canReceiveBuff = illager -> !illager.is(ShamanIllagerEntity.this) && !illager.hasEffect(MobEffects.DAMAGE_BOOST);
                List<AbstractIllager> raiders = level.getEntitiesOfClass(AbstractIllager.class, buffAABB, canReceiveBuff);

                //0: Duration, 1: Amplifier
                int[] strengthData = switch(raidDifficulty) {
                    case HERO -> new int[]{200, 1};
                    case LEGEND -> new int[]{480, 1};
                    case MASTER -> new int[]{240, 2};
                    case APOCALYPSE -> new int[]{480, 3};
                    default -> new int[]{120, 1};
                };

                strengthData[0] += switch(level.getDifficulty()) {
                    case PEACEFUL -> -strengthData[0];
                    case EASY -> -40;
                    case NORMAL -> -20 + random.nextInt(41);
                    case HARD -> +40;
                };

                raiders.forEach(r -> {
                    r.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST,
                            random.nextInt(strengthData[0] - 20, strengthData[0] + 21),
                            strengthData[1]));
                    r.playSound(SoundEvents.BREWING_STAND_BREW, 0.5F, 1.0F);
                    System.out.println("Shaman Buffed a " + r.getType().toShortString());
                });

                ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 2));
            }
            else if(!ShamanIllagerEntity.this.hasEffect(MobEffects.DAMAGE_BOOST))
            {
                int strengthLevel = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 1;
                    case NORMAL -> 2;
                    case HARD -> 3;
                };

                int strengthDuration = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 120;
                    case NORMAL -> 200;
                    case HARD -> 240;
                };

                ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, strengthDuration, strengthLevel));
            }
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 40;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.WITCH_DRINK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_ATTACK_BOOST;
        }
    }
}
