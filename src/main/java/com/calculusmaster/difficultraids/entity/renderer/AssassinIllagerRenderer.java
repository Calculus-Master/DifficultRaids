package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.AssassinIllagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AssassinIllagerRenderer extends IllagerRenderer<AssassinIllagerEntity>
{
    private static final ResourceLocation ASSASSIN_ILLAGER = new ResourceLocation(DifficultRaids.MODID, "textures/entity/assassin_illager.png");

    public AssassinIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, new IllagerModel<>(entityRenderProvider.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this)
        {
            @Override
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, AssassinIllagerEntity p_116355_, float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_, float p_116361_)
            {
                if(p_116355_.isAggressive())
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_, p_116360_, p_116361_);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AssassinIllagerEntity p_115720_)
    {
        return ASSASSIN_ILLAGER;
    }
}