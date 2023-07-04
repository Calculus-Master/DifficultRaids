package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.entity.entities.component.XydraxWindColumn;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEffects;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
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
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XydraxEliteEntity extends AbstractEvokerVariant
{
    private final Component ELITE_NAME = Component.translatable("com.calculusmaster.difficultraids.elite_event.xydrax");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private boolean isHealing = false;
    private final List<XydraxWindColumn> windColumns = new ArrayList<>();
    private int vortexTicks = 0;
    private BlockPos vortexFloor = BlockPos.ZERO;
    private AABB vortexAABB = new AABB(0, 0, 0, 0, 0, 0);

    public XydraxEliteEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new XydraxCastSpellGoal());
        this.goalSelector.addGoal(2, new XydraxAvoidEntityGoal( 4.0F, 0.7D, 0.8D));
        this.goalSelector.addGoal(3, new XydraxVortexSpellGoal());
        this.goalSelector.addGoal(3, new XydraxWindColumnSpellGoal());
        this.goalSelector.addGoal(4, new XydraxBarrageSpellGoal());
        this.goalSelector.addGoal(4, new XydraxHealSpellGoal());

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
        pCompound.putIntArray("WindColumnData", this.serializeWindColumns());
        pCompound.putInt("VortexTicks", this.vortexTicks);
        pCompound.putIntArray("VortexFloorPos", new int[]{this.vortexFloor.getX(), this.vortexFloor.getY(), this.vortexFloor.getZ()});
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.isHealing = pCompound.getBoolean("IsHealing");
        this.deserializeWindColumns(pCompound.getIntArray("WindColumnData"));
        this.vortexTicks = pCompound.getInt("VortexTicks");

        int[] vortexFloorPos = pCompound.getIntArray("VortexFloorPos");
        this.vortexFloor = vortexFloorPos.length == 0 ? BlockPos.ZERO : new BlockPos(vortexFloorPos[0], vortexFloorPos[1], vortexFloorPos[2]);
        if(this.vortexTicks > 0) this.createVortexAABB();
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
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_TELEPORTATION.get()));
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource)
    {
        return false;
    }

    @Override
    public void tick()
    {
        super.tick();

        RaidDifficulty raidDifficulty = this.isInDifficultRaid() ? this.getRaidDifficulty() : RaidDifficulty.DEFAULT;

        if(this.isHealing())
        {
            //Slow Descent
            Vec3 deltaMove = this.getDeltaMovement();
            if(!this.isVortexActive() && !this.isOnGround() && deltaMove.y() < 0) this.setDeltaMovement(deltaMove.multiply(1.0D, 0.55D, 1.0D));

            //Healing Checks
            if(this.isOnGround()) this.isHealing = false;
            else if(this.random.nextBoolean())
            {
                float currentHealth = this.getHealth();
                float maxHealth = this.getMaxHealth();

                if(maxHealth - currentHealth > 0.5F)
                {
                    float healAmount = this.random.nextFloat() / 1.5F;
                    this.heal(healAmount);
                }
            }
        }

        if(!this.windColumns.isEmpty())
        {
            this.windColumns.removeIf(XydraxWindColumn::isComplete);

            this.windColumns.forEach(XydraxWindColumn::tick);
        }

        if(this.isVortexActive())
        {
            this.vortexTicks--;

            //Extremely slow descent
            Vec3 deltaMove = this.getDeltaMovement();
            if(!this.isOnGround() && deltaMove.y() < 0) this.setDeltaMovement(deltaMove.multiply(1.0D, 0.1D, 1.0D));

            //Particles
            for(int i = this.vortexFloor.getY(); i < this.blockPosition().getY(); i++)
            {
                BlockPos pos = new BlockPos(this.vortexFloor.getX() + 0.5, i, this.vortexFloor.getZ() + 0.5);
                ((ServerLevel)this.level).sendParticles(ParticleTypes.FALLING_OBSIDIAN_TEAR, pos.getX(), pos.getY(), pos.getZ(), 1, 0.05, 0, 0.05, 1.0);
            }

            //Vortex Pull
            if(this.vortexTicks % 20 == 0)
            {
                this.getVortexTargets().forEach(e -> {
                    BlockPos targetPos = e.blockPosition().offset(0.5, 0, 0.5);
                    Vec3 targetVector = new Vec3(this.vortexFloor.getX() - targetPos.getX(), this.vortexFloor.getY() - targetPos.getY(), this.vortexFloor.getZ() - targetPos.getZ()).normalize();

                    double force = switch(raidDifficulty) {
                        case DEFAULT -> 1.25F;
                        case HERO -> 1.75F;
                        case LEGEND -> 2.5F;
                        case MASTER -> 3.75F;
                        case GRANDMASTER -> 5.0F;
                    };

                    //Modifier from entity's Knockback Resistance (0 -> 1, percent of some max value)
                    double kbresistanceModifier = 1 - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) * 0.5F;
                    force = Math.max(force * 0.25F, force * kbresistanceModifier);

                    e.push(targetVector.x * force, targetVector.y, targetVector.z * force);
                    e.hurtMarked = true;
                });
            }

            //DoT
            if(this.vortexTicks % 40 == 0)
            {
                this.getVortexTargets().forEach(e -> {
                    double distance = Math.pow(e.distanceToSqr(this.vortexFloor.getX(), this.vortexFloor.getY(), this.vortexFloor.getZ()), 0.5);
                    float damage;
                    if(distance < 1) damage = 17.5F;
                    else if(distance < 2) damage = 10.0F;
                    else if(distance < 5) damage = 4.0F;
                    else damage = 1.0F;

                    damage *= switch(raidDifficulty) {
                        case DEFAULT, HERO -> 1.0F;
                        case LEGEND -> 1.05F;
                        case MASTER -> 1.2F;
                        case GRANDMASTER -> 2.0F;
                    };

                    if(damage != 0.0F) e.hurt(DamageSource.mobAttack(this), damage);
                });
            }
        }

        //Movement Slowdown while Wind Columns Active
        if(this.isWindColumnActive() && !this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 60, 5, false, true));
        else if(!this.isWindColumnActive() && this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
            this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    public boolean isHealing()
    {
        return this.isHealing;
    }

    public boolean isWindColumnActive()
    {
        return this.windColumns.size() > 3;
    }

    public boolean isVortexActive()
    {
        return this.vortexTicks > 0;
    }

    private int[] serializeWindColumns()
    {
        int[] data = new int[this.windColumns.size() * 4];
        for(int i = 0; i < data.length; i += 4)
        {
            XydraxWindColumn column = this.windColumns.get(i / 4);
            data[i] = column.getPosition().getX();
            data[i + 1] = column.getPosition().getY();
            data[i + 2] = column.getPosition().getZ();
            data[i + 3] = column.getLife();
        }
        return data;
    }

    private void deserializeWindColumns(int[] data)
    {
        for(int i = 0; i < data.length; i += 4)
        {
            BlockPos pos = new BlockPos(data[i], data[i + 1], data[i + 2]);
            int life = data[i + 3];
            this.windColumns.add(new XydraxWindColumn(this, pos, life));
        }
    }

    private void createVortexAABB()
    {
        this.vortexAABB = new AABB(this.vortexFloor)
                .inflate(10.0, 0, 10.0)
                .setMaxY(this.vortexFloor.getY() + 10)
                .setMinY(this.vortexFloor.getY() - 1);
    }

    private void summonWindColumns()
    {
        BlockPos center = new BlockPos(this.blockPosition()).offset(0, 0.05, 0);

        RaidDifficulty raidDifficulty = this.isInDifficultRaid() ? this.getRaidDifficulty() : RaidDifficulty.DEFAULT;

        List<BlockPos> windColumnSpawns = switch(raidDifficulty) {
            case DEFAULT -> List.of(
                    center.offset(4, 0, 0),
                    center.offset(-4, 0, 0)
            );
            case HERO -> List.of(
                    center.offset(4, 0, 0),
                    center.offset(-4, 0, 0),
                    center.offset(0, 0, 4),
                    center.offset(0, 0, -4)
            );
            case LEGEND -> List.of(
                    center.offset(4, 0, 0),
                    center.offset(-4, 0, 0),
                    center.offset(0, 0, 4),
                    center.offset(0, 0, -4),
                    center.offset(4, 0, 4),
                    center.offset(4, 0, -4)
            );
            case MASTER -> List.of(
                    center.offset(4, 0, 0),
                    center.offset(-4, 0, 0),
                    center.offset(0, 0, 4),
                    center.offset(0, 0, -4),
                    center.offset(4, 0, 4),
                    center.offset(4, 0, -4),
                    center.offset(-4, 0, 4),
                    center.offset(-4, 0, -4)
            );
            case GRANDMASTER -> List.of(
                    center.offset(4, 0, 0),
                    center.offset(-4, 0, 0),
                    center.offset(0, 0, 4),
                    center.offset(0, 0, -4),
                    center.offset(4, 0, 4),
                    center.offset(4, 0, -4),
                    center.offset(-4, 0, 4),
                    center.offset(-4, 0, -4),
                    center.offset(6, 0, 6),
                    center.offset(-6, 0, 6),
                    center.offset(6, 0, -6),
                    center.offset(-6, 0, -6)
            );
        };

        windColumnSpawns.forEach(pos -> {
            int life = switch(raidDifficulty) {
                case DEFAULT -> this.random.nextInt(20 * 5, 20 * 15 + 1);
                case HERO -> this.random.nextInt(20 * 7, 20 * 17 + 1);
                case LEGEND -> this.random.nextInt(20 * 9, 20 * 17 + 1);
                case MASTER -> this.random.nextInt(20 * 10, 20 * 20 + 1);
                case GRANDMASTER -> this.random.nextInt(20 * 15, 20 * 25 + 1);
            };

            XydraxWindColumn column = new XydraxWindColumn(this, pos, life);
            this.windColumns.add(column);
        });
    }

    private List<LivingEntity> getVortexTargets()
    {
        return this.level.getEntitiesOfClass(LivingEntity.class, this.vortexAABB, e ->
        {
            if(e.isAlliedTo(this)) return false;
            else if(e instanceof Player player) return !player.isCreative() && !player.isSpectator();
            else return true;
        });
    }

    //For spells that last beyond their goals, like healing or wind column
    private boolean isInExtendedSpellState()
    {
        return this.isHealing() || this.isWindColumnActive() || this.isVortexActive();
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

    private class XydraxVortexSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxVortexSpellGoal() {}

        private boolean isEntityNearby()
        {
            XydraxEliteEntity xydrax = XydraxEliteEntity.this;
            AABB search = new AABB(xydrax.blockPosition().offset(0.5, 1.0, 0.5)).inflate(10.0);
            return !xydrax.level.getEntitiesOfClass(LivingEntity.class, search, e -> !e.isAlliedTo(XydraxEliteEntity.this)).isEmpty();
        }

        @Override
        protected void castSpell()
        {
            XydraxEliteEntity xydrax = XydraxEliteEntity.this;

            //Set up vortex bounds
            xydrax.vortexFloor = XydraxEliteEntity.this.blockPosition().offset(0.5, -0.05, 0.5);
            xydrax.createVortexAABB();

            //Push Xydrax into air
            xydrax.push(0, 1.5F, 0);
            xydrax.setOnGround(false);

            //Initiate vortex
            RaidDifficulty raidDifficulty = xydrax.isInDifficultRaid() ? xydrax.getRaidDifficulty() : RaidDifficulty.DEFAULT;
            xydrax.vortexTicks = switch(raidDifficulty) {
                case DEFAULT -> 20 * 12;
                case HERO, LEGEND -> 20 * 20;
                case MASTER, GRANDMASTER -> 20 * 30;
            };
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !XydraxEliteEntity.this.isInExtendedSpellState() && this.isEntityNearby();
        }

        @Override
        protected int getCastingTime()
        {
            return 80;
        }

        @Override
        protected int getCastingInterval()
        {
            return 700 + XydraxEliteEntity.this.vortexTicks;
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
            return SoundEvents.BUCKET_FILL;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.XYDRAX_VORTEX;
        }
    }

    private class XydraxWindColumnSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxWindColumnSpellGoal() {}

        @Override
        protected void castSpell()
        {
            XydraxEliteEntity xydrax = XydraxEliteEntity.this;

            xydrax.getNavigation().stop();

            xydrax.summonWindColumns();
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !XydraxEliteEntity.this.isInExtendedSpellState() && XydraxEliteEntity.this.isOnGround();
        }

        @Override
        protected int getCastingTime()
        {
            return 60;
        }

        @Override
        protected int getCastingInterval()
        {
            return 900 + XydraxEliteEntity.this.windColumns.size() * 2 * 20;
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

    private class XydraxHealSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxHealSpellGoal() {}

        @Override
        protected void castSpell()
        {
            XydraxEliteEntity xydrax = XydraxEliteEntity.this;

            xydrax.push(0, 1 + xydrax.random.nextFloat(), 0);
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

    private class XydraxBarrageSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private XydraxBarrageSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = XydraxEliteEntity.this.getTarget();

            if(target != null)
            {
                int curseDuration = XydraxEliteEntity.this.isInDifficultRaid() ? switch(XydraxEliteEntity.this.getRaidDifficulty()) {
                    case DEFAULT, HERO -> 20 * 10;
                    case LEGEND -> 20 * 20;
                    case MASTER -> 20 * 30;
                    case GRANDMASTER -> 20 * 40;
                } : 20 * 10;

                int curseAmplifier = XydraxEliteEntity.this.isInDifficultRaid() ? switch(XydraxEliteEntity.this.getRaidDifficulty()) {
                    case DEFAULT, HERO -> 1;
                    case LEGEND -> 2;
                    case MASTER -> 3;
                    case GRANDMASTER -> 4;
                } : 1;

                for(int i = 0; i < 12; i++)
                {
                    Arrow arrow = new Arrow(XydraxEliteEntity.this.level, XydraxEliteEntity.this)
                    {
                        @Override
                        protected void onHitBlock(BlockHitResult p_36755_) { super.onHitBlock(p_36755_); this.discard(); }

                        @Override
                        protected void onHitEntity(EntityHitResult pResult)
                        {
                            if(!(pResult.getEntity() instanceof Raider))
                            {
                                super.onHitEntity(pResult);

                                if(pResult.getEntity() instanceof LivingEntity living)
                                    living.addEffect(new MobEffectInstance(DifficultRaidsEffects.WIND_CURSE_EFFECT.get(), curseDuration, curseAmplifier));
                            }
                        }
                    };

                    arrow.setPos(XydraxEliteEntity.this.getEyePosition().x(), XydraxEliteEntity.this.getEyePosition().y() - 0.2, XydraxEliteEntity.this.getEyePosition().z());

                    double targetY = target.getEyeY() - 1.1D;
                    double targetX = target.getX() - XydraxEliteEntity.this.getX();
                    double targetArrowY = targetY - arrow.getY();
                    double targetZ = target.getZ() - XydraxEliteEntity.this.getZ();
                    double distanceY = Math.sqrt(targetX * targetX + targetZ * targetZ) * (double)0.2F;

                    arrow.shoot(targetX, targetArrowY + distanceY, targetZ, 1.5F, 25.0F);
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
            return 250;
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
