package com.calculusmaster.difficultraids.entity.entities.core;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.AbstractIllager;
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
        else
            return false;
    }

    protected RaidDifficulty getRaidDifficulty()
    {
        if(this.getCurrentRaid() == null) return null;
        else return RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());
    }

    protected boolean isInRaid()
    {
        return this.getCurrentRaid() != null;
    }

    protected boolean isInDifficultRaid()
    {
        return this.isInRaid() && !this.getRaidDifficulty().isDefault();
    }
}
