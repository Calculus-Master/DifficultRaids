package com.calculusmaster.difficultraids.entity.renderer.raider;

import com.calculusmaster.difficultraids.entity.entities.raider.AssassinIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractPillagerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AssassinIllagerRenderer extends AbstractPillagerVariantRenderer<AssassinIllagerEntity>
{
    public AssassinIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "assassin_illager.png");
    }
}