package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.entity.entities.component.VoldonFamiliarEntity;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
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
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VoldonEliteEntity extends AbstractEvokerVariant implements RangedAttackMob
{
    private final TextComponent ELITE_NAME = new TextComponent("Voldon, The Protected");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);

    private int totalFamiliars = 0;
    private List<LivingEntity> familiars = new ArrayList<>();

    public VoldonEliteEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    public static AttributeSupplier.Builder createEliteAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.42F)
                .add(Attributes.FOLLOW_RANGE, 15.0D)
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new VoldonCastSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 4.0F, 0.6D, 0.75D));
        this.goalSelector.addGoal(3, new VoldonSummonFamiliarsSpellGoal());
        this.goalSelector.addGoal(4, new VoldonTeleportFamiliarSpellGoal());
        this.goalSelector.addGoal(5, new VoldonSacrificeFamiliarSpellGoal());
        this.goalSelector.addGoal(5, new RangedAttackGoal(this, 0.5, 40, 5.0F));

        //TODO: Voldon (and Necromancer maybe) Void Blast Spell as a basic attack
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
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        for(int ID : pCompound.getIntArray("FamiliarIDs")) if(this.level.getEntity(ID) instanceof LivingEntity familiar) this.familiars.add(familiar);

        this.totalFamiliars = pCompound.getInt("TotalFamiliarCount");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        int[] IDs = new int[this.familiars.size()];
        for(int i = 0; i < IDs.length; i++) IDs[i] = this.familiars.get(i).getId();
        pCompound.putIntArray("FamiliarIDs", IDs);

        pCompound.putInt("TotalFamiliarCount", this.totalFamiliars);
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
        ItemStack legs = new ItemStack(Items.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Items.IRON_BOOTS);

        EnchantmentHelper.setEnchantments(generalEnchants, helm);
        EnchantmentHelper.setEnchantments(generalEnchants, chest);
        EnchantmentHelper.setEnchantments(generalEnchants, legs);
        EnchantmentHelper.setEnchantments(generalEnchants, boots);

        this.setItemSlot(EquipmentSlot.HEAD, helm);
        this.setItemSlot(EquipmentSlot.CHEST, chest);
        this.setItemSlot(EquipmentSlot.LEGS, legs);
        this.setItemSlot(EquipmentSlot.FEET, boots);
    }

    public boolean areFamiliarsDead()
    {
        return this.familiars.isEmpty();
    }

    public void removeFamiliar(VoldonFamiliarEntity familiar)
    {
        this.familiars.remove(familiar);
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
            pAmount *= 0.5;

        if(!this.areFamiliarsDead())
        {
            //TODO: Rework after making the custom familiar entities
            pAmount *= (1 - ((double)this.familiars.size()) / this.totalFamiliars) + 0.1F;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void die(DamageSource pCause)
    {
        super.die(pCause);

        BlockPos pos = this.blockPosition();

        int randomSpawnCount = this.random.nextInt(3, 6);
        for(int i = 0; i < randomSpawnCount; i++)
        {
            BlockPos spawnPos = pos.offset(5 - this.random.nextInt(1, 10), 1, 5 - this.random.nextInt(1, 10));

            Monster zombie = EntityType.ZOMBIE.create(this.level);
            zombie.moveTo(spawnPos, 0.0F, 0.0F);
            zombie.targetSelector.removeAllGoals();
            zombie.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(zombie, Villager.class, true));
            zombie.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(zombie, Player.class, true));
            zombie.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(zombie, IronGolem.class, true));
            if(DifficultRaidsUtil.isGuardVillagersLoaded()) zombie.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(zombie, Guard.class, true));
        }
    }

    @Override
    public void tick()
    {
        super.tick();

        if(!this.level.isClientSide) this.familiars.removeIf(LivingEntity::isDeadOrDying);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit)
    {
        //TODO: Voldon Unique Raid Loot - the Totem reward is temporary
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_PROTECTION.get()));
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_PROTECTION.get()));
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_PROTECTION.get()));
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor)
    {
        BlockPos shootingTargetPos = pTarget.eyeBlockPosition();

        //TODO: Void blast, also this code for the fireball doesn't work lol
        Projectile fireball = new SmallFireball(EntityType.SMALL_FIREBALL, this.level);
        fireball.setPos(this.position().add(0.0, this.getEyeHeight(), 0.0));
        fireball.shoot(shootingTargetPos.getX(), shootingTargetPos.getY(), shootingTargetPos.getZ(), 2.0F, 4.2F);

        this.level.addFreshEntity(fireball);
        if(this.level.isClientSide) this.level.addParticle(ParticleTypes.SMALL_FLAME, this.getX(), this.getEyeY() + 0.4, this.getZ(), 0.2, 0.0, 0.3);
    }

    private class VoldonCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        @Override
        public void tick()
        {
            if(VoldonEliteEntity.this.getTarget() != null)
                VoldonEliteEntity.this.getLookControl().setLookAt(VoldonEliteEntity.this.getTarget(), (float)VoldonEliteEntity.this.getMaxHeadYRot(), (float)VoldonEliteEntity.this.getMaxHeadXRot());
        }
    }

    private class VoldonSacrificeFamiliarSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private VoldonSacrificeFamiliarSpellGoal() { super(Flag.LOOK); }

        @Override
        protected void castSpell()
        {
            List<LivingEntity> familiars = VoldonEliteEntity.this.familiars.stream().filter(LivingEntity::isAlive).toList();

            boolean last = familiars.size() == 1;

            if(!familiars.isEmpty())
            {
                LivingEntity target = familiars.get(VoldonEliteEntity.this.random.nextInt(familiars.size()));

                VoldonEliteEntity.this.getLookControl().setLookAt(target);
                ((Mob)target).getLookControl().setLookAt(VoldonEliteEntity.this);

                int health = (int)target.getHealth();

                int resistanceDuration = health * 10 + (last ? 20 * 5 : 0);
                int regenerationDuration = health * 5 + (last ? 20 * 4 : 0);

                VoldonEliteEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, resistanceDuration, 1));
                VoldonEliteEntity.this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenerationDuration, 1));

                VoldonEliteEntity.this.playSound(SoundEvents.WITCH_DRINK, 1.0F, 1.0F);
                target.hurt(DamageSource.STARVE, health + 1.0F);
            }
        }

        @Override
        public boolean canUse()
        {
            return VoldonEliteEntity.this.tickCount >= this.spellCooldown && !VoldonEliteEntity.this.areFamiliarsDead();
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 900;
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
            return SoundEvents.ENDERMAN_DEATH;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.VOLDON_SACRIFICE_FAMILIAR;
        }
    }

    private class VoldonTeleportFamiliarSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private VoldonTeleportFamiliarSpellGoal() { super(Flag.MOVE, Flag.LOOK); }

        @Override
        protected void castSpell()
        {
            List<LivingEntity> familiars = VoldonEliteEntity.this.familiars.stream().filter(LivingEntity::isAlive).toList();

            if(!familiars.isEmpty())
            {
                LivingEntity target = familiars.get(VoldonEliteEntity.this.random.nextInt(familiars.size()));

                VoldonEliteEntity.this.getLookControl().setLookAt(target);

                double yOffset = 0.3;
                BlockPos targetPos = target.blockPosition().offset(0, yOffset, 0);
                BlockPos thisPos = VoldonEliteEntity.this.blockPosition().offset(0, yOffset, 0);

                VoldonEliteEntity.this.teleportToWithTicket(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                target.teleportToWithTicket(thisPos.getX(), thisPos.getY(), thisPos.getZ());
            }
        }

        @Override
        public boolean canUse()
        {
            return VoldonEliteEntity.this.tickCount >= this.spellCooldown && !VoldonEliteEntity.this.areFamiliarsDead() && VoldonEliteEntity.this.getHealth() < VoldonEliteEntity.this.getMaxHealth() * 2 / 3;
        }

        @Override
        protected int getCastingTime()
        {
            return 10;
        }

        @Override
        protected int getCastingInterval()
        {
            return 700;
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
            return SoundEvents.ENDERMAN_TELEPORT;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.VOLDON_TELEPORT_FAMILIAR;
        }
    }

    private class VoldonSummonFamiliarsSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private VoldonSummonFamiliarsSpellGoal() {}

        @Override
        protected void castSpell()
        {
            RaidDifficulty raidDifficulty = RaidDifficulty.current();

            int familiarCount;

            if(VoldonEliteEntity.this.getCurrentRaid() != null && !raidDifficulty.isDefault()) familiarCount = switch(raidDifficulty) {
                case HERO -> 4;
                case LEGEND -> 6;
                case MASTER -> 8;
                case APOCALYPSE -> 12;
                default -> 0;
            };
            else familiarCount = switch(VoldonEliteEntity.this.level.getDifficulty()) {
                case PEACEFUL -> 0;
                case EASY -> 3;
                case NORMAL -> 4;
                case HARD -> 6;
            };

            VoldonEliteEntity.this.totalFamiliars = familiarCount;

            BlockPos sourcePos = VoldonEliteEntity.this.blockPosition();
            Supplier<BlockPos> familiarPos = () -> sourcePos.offset(VoldonEliteEntity.this.random.nextInt(2, 6), VoldonEliteEntity.this.random.nextInt(2, 6), VoldonEliteEntity.this.random.nextInt(2, 6));
            for(int i = 0; i < familiarCount; i++)
            {
                //TODO: Create custom familiars
                Monster familiar = new VoldonFamiliarEntity(VoldonEliteEntity.this.level, VoldonEliteEntity.this);
                familiar.moveTo(familiarPos.get(), 0.0F, 0.0F);
                familiar.setOnGround(true);

                LogUtils.getLogger().info("Spawning Voldon Familiar!");
                familiar.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10000, 1));
                VoldonEliteEntity.this.level.addFreshEntity(familiar);
                VoldonEliteEntity.this.familiars.add(familiar);
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && VoldonEliteEntity.this.areFamiliarsDead();
        }

        @Override
        protected int getCastingTime()
        {
            return 70;
        }

        @Override
        protected int getCastingInterval()
        {
            return 1800;
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
            return SpellType.VOLDON_SUMMON_FAMILIARS;
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
