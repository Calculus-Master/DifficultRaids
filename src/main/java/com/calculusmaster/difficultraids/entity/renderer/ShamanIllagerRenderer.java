package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.entity.entities.ShamanIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShamanIllagerRenderer extends AbstractEvokerVariantRenderer<ShamanIllagerEntity>
{
    public ShamanIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "shaman_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(ShamanIllagerEntity p_115720_)
    {
        return DEFAULT;
    }
}
