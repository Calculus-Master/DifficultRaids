package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModurEliteEntity extends AbstractEvokerVariant
{
    private final TextComponent ELITE_NAME = new TextComponent("Modur, Harbinger of Thunder");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);

    private int stormTicks;
    private AABB stormAABB;

    public ModurEliteEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);

        this.stormTicks = 0;
        this.stormAABB = new AABB(BlockPos.ZERO);
    }

    public static AttributeSupplier.Builder createEliteAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.45F)
                .add(Attributes.FOLLOW_RANGE, 14.0D)
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new ModurCastSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 4.0F, 0.6D, 0.75D));
        this.goalSelector.addGoal(3, new ModurSummonThunderGoal());
        this.goalSelector.addGoal(4, new ModurLightningStormGoal());
        this.goalSelector.addGoal(4, new ModurLightningZapGoal());
        this.goalSelector.addGoal(5, new ModurShootFireballGoal());

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        if(DifficultRaidsUtil.isGuardVillagersLoaded()) this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Guard.class, 6.0F, 0.6D, 0.75D));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        //Armor
        Map<Enchantment, Integer> generalEnchants = new HashMap<>();
        generalEnchants.put(Enchantments.ALL_DAMAGE_PROTECTION, 2);
        generalEnchants.put(Enchantments.VANISHING_CURSE, 1);
        generalEnchants.put(Enchantments.THORNS, 1);
        generalEnchants.put(Enchantments.FIRE_PROTECTION, 5);

        ItemStack helm = new ItemStack(Items.NETHERITE_HELMET);
        ItemStack chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
        ItemStack legs = new ItemStack(Items.NETHERITE_LEGGINGS);
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
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getEntity() instanceof IronGolem || (DifficultRaidsUtil.isGuardVillagersLoaded() && pSource.getEntity() instanceof Guard))
            pAmount *= 0.4;

        if(pSource.getDirectEntity() instanceof LivingEntity living && this.isStormActive() && this.random.nextFloat() < 0.15F)
            this.spawnCustomBolt(living.blockPosition().offset(0, 0.5, 0), this.random.nextFloat() * 3);

        if(pSource.getDirectEntity() instanceof Projectile) pAmount *= 0.8F;

        if(this.isStormActive()) pAmount *= 1.2;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void die(DamageSource pCause)
    {
        super.die(pCause);

        this.spawnCustomBolt(this.blockPosition(), 20.0F);

        if(!this.level.isClientSide) ((ServerLevel)ModurEliteEntity.this.level).setWeatherParameters(6000, 0, false, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("StormTicks", this.stormTicks);
        pCompound.putIntArray("StormAABB", new int[]{(int)this.stormAABB.minX, (int)this.stormAABB.maxX, (int)this.stormAABB.minY, (int)this.stormAABB.maxY, (int)this.stormAABB.minZ, (int)this.stormAABB.maxZ});
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.stormTicks = pCompound.getInt("StormTicks");

        int[] dataAABB = pCompound.getIntArray("StormAABB");
        this.stormAABB = dataAABB.length == 6 ? new AABB(dataAABB[0], dataAABB[1], dataAABB[2], dataAABB[3], dataAABB[4], dataAABB[5]) : new AABB(BlockPos.ZERO);
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
        //TODO: Modur Unique Raid Loot - the Totem reward is temporary
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_LIGHTNING.get()));
    }

    public boolean isStormActive()
    {
        return this.stormTicks > 0;
    }

    private void spawnCustomBolt(BlockPos spawn, float damage)
    {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(this.level); if(bolt == null) return;

        bolt.setCustomName(new TextComponent(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG));
        bolt.setDamage(damage);
        bolt.moveTo(spawn, 0.0F, 0.0F);

        this.level.addFreshEntity(bolt);
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.isStormActive())
        {
            int strikes = this.level.getDifficulty().equals(Difficulty.HARD) ? 2 : 1;

            for(int i = 0; i < strikes; i++)
            {
                BlockPos strikePos = new BlockPos(this.random.nextInt((int)this.stormAABB.minX, (int)(this.stormAABB.maxX + 1)), this.stormAABB.minY, this.random.nextInt((int)this.stormAABB.minZ, (int)this.stormAABB.maxZ));
                int tries = 0; while(!this.level.getBlockState(strikePos).isAir() && tries++ < 20) strikePos = strikePos.above(1);

                ModurEliteEntity.this.spawnCustomBolt(strikePos, switch(this.level.getDifficulty()) {
                    case PEACEFUL -> 0.0F;
                    case EASY -> 3.0F;
                    case NORMAL -> 7.5F;
                    case HARD -> 10.0F;
                });
            }

            this.stormTicks--;
            if(this.stormTicks == 0) this.stormAABB = new AABB(BlockPos.ZERO);
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

    private class ModurShootFireballGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ModurShootFireballGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = ModurEliteEntity.this.getTarget();
            ServerLevel level = (ServerLevel)ModurEliteEntity.this.level;
            ModurEliteEntity modur = ModurEliteEntity.this;

            if(target != null)
            {
                int count = switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 2;
                    case NORMAL -> 4;
                    case HARD -> 6;
                };

                double dX = target.getX() - modur.getX();
                double dY = target.getY(0.5D) - modur.getY(0.5D);
                double dZ = target.getZ() - modur.getZ();

                for(int i = 0; i < count; i++)
                {
                    Supplier<Double> modifier_dX = () -> (Math.sqrt(Math.sqrt(dX)) * 0.5D) * modur.random.nextGaussian();
                    SmallFireball fireball = new SmallFireball(level, modur, dX + modifier_dX.get(), dY, dZ + modifier_dX.get()) {
                        private int life;

                        @Override
                        public void onAddedToWorld()
                        {
                            super.onAddedToWorld();
                            this.life = 0;
                        }

                        @Override
                        public void tick()
                        {
                            super.tick();

                            if(this.life != -1 && this.life < 60) this.life++;
                            if(this.life == 60 && !this.isRemoved())
                            {
                                this.life = -1;
                                this.discard();
                            }
                        }

                        @Override
                        protected void onHitEntity(EntityHitResult pResult)
                        {
                            if(pResult.getEntity() instanceof Raider) this.discard();
                            else super.onHitEntity(pResult);
                        }
                    };

                    fireball.setPos(fireball.getX(), modur.getY(0.5D) + 0.5D, fireball.getZ());
                    level.addFreshEntity(fireball);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            LivingEntity target = ModurEliteEntity.this.getTarget();
            return super.canUse() && target != null && target.distanceTo(ModurEliteEntity.this) > 3.0;
        }

        @Override
        protected int getCastingTime()
        {
            return 20;
        }

        @Override
        protected int getCastingInterval()
        {
            return 40;
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
            return SoundEvents.BLASTFURNACE_FIRE_CRACKLE;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_FIREBALL;
        }
    }

    private class ModurLightningZapGoal extends SpellcastingIllagerUseSpellGoal
    {
        private BlockPos targetPos;

        private ModurLightningZapGoal() { this.targetPos = BlockPos.ZERO; }

        @Override
        protected void castSpell()
        {
            if(!this.targetPos.equals(BlockPos.ZERO))
            {
                BlockPos pos = this.targetPos.offset(ModurEliteEntity.this.random.nextInt(3) - 1, 0, ModurEliteEntity.this.random.nextInt(3) - 1);

                int times = ModurEliteEntity.this.random.nextInt(2, 6);
                for(int i = 0; i < times; i++) ModurEliteEntity.this.spawnCustomBolt(pos, 15.0F);
            }
        }

        @Override
        public void start()
        {
            super.start();
            if(ModurEliteEntity.this.getTarget() != null) this.targetPos = new BlockPos(ModurEliteEntity.this.getTarget().blockPosition());
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
            return SoundEvents.TRIDENT_THUNDER;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.MODUR_LIGHTNING_ZAP;
        }
    }

    private class ModurLightningStormGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ModurLightningStormGoal() {}

        @Override
        protected void castSpell()
        {
            ModurEliteEntity modur = ModurEliteEntity.this;

            modur.stormTicks = modur.random.nextInt(40, switch(modur.level.getDifficulty()) {
                case PEACEFUL -> 41;
                case EASY -> 80;
                case NORMAL -> 100;
                case HARD -> 160;
            });

            modur.stormAABB = new AABB(modur.blockPosition()).inflate(modur.getCurrentRaid() != null ? switch(RaidDifficulty.current()) {
                case HERO -> 18.0;
                case LEGEND -> 24.0;
                case MASTER -> 30.0;
                case APOCALYPSE -> 50.0;
                default -> 0.0;
            } : 10.0).setMaxY(modur.getEyeY() + 4.0).setMinY(modur.blockPosition().getY() + 0.4);
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && ModurEliteEntity.this.level.getLevelData().isThundering() && !ModurEliteEntity.this.isStormActive();
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 700;
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

    private class ModurSummonThunderGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ModurSummonThunderGoal() {}

        @Override
        protected void castSpell()
        {
            ((ServerLevel)ModurEliteEntity.this.level).setWeatherParameters(0, 20 * 60 * 5, true, true);
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !ModurEliteEntity.this.level.getLevelData().isThundering();
        }

        @Override
        protected int getCastingTime()
        {
            return 70;
        }

        @Override
        protected int getCastingInterval()
        {
            return 300;
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
