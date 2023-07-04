package com.calculusmaster.difficultraids.entity.renderer.core;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractEvokerVariantRenderer<T extends AbstractEvokerVariant> extends BaseIllagerRenderer<T>
{
    protected static final ResourceLocation DEFAULT = new ResourceLocation("textures/entity/illager/evoker.png");

    public AbstractEvokerVariantRenderer(EntityRendererProvider.Context entityRenderProvider, String path)
    {
        super(entityRenderProvider, path, ModelLayers.EVOKER);

        this.addLayer(new ItemInHandLayer<>(this, entityRenderProvider.getItemInHandRenderer())
        {
            @Override
            public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
            {
                if(pLivingEntity.isCastingSpell())
                    super.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            }
        });
    }
}
