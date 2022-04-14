package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

import java.util.*;

public class RaidLoot
{
    private static final Map<RaidDifficulty, RaidLootInfo> RAID_LOOT_INFO = new HashMap<>();

    public static void register()
    {
        RaidLootInfo
                .forRD(RaidDifficulty.HERO)
                .withRewardCount(5)
                .addLootEntry(Items.TOTEM_OF_UNDYING, 100, 3, 1, -1, 1)
                .addLootEntry(Items.EMERALD, 10, 5, 10, 4, -2, 2)
                .addLootEntry(Items.IRON_INGOT, 15, 8, 32, 4, -4, 8)
                .addLootEntry(Items.DIAMOND, 5, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_SPEED.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_POISON.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_LEVITATION.get(), 4, 1, 2, 1, -1, 1)
                .compile();

        RaidLootInfo
                .forRD(RaidDifficulty.LEGEND)
                .withRewardCount(7)
                .addLootEntry(Items.TOTEM_OF_UNDYING, 100, 5, 1, -1, 1)
                .addLootEntry(Items.EMERALD, 10, 10, 20, 4, -3, 5)
                .addLootEntry(Items.IRON_INGOT, 15, 20, 64, 4, -8, 12)
                .addLootEntry(Items.DIAMOND, 5, 5, 12, 1, -1, 1)
                .addLootEntry(Items.GOLDEN_APPLE, 6, 5, 10, 2, -2, 4)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_SPEED.get(), 4, 1, 3, 1, -1, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_POISON.get(), 4, 1, 3, 1, -1, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_LEVITATION.get(), 4, 1, 3, 1, -1, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_LIGHTNING.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_TELEPORTATION.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_FIREBALLS.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_FREEZING.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_VENGEANCE.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_PERSISTENCE.get(), 1, 1, 1, 0, 0)
                .compile();

        RaidLootInfo.forRD(RaidDifficulty.MASTER)
                .withRewardCount(12)
                .addLootEntry(Items.TOTEM_OF_UNDYING, 100, 10, 1, -1, 1)
                .addLootEntry(Items.EMERALD, 10, 64, 200, 2, -32, 32)
                .addLootEntry(Items.IRON_INGOT, 15, 48, 72, 2, -8, 12)
                .addLootEntry(Items.DIAMOND, 5, 16, 2, -6, 3)
                .addLootEntry(Items.GOLDEN_APPLE, 6, 16, 32, 2, -5, 5)
                .addLootEntry(Items.ANCIENT_DEBRIS, 2, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_SPEED.get(), 4, 1, 5, 1, -2, 3)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_POISON.get(), 4, 1, 5, 1, -2, 3)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_LEVITATION.get(), 4, 1, 5, 1, -2, 3)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_FREEZING.get(), 4, 1, 5, 1, -2, 3)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_LIGHTNING.get(), 4, 1, 3, 1, -1, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_VENGEANCE.get(), 4, 1, 3, 1, -1, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_TELEPORTATION.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_FIREBALLS.get(), 4, 1, 2, 1, -1, 1)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_DESTINY.get(), 8, 1, 2, 1, 0, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_PROTECTION.get(), 8, 1, 2, 1, 0, 2)
                .addLootEntry(DifficultRaidsItems.TOTEM_OF_PERSISTENCE.get(), 6, 1, 1, 0, 1)
                .compile();
    }

    public static List<ItemStack> generate(RaidDifficulty raidDifficulty, Difficulty levelDifficulty)
    {
        Random random = new Random();
        RaidLootInfo info = RAID_LOOT_INFO.get(raidDifficulty);

        if(info == null) return List.of();

        //Base Item Pool
        List<Item> pool = new ArrayList<>();
        info.loot.forEach(entry -> {
            for(int i = 0; i < entry.weight; i++) pool.add(entry.loot);
        });

        Map<Item, Integer> pullCount = new HashMap<>();

        List<ItemStack> stacks = new ArrayList<>();
        //Main Loot Pull Logic
        for(int i = 0; i < info.rewards; i++)
        {
            //Item
            int itemIndex = random.nextInt(pool.size());
            Item item = pool.get(itemIndex);

            //Loot Data
            RaidLootInfo.LootEntry data = info.getEntry(item);
            if(data == null) { i--; continue; } //Shouldn't happen

            //If the Item has already been pulled the max times, skip
            if(pullCount.getOrDefault(item, 0) == data.maxPulls()) { i--; continue; }

            //Item Count
            int count;

            if(data.minReward() == data.maxReward()) count = data.maxReward();
            else count = random.nextInt(data.minReward(), data.maxReward() + 1);

            //Difficulty Modifiers for Count
            count += levelDifficulty.equals(Difficulty.EASY) ? data.easyRewardModifier() : (levelDifficulty.equals(Difficulty.HARD) ? data.hardRewardModifier() : 0);

            ItemStack finalStack = new ItemStack(item, count);
            pullCount.put(item, pullCount.getOrDefault(item, 0) + 1);

            stacks.add(finalStack);
            pool.remove(itemIndex);
            Collections.shuffle(pool);
        }

        return stacks;
    }

    private static class RaidLootInfo
    {
        private RaidDifficulty raidDifficulty;
        private List<LootEntry> loot;
        private int rewards;

        private RaidLootInfo(RaidDifficulty raidDifficulty)
        {
            this.raidDifficulty = raidDifficulty;
            this.loot = new ArrayList<>();
        }

        static RaidLootInfo forRD(RaidDifficulty raidDifficulty)
        {
            return new RaidLootInfo(raidDifficulty);
        }

        RaidLootInfo withRewardCount(int rewards)
        {
            this.rewards = rewards;
            return this;
        }

        //TODO: Raid Tokens

        RaidLootInfo addLootEntry(Item loot, int weight, int minReward, int maxReward, int maxPulls, int easyRewardModifier, int hardRewardModifier)
        {
            this.loot.add(new LootEntry(loot, weight, minReward, maxReward, maxPulls, easyRewardModifier, hardRewardModifier));
            return this;
        }

        RaidLootInfo addLootEntry(Item loot, int weight, int reward, int maxPulls, int easyRewardModifier, int hardRewardModifier)
        {
            return this.addLootEntry(loot, weight, reward, reward, maxPulls, easyRewardModifier, hardRewardModifier);
        }

        RaidLootInfo addLootEntry(String modid, Item loot, int weight, int minReward, int maxReward, int maxPulls, int easyRewardModifier, int hardRewardModifier)
        {
            if(ModList.get().isLoaded(modid)) return this.addLootEntry(loot, weight, minReward, maxReward, maxPulls, easyRewardModifier, hardRewardModifier);
            else return this;
        }

        void compile()
        {
            RAID_LOOT_INFO.put(this.raidDifficulty, this);
        }

        LootEntry getEntry(Item item)
        {
            return this.loot.stream().filter(e -> e.loot().equals(item)).findFirst().orElse(null);
        }

        record LootEntry(Item loot, int weight, int minReward, int maxReward, int maxPulls, int easyRewardModifier, int hardRewardModifier) {}
    }
}
