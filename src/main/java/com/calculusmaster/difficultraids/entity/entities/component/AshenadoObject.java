package com.calculusmaster.difficultraids.entity.entities.component;

import com.calculusmaster.difficultraids.entity.entities.raider.AshenmancerIllagerEntity;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AshenadoObject
{
    private final AshenmancerIllagerEntity owner;
    private final ServerLevel level;
    private final RaidDifficulty raidDifficulty;

    private Vec3 center;
    private int life;

    private AABB coreArea;
    private AABB fullArea;
    private List<Vec3> particlePositions;
    private boolean firstTick;

    public AshenadoObject(AshenmancerIllagerEntity owner, RaidDifficulty raidDifficulty, Vec3 center, int life)
    {
        this.owner = owner;
        this.level = (ServerLevel)owner.getLevel();
        this.raidDifficulty = raidDifficulty;
        this.center = center;
        this.life = life;

        this.firstTick = true;

        this.generateArea();
    }

    public AshenadoObject(AshenmancerIllagerEntity owner, CompoundTag tag)
    {
        this.owner = owner;
        this.level = (ServerLevel)owner.getLevel();

        this.raidDifficulty = RaidDifficulty.valueOf(tag.getString("AshenadoRaidDifficulty"));
        this.center = new Vec3(tag.getDouble("AshenadoCenterX"), tag.getDouble("AshenadoCenterY"), tag.getDouble("AshenadoCenterZ"));
        this.life = tag.getInt("AshenadoLife");

        this.firstTick = true;

        this.generateArea();
    }

    public void save(CompoundTag tag)
    {
        tag.putString("AshenadoRaidDifficulty", this.raidDifficulty.toString());
        tag.putDouble("AshenadoCenterX", this.center.x());
        tag.putDouble("AshenadoCenterY", this.center.y());
        tag.putDouble("AshenadoCenterZ", this.center.z());
        tag.putInt("AshenadoLife", this.life);
    }

    private void generateArea()
    {
        final double radius = 0.75;
        final int height = 8;

        this.coreArea = new AABB(new BlockPos(this.center))
                .inflate(radius * 0.5, 0, radius * 0.5)
                .setMaxY(this.center.y() + height);

        this.fullArea = new AABB(new BlockPos(this.center))
                .inflate(radius, 0, radius)
                .setMaxY(this.center.y() + height);

        this.particlePositions = new ArrayList<>();
        for(float y = 0; y < height; y += 0.25)
        {
            double currentY = this.fullArea.minY + y;

            for(float x = 0; x <= (this.fullArea.maxX - this.fullArea.minX); x += 0.25)
            {
                double currentX = this.fullArea.minX + x;
                this.particlePositions.add(new Vec3(currentX, currentY, this.fullArea.minZ));
                this.particlePositions.add(new Vec3(currentX, currentY, this.fullArea.maxZ));
            }

            for(float z = 0; z <= (this.fullArea.maxZ - this.fullArea.minZ); z += 0.25)
            {
                double currentZ = this.fullArea.minZ + z;
                this.particlePositions.add(new Vec3(this.fullArea.minX, currentY, currentZ));
                this.particlePositions.add(new Vec3(this.fullArea.maxX, currentY, currentZ));
            }
        }
    }

    public void tick()
    {
        if(this.life > 0)
        {
            if(this.life % 8 == 0) this.render();

            if(this.life % 15 == 0) this.applyEffects();

            if(!this.firstTick && this.life % 100 == 0)
            {
                this.center = this.center.add(5 - this.level.random.nextInt(11), 0, 5 - this.level.random.nextInt(11));
                this.generateArea();
            }

            this.life--;
        }

        if(this.firstTick) this.firstTick = false;
    }

    private void render()
    {
        this.particlePositions.forEach(pos -> this.level.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.BLACK_CONCRETE.defaultBlockState()), pos.x(), pos.y(), pos.z(), 1, 0, 0, 0, 3.0));
    }

    private void applyEffects()
    {
        Predicate<LivingEntity> validTarget = l -> !(l instanceof Raider)
                && !l.isAlliedTo(this.owner)
                && (!(l instanceof Player p) || !p.isCreative() && !p.isSpectator());

        this.level
                .getEntitiesOfClass(LivingEntity.class, this.fullArea, validTarget)
                .forEach(l ->
                {
                    l.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 7, 1, false, false));
                    l.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 7, 1, false, false));
                });

        this.level
                .getEntitiesOfClass(LivingEntity.class, this.coreArea, validTarget)
                .forEach(l -> l.hurt(DamageSource.mobAttack(this.owner).bypassArmor().bypassEnchantments().bypassMagic(), 2.0F));
    }

    public boolean isComplete()
    {
        return this.life == 0;
    }

    public Vec3 getCenter()
    {
        return this.center;
    }
}
