package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.ElectroIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectroIllagerRenderer extends AbstractEvokerVariantRenderer<ElectroIllagerEntity>
{
    private static final ResourceLocation ELECTRIFIED = new ResourceLocation(DifficultRaids.MODID, "textures/entity/electro_illager_electrified.png");

    public ElectroIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "electro_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(ElectroIllagerEntity pEntity)
    {
        return pEntity.isCastingSpell() ? ELECTRIFIED : super.getTextureLocation(pEntity);
    }
}
