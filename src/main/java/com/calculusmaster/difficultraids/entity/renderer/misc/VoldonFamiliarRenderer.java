package com.calculusmaster.difficultraids.entity.renderer.misc;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.component.VoldonFamiliarEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractIllagerVariantRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class VoldonFamiliarRenderer extends AbstractIllagerVariantRenderer<VoldonFamiliarEntity>
{
    private static final ResourceLocation VOLDON_FAMILIAR_BASE = new ResourceLocation(DifficultRaids.MODID, "textures/entity/voldon_familiar.png");
    private static final ResourceLocation VOLDON_FAMILIAR_HIDE = new ResourceLocation(DifficultRaids.MODID, "textures/entity/voldon_familiar_hide.png");

    public VoldonFamiliarRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "voldon_familiar.png", ModelLayers.EVOKER);
    }

    @Override
    public ResourceLocation getTextureLocation(VoldonFamiliarEntity pEntity)
    {
        return pEntity.isInHideState() ? VOLDON_FAMILIAR_HIDE : VOLDON_FAMILIAR_BASE;
    }
}
