package com.calculusmaster.difficultraids.mixins.raider;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin extends Raider
{
    private static final String TAG_RAIDER_ARMOR_MODIFIER = "DifficultRaids Raider Armor Modifier";
    private static final String TAG_RAIDER_TOUGHNESS_MODIFIER = "DifficultRaids Raider Armor Toughness Modifier";
    private static final String TAG_RAIDER_DIFFICULTY_ARMOR_MODIFIER = "DifficultRaids Raider Armor Modifier (MC Difficulty-Dependent)";

    //Default Constructor
    protected AbstractIllagerMixin(EntityType<? extends Raider> p_37839_, Level p_37840_)
    {
        super(p_37839_, p_37840_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
                                        MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag)
    {
        boolean isRaidSpawn = this.getCurrentRaid() != null && mobSpawnType.equals(MobSpawnType.EVENT);
        Random random = new Random();

        if(isRaidSpawn)
        {
            RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

            boolean standard = DifficultRaidsUtil.STANDARD_RAIDERS.contains(this.getType());
            boolean basicMagic = DifficultRaidsUtil.BASIC_MAGIC_RAIDERS.contains(this.getType());
            boolean advMagic = DifficultRaidsUtil.ADVANCED_MAGIC_RAIDERS.contains(this.getType());

            AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
            AttributeInstance toughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);

            if(!raidDifficulty.isDefault() && (standard || basicMagic || advMagic) && (armor != null && toughness != null))
            {
                int[] iron = {2, 6};
                int[] diamond = {3, 8};
                int[] netherite = {5, 8};

                //Standard Raiders
                if(standard)
                {
                    AttributeModifier armorModifier = new AttributeModifier(TAG_RAIDER_ARMOR_MODIFIER, switch(raidDifficulty) {
                        case DEFAULT -> 0.0F;
                        case HERO -> random.nextInt(iron[0], iron[1] + 1);
                        case LEGEND -> random.nextInt(diamond[0], diamond[1] + 1) * 1.25;
                        case MASTER -> random.nextInt(netherite[0], netherite[1] + 1) * 1.5;
                        case GRANDMASTER -> random.nextInt(10, 21);
                    }, AttributeModifier.Operation.ADDITION);

                    armor.addPermanentModifier(armorModifier);

                    if(raidDifficulty.is(RaidDifficulty.GRANDMASTER))
                    {
                        AttributeModifier toughnessModifier = new AttributeModifier(TAG_RAIDER_TOUGHNESS_MODIFIER, 10, AttributeModifier.Operation.ADDITION);
                        toughness.addPermanentModifier(toughnessModifier);
                    }
                }
                //Simpler Spellcasting Raiders
                else if(basicMagic)
                {
                    AttributeModifier armorModifier = new AttributeModifier(TAG_RAIDER_ARMOR_MODIFIER, switch(raidDifficulty) {
                        case DEFAULT -> 0.0F;
                        case HERO -> random.nextInt(0, 5);
                        case LEGEND -> random.nextInt(1, 6);
                        case MASTER -> random.nextInt(3, 7);
                        case GRANDMASTER -> random.nextInt(5, 15);
                    }, AttributeModifier.Operation.ADDITION);

                    armor.addPermanentModifier(armorModifier);

                    if(raidDifficulty.is(RaidDifficulty.GRANDMASTER))
                    {
                        AttributeModifier toughnessModifier = new AttributeModifier(TAG_RAIDER_TOUGHNESS_MODIFIER, 5, AttributeModifier.Operation.ADDITION);
                        toughness.addPermanentModifier(toughnessModifier);
                    }
                }
                //Advanced Spellcasting Raiders
                else
                {
                    AttributeModifier armorModifier = new AttributeModifier(TAG_RAIDER_ARMOR_MODIFIER, switch(raidDifficulty) {
                        case DEFAULT -> 0.0F;
                        case HERO -> random.nextInt(0, 3);
                        case LEGEND -> random.nextInt(1, 4);
                        case MASTER -> random.nextInt(4, 7);
                        case GRANDMASTER -> random.nextInt(5, 10);
                    }, AttributeModifier.Operation.ADDITION);

                    armor.addPermanentModifier(armorModifier);

                    if(raidDifficulty.is(RaidDifficulty.GRANDMASTER))
                    {
                        AttributeModifier toughnessModifier = new AttributeModifier(TAG_RAIDER_TOUGHNESS_MODIFIER, 5, AttributeModifier.Operation.ADDITION);
                        toughness.addPermanentModifier(toughnessModifier);
                    }
                }

                //General Easy/Hard Modifiers
                Difficulty difficulty = this.level.getDifficulty();

                if(difficulty.equals(Difficulty.EASY) || difficulty.equals(Difficulty.HARD))
                    armor.addPermanentModifier(new AttributeModifier(TAG_RAIDER_DIFFICULTY_ARMOR_MODIFIER, difficulty.equals(Difficulty.EASY) ? 0.9 : 1.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, tag);
    }
}
