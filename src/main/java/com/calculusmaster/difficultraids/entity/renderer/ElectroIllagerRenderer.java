package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.entity.entities.ElectroIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectroIllagerRenderer extends AbstractEvokerVariantRenderer<ElectroIllagerEntity>
{
    public ElectroIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "electro_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(ElectroIllagerEntity p_115720_)
    {
        return DEFAULT;
    }
}
