package com.calculusmaster.difficultraids.entity.entities.core;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class AbstractIllagerVariant extends AbstractIllager
{
    public AbstractIllagerVariant(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
    }

    //Default isAlliedTo for Raiders
    @Override
    public boolean isAlliedTo(Entity pEntity)
    {
        //Default Raider isAlliedTo
        if(super.isAlliedTo(pEntity))
            return true;
        else if(pEntity instanceof LivingEntity living && living.getMobType() == MobType.ILLAGER)
            return this.getTeam() == null && pEntity.getTeam() == null;
        else if(pEntity instanceof Raider)
            return this.getTeam() == null && pEntity.getTeam() == null;
        else
            return false;
    }

    public RaidDifficulty getRaidDifficulty()
    {
        if(this.getCurrentRaid() == null) return null;
        else return RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());
    }

    public RaidDifficultyConfig config()
    {
        return (this.isInRaid() ? this.getRaidDifficulty() : RaidDifficulty.DEFAULT).config();
    }

    protected boolean isInRaid()
    {
        return this.getCurrentRaid() != null;
    }

    public boolean isInDifficultRaid()
    {
        return this.isInRaid() && !this.getRaidDifficulty().isDefault();
    }
}
