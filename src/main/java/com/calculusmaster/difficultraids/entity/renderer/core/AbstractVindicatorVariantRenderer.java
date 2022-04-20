package com.calculusmaster.difficultraids.entity.renderer.core;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractVindicatorVariant;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractVindicatorVariantRenderer<T extends AbstractVindicatorVariant> extends AbstractIllagerVariantRenderer<T>
{
    protected static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/illager/vindicator.png");

    public AbstractVindicatorVariantRenderer(EntityRendererProvider.Context entityRenderProvider, String path)
    {
        super(entityRenderProvider, path, ModelLayers.VINDICATOR);
    }
}
