package com.calculusmaster.difficultraids.entity.renderer;

import com.calculusmaster.difficultraids.entity.entities.WarriorIllagerEntity;
import com.calculusmaster.difficultraids.entity.renderer.core.AbstractVindicatorVariantRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WarriorIllagerRenderer extends AbstractVindicatorVariantRenderer<WarriorIllagerEntity>
{
    public WarriorIllagerRenderer(EntityRendererProvider.Context entityRenderProvider)
    {
        super(entityRenderProvider, "warrior_illager.png");
    }

    @Override
    public ResourceLocation getTextureLocation(WarriorIllagerEntity pEntity)
    {
        return DEFAULT;
    }
}
