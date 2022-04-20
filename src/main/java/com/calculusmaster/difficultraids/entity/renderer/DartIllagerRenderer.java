package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.entity.entities.DartIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractVindicatorVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DartIllagerRenderer extends AbstractVindicatorVariantRenderer<DartIllagerEntity>
{
    public DartIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "dart_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(DartIllagerEntity pEntity)
    {
        return DEFAULT;
    }
}
