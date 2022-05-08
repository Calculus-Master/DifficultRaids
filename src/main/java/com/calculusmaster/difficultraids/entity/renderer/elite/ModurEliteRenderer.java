package com.calculusmaster.difficultraids.entity.renderer.elite;

import com.calculusmaster.difficultraids.entity.entities.elite.ModurEliteEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModurEliteRenderer extends AbstractEvokerVariantRenderer<ModurEliteEntity>
{
    public ModurEliteRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "modur_elite.png");
    }
}
