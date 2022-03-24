package com.calculusmaster.difficultraids.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class WeightedRewardPool
{
    private Map<Item, Integer> weights;
    private Map<Item, Integer> rewardCounts;
    private Map<Item, Integer> maxPulls;

    public WeightedRewardPool()
    {
        this.weights = new HashMap<>();
        this.rewardCounts = new HashMap<>();
        this.maxPulls = new HashMap<>();
    }

    public WeightedRewardPool add(Item item, int weight, int maxReward, int maxPulls)
    {
        if(weight == 0 || maxReward == 0 || maxPulls == 0) return this;

        this.weights.put(item, weight);
        this.rewardCounts.put(item, maxReward);
        this.maxPulls.put(item, maxPulls);
        return this;
    }

    public ItemStack pull()
    {
        List<Item> pool = new ArrayList<>();
        this.weights.forEach((key, value) -> {
            for(int i = 0; i < value; i++) pool.add(key);
        });

        Item chosen = pool.get(new Random().nextInt(pool.size()));
        this.maxPulls.put(chosen, this.maxPulls.get(chosen) - 1);

        ItemStack stack = new ItemStack(chosen, 1);
        stack.grow(new Random().nextInt(1, this.rewardCounts.get(chosen) + 1));

        if(this.maxPulls.get(chosen) <= 0) this.remove(chosen);
        return stack;
    }

    private void remove(Item item)
    {
        this.weights.remove(item);
        this.rewardCounts.remove(item);
        this.maxPulls.remove(item);
    }
}
