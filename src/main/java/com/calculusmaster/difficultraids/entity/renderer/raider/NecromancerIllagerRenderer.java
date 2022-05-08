package com.calculusmaster.difficultraids.entity.renderer.raider;

import com.calculusmaster.difficultraids.entity.entities.raider.NecromancerIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NecromancerIllagerRenderer extends AbstractEvokerVariantRenderer<NecromancerIllagerEntity>
{
    public NecromancerIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "necromancer_illager.png");
    }
}
