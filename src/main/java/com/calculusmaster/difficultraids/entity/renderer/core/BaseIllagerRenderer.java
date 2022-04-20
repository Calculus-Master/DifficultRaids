package com.calculusmaster.difficultraids.entity.renderer.core;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseIllagerRenderer<T extends AbstractIllagerVariant> extends IllagerRenderer<T>
{
    private final ResourceLocation resourceLocation;

    public BaseIllagerRenderer(EntityRendererProvider.Context entityRenderProvider, String path, ModelLayerLocation bakeLayer)
    {
        super(entityRenderProvider, new IllagerModel<>(entityRenderProvider.bakeLayer(bakeLayer)), 0.5F);

        this.resourceLocation = new ResourceLocation(DifficultRaids.MODID, "textures/entity/" + path);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity)
    {
        return this.resourceLocation;
    }
}
