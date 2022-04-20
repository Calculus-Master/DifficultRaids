package com.calculusmaster.difficultraids.entity.renderer.core;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractPillagerVariant;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractPillagerVariantRenderer<T extends AbstractPillagerVariant> extends AbstractIllagerVariantRenderer<T>
{
    protected static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/illager/pillager.png");

    public AbstractPillagerVariantRenderer(EntityRendererProvider.Context entityRenderProvider, String path)
    {
        super(entityRenderProvider, path, ModelLayers.PILLAGER);
    }
}
