package com.calculusmaster.difficultraids.entity.renderer.elite;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractIllagerVariantRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NuaosEliteRenderer extends AbstractIllagerVariantRenderer<NuaosEliteEntity>
{
    private static final ResourceLocation NUAOS_DEFAULT = new ResourceLocation(DifficultRaids.MODID, "textures/entity/nuaos_elite_default.png");
    private static final ResourceLocation NUAOS_LOW = new ResourceLocation(DifficultRaids.MODID, "textures/entity/nuaos_elite_low.png");
    private static final ResourceLocation NUAOS_HIGH = new ResourceLocation(DifficultRaids.MODID, "textures/entity/nuaos_elite_high.png");
    private static final ResourceLocation NUAOS_MAX = new ResourceLocation(DifficultRaids.MODID, "textures/entity/nuaos_elite_max.png");

    public NuaosEliteRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "nuaos_elite.png", ModelLayers.VINDICATOR);
    }

    @Override
    public ResourceLocation getTextureLocation(NuaosEliteEntity pEntity)
    {
        return switch(pEntity.getChargeState()) {
            case NO_CHARGE -> NUAOS_DEFAULT;
            case LOW_CHARGE -> NUAOS_LOW;
            case HIGH_CHARGE -> NUAOS_HIGH;
            case MAX_CHARGE -> NUAOS_MAX;
        };
    }
}
