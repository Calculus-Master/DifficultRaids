package com.calculusmaster.difficultraids.entity.renderer.elite;

import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoldonEliteRenderer extends AbstractEvokerVariantRenderer<VoldonEliteEntity>
{
    public VoldonEliteRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "voldon_elite.png");
    }
}
