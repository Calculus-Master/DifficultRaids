package com.calculusmaster.difficultraids.entity.entities.component;

import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.Random;

public class WindColumnObject
{
    private final XydraxEliteEntity owner;
    private final ServerLevel level;
    private BlockPos position;
    private int life;

    private final Random random;
    private AABB pushAABB;
    private final RaidDifficulty raidDifficulty;

    private final int height = 20;

    public WindColumnObject(XydraxEliteEntity owner, BlockPos startPos, int life)
    {
        this.owner = owner;
        this.level = (ServerLevel)owner.getLevel();
        this.position = startPos;
        this.life = life;

        this.random = new Random();
        this.rebuildAABB();
        this.raidDifficulty = owner.isInDifficultRaid() ? owner.getRaidDifficulty() : RaidDifficulty.DEFAULT;
    }

    public boolean isComplete()
    {
        return this.life <= 0;
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public int getLife()
    {
        return this.life;
    }

    public void tick()
    {
        this.life--;

        if(this.life != 0)
        {
            this.spawnParticles();

            if(this.life % 20 == 0) this.push();

            if(this.life % 40 == 0 && this.random.nextBoolean()) this.move();
        }
    }

    private void push()
    {
        this.level.getEntitiesOfClass(LivingEntity.class, this.pushAABB, e -> !e.isAlliedTo(this.owner) && !(e instanceof Player p && (p.isSpectator() || p.isCreative()))).forEach(this::applyPushTarget);
    }

    private void applyPushTarget(LivingEntity target)
    {
        float dX = this.random.nextFloat() * 4 - 2;
        float dY = 1.5F;
        float dZ = this.random.nextFloat() * 4 - 2;

        float modifier = switch(this.raidDifficulty) {
            case DEFAULT -> 1.0F;
            case HERO -> 1.15F;
            case LEGEND -> 1.25F;
            case MASTER -> 1.4F;
            case GRANDMASTER -> 1.75F;
        };

        target.push(dX, dY * modifier, dZ);
        target.hurtMarked = true;

        float windsCurseChance = switch(this.raidDifficulty) {
            case DEFAULT, HERO, LEGEND -> 0.0F;
            case MASTER -> 0.15F;
            case GRANDMASTER -> 0.35F;
        };

        if(this.random.nextFloat() < windsCurseChance && !target.hasEffect(DifficultRaidsEffects.WIND_CURSE_EFFECT.get()))
        {
            int curseAmplifier = switch(this.raidDifficulty)
            {
                case DEFAULT, HERO, LEGEND -> 0;
                case MASTER -> 2;
                case GRANDMASTER -> 4;
            };

            target.addEffect(new MobEffectInstance(DifficultRaidsEffects.WIND_CURSE_EFFECT.get(), 20 * 10, curseAmplifier));
        }
    }

    private void spawnParticles()
    {
        for(int i = 0; i < this.height; i += 3)
        {
            BlockPos particlePos = this.position.offset(0.5, i, 0.5);
            this.level.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.WHITE_WOOL.defaultBlockState()), particlePos.getX(), particlePos.getY(), particlePos.getZ(), 1, 0.15, 0, 0.15, 2.0);
        }
    }

    private void move()
    {
        BlockPos target = this.position.offset(this.random.nextInt(7) - 3, 0, this.random.nextInt(7) - 3);

        int tries = 0;
        while(tries < 3 && !this.level.getBlockState(target).isAir())
        {
            target = target.above(1);
            tries++;
        }

        this.position = target;
        this.rebuildAABB();
    }

    private void rebuildAABB()
    {
        this.pushAABB = new AABB(this.position.offset(-1, -2, -1), this.position.offset(1, this.height, 1));
    }
}
