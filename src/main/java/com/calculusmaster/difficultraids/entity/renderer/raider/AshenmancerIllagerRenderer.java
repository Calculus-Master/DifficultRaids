package com.calculusmaster.difficultraids.entity.renderer.raider;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.raider.AshenmancerIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractEvokerVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AshenmancerIllagerRenderer extends AbstractEvokerVariantRenderer<AshenmancerIllagerEntity>
{
    private static final ResourceLocation WITHERED = new ResourceLocation(DifficultRaids.MODID, "textures/entity/ashenmancer_illager_withered.png");

    public AshenmancerIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "ashenmancer_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(AshenmancerIllagerEntity pEntity)
    {
        return pEntity.isTurretActive() ? WITHERED : super.getTextureLocation(pEntity);
    }
}
