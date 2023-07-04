package com.calculusmaster.difficultraids.entity.renderer.core;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;

public class AbstractIllagerVariantRenderer<T extends AbstractIllagerVariant> extends BaseIllagerRenderer<T>
{
    public AbstractIllagerVariantRenderer(EntityRendererProvider.Context entityRenderProvider, String path, ModelLayerLocation bakeLayer)
    {
        super(entityRenderProvider, path, bakeLayer);

        this.addLayer(new ItemInHandLayer<>(this, entityRenderProvider.getItemInHandRenderer())
        {
            @Override
            public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
            {
                if(pLivingEntity.isAggressive())
                    super.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            }
        });
    }
}
