package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.entity.entities.TankIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractVindicatorVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TankIllagerRenderer extends AbstractVindicatorVariantRenderer<TankIllagerEntity>
{
    public TankIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "tank_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(TankIllagerEntity pEntity)
    {
        return DEFAULT;
    }
}
