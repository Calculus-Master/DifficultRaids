package com.calculusmaster.difficultraids.entity.renderer.elite;

import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class XydraxEliteRenderer extends AbstractEvokerVariantRenderer<XydraxEliteEntity>
{
    public XydraxEliteRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "xydrax_elite.png");
    }
}
