package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import net.minecraft.util.Tuple;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.stream.Stream;

import static com.calculusmaster.difficultraids.setup.DifficultRaidsItems.*;

public class RaidLoot
{
    public static final Map<RaidDifficulty, RaidLootData> RAID_LOOT = new LinkedHashMap<>();

    public static void registerLoot()
    {
        RaidLootData
                .forRD(RaidDifficulty.HERO)
                .setEmeralds(16, 64)
                .setValuablesPoolPulls(5, 7, 10)
                .setValuablesPool(
                        LootEntry.of(Items.IRON_INGOT, 0.6F),
                        LootEntry.of(Items.GOLD_INGOT, 0.3F),
                        LootEntry.of(Items.DIAMOND, 0.1F)
                )
                .setTotemsPoolPulls(1, 2, 3)
                .setTotemsPool(TOTEM_OF_SPEED, TOTEM_OF_POISON, TOTEM_OF_LEVITATION, TOTEM_OF_FREEZING)
                .setArmorLoot(new Tuple<>(ArmorMaterials.IRON, 3), new Tuple<>(ArmorMaterials.DIAMOND, 1))
                .setEnchantmentCount(2)
                .setEnchantmentsLoot(
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.RAIDERS_BANE.get(), 3, 1, 2),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), 1, 1, 1),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.PROJECTILE_EVASION.get(), 2, 1, 1)
                )
                .register();

        RaidLootData
                .forRD(RaidDifficulty.LEGEND)
                .setEmeralds(64, 128)
                .setValuablesPoolPulls(8, 12, 16)
                .setValuablesPool(
                        LootEntry.of(Items.IRON_INGOT, 0.5F),
                        LootEntry.of(Items.GOLD_INGOT, 0.3F),
                        LootEntry.of(Items.DIAMOND, 0.15F),
                        LootEntry.of(Items.NETHERITE_INGOT, 0.05F)
                )
                .setTotemsPoolPulls(3, 5, 7)
                .setTotemsPool(TOTEM_OF_SPEED, TOTEM_OF_POISON, TOTEM_OF_LEVITATION, TOTEM_OF_LIGHTNING, TOTEM_OF_TELEPORTATION, TOTEM_OF_FIREBALLS, TOTEM_OF_FREEZING, TOTEM_OF_PERSISTENCE)
                .setArmorLoot(new Tuple<>(ArmorMaterials.IRON, 2), new Tuple<>(ArmorMaterials.DIAMOND, 2))
                .setEnchantmentCount(4)
                .setEnchantmentsLoot(
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.RAIDERS_BANE.get(), 5, 1, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.INVISIBILITY.get(), 4, 1, 2),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), 2, 1, 1),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_BURST.get(), 2, 1, 2),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_RESISTANCE.get(), 2, 1, 2),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.LIGHTNING_RESISTANCE.get(), 1, 1, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.PROJECTILE_EVASION.get(), 3, 1, 2)
                )
                .register();

        RaidLootData
                .forRD(RaidDifficulty.MASTER)
                .setEmeralds(128, 256)
                .setValuablesPoolPulls(12, 16, 20)
                .setValuablesPool(
                        LootEntry.of(Items.IRON_INGOT, 0.3F),
                        LootEntry.of(Items.GOLD_INGOT, 0.3F),
                        LootEntry.of(Items.DIAMOND, 0.25F),
                        LootEntry.of(Items.NETHERITE_INGOT, 0.15F)
                )
                .setTotemsPoolPulls(5, 7, 9)
                .setTotemsPool(TOTEM_OF_SPEED, TOTEM_OF_POISON, TOTEM_OF_LEVITATION, TOTEM_OF_LIGHTNING, TOTEM_OF_TELEPORTATION, TOTEM_OF_FIREBALLS, TOTEM_OF_FREEZING, TOTEM_OF_PERSISTENCE, TOTEM_OF_DESTINY, TOTEM_OF_PROTECTION)
                .setArmorLoot(new Tuple<>(ArmorMaterials.DIAMOND, 3), new Tuple<>(ArmorMaterials.NETHERITE, 1))
                .setEnchantmentCount(6)
                .setEnchantmentsLoot(
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.RAIDERS_BANE.get(), 4, 2, 4),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.INVISIBILITY.get(), 2, 1, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), 2, 1, 2),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_BURST.get(), 2, 1, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_RESISTANCE.get(), 2, 1, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.LIGHTNING_RESISTANCE.get(), 2, 1, 5),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.PROJECTILE_EVASION.get(), 3, 1, 3)
                )
                .register();

        RaidLootData
                .forRD(RaidDifficulty.GRANDMASTER)
                .setEmeralds(320, 640)
                .setValuablesPoolPulls(25, 30, 50)
                .setValuablesPool(
                        LootEntry.of(Items.IRON_INGOT, 0.325F),
                        LootEntry.of(Items.GOLD_INGOT, 0.25F),
                        LootEntry.of(Items.DIAMOND, 0.3F),
                        LootEntry.of(Items.NETHERITE_INGOT, 0.175F)
                )
                .setTotemsPoolPulls(8, 10, 12)
                .setTotemsPool(TOTEM_OF_SPEED, TOTEM_OF_POISON, TOTEM_OF_LEVITATION, TOTEM_OF_LIGHTNING, TOTEM_OF_TELEPORTATION, TOTEM_OF_FIREBALLS, TOTEM_OF_FREEZING, TOTEM_OF_PERSISTENCE, TOTEM_OF_DESTINY, TOTEM_OF_PROTECTION)
                .setArmorLoot(new Tuple<>(ArmorMaterials.NETHERITE, 1))
                .setEnchantmentCount(10)
                .setEnchantmentsLoot(
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.RAIDERS_BANE.get(), 1, 3, 5),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.INVISIBILITY.get(), 1, 2, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), 1, 2, 2),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_BURST.get(), 1, 2, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.CRITICAL_RESISTANCE.get(), 1, 2, 3),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.LIGHTNING_RESISTANCE.get(), 1, 3, 5),
                        new EnchantmentLootEntry(DifficultRaidsEnchantments.PROJECTILE_EVASION.get(), 1, 2, 3)
                )
                .register();
    }

    public static List<ItemStack> generateArmorLoot(RaidDifficulty rd)
    {
        Random r = new Random();

        if(!rd.is(RaidDifficulty.DEFAULT, RaidDifficulty.GRANDMASTER))
        {
            //TODO: Special Effects for Raid Armor, and an entire set of Raid armor for each Difficulty

            int count = switch(rd) {
                case HERO -> 2;
                case LEGEND -> 3;
                case MASTER -> 4;
                default -> 0;
            };

            RaidLootData data = RAID_LOOT.get(rd);
            List<ItemStack> loot = new ArrayList<>();
            for(int i = 0; i < count; i++)
            {
                ItemStack armor = new ItemStack(data.armorMaterials.get(r.nextInt(data.armorMaterials.size())));
                Map<Enchantment, Integer> enchantments = new HashMap<>();

                int[] protectionLevels = switch(rd) {
                    case HERO -> new int[]{1, 3};
                    case LEGEND -> new int[]{1, 4};
                    case MASTER -> new int[]{2, 5};
                    default -> new int[]{0, 0};
                };
                int protection = r.nextInt(protectionLevels[0], protectionLevels[1] + 1);
                enchantments.put(Enchantments.ALL_DAMAGE_PROTECTION, protection);

                int unbreaking = switch(rd) {
                    case HERO -> 1;
                    case LEGEND -> 2;
                    case MASTER -> 3;
                    default -> 0;
                };
                enchantments.put(Enchantments.UNBREAKING, unbreaking);

                if(rd.is(RaidDifficulty.MASTER)) enchantments.put(Enchantments.MENDING, 1);

                EnchantmentHelper.setEnchantments(enchantments, armor);
                loot.add(armor);
            }

            return loot;
        }
        else if(rd.is(RaidDifficulty.GRANDMASTER))
        {
            List<ItemStack> pieces = new ArrayList<>();
            List<Item> pool = List.of(GRANDMASTER_HELMET.get(), GRANDMASTER_CHESTPLATE.get(), GRANDMASTER_LEGGINGS.get(), GRANDMASTER_BOOTS.get());

            int count = r.nextInt(2, 4);
            for(int i = 0; i < count; i++) pieces.add(new ItemStack(pool.get(r.nextInt(pool.size()))));

            Map<Enchantment, Integer> enchantments = new HashMap<>();
            enchantments.put(Enchantments.ALL_DAMAGE_PROTECTION, 6);
            enchantments.put(Enchantments.UNBREAKING, 5);
            enchantments.put(Enchantments.MENDING, 1);

            pieces.forEach(stack -> EnchantmentHelper.setEnchantments(enchantments, stack));

            return pieces;
        }
        else return new ArrayList<>();
    }

    public static class RaidLootData
    {
        private RaidDifficulty raidDifficulty;

        public int[] emeralds;

        public Map<Difficulty, Integer> valuablesPulls;
        public List<LootEntry> valuablesPool;

        public Map<Difficulty, Integer> totemsPulls;
        public List<Item> totemsPool;

        public List<Item> armorMaterials;

        public int enchantmentCount;
        public List<EnchantmentLootEntry> enchantmentsPool;

        RaidLootData()
        {
            this.emeralds = new int[2];

            this.valuablesPulls = new LinkedHashMap<>();
            this.valuablesPool = new ArrayList<>();

            this.totemsPulls = new LinkedHashMap<>();
            this.totemsPool = new ArrayList<>();

            this.armorMaterials = new ArrayList<>();

            this.enchantmentCount = 0;
            this.enchantmentsPool = new ArrayList<>();
        }

        //Registry

        static RaidLootData forRD(RaidDifficulty raidDifficulty)
        {
            RaidLootData data = new RaidLootData();
            data.raidDifficulty = raidDifficulty;
            return data;
        }

        final RaidLootData setEmeralds(int min, int max)
        {
            this.emeralds = new int[]{min, max};
            return this;
        }

        final RaidLootData setValuablesPoolPulls(int easy, int normal, int hard)
        {
            this.valuablesPulls.put(Difficulty.EASY, easy);
            this.valuablesPulls.put(Difficulty.NORMAL, normal);
            this.valuablesPulls.put(Difficulty.HARD, hard);
            return this;
        }

        final RaidLootData setValuablesPool(LootEntry... entries)
        {
            this.valuablesPool = new ArrayList<>(List.of(entries));
            return this;
        }

        final RaidLootData setTotemsPoolPulls(int easy, int normal, int hard)
        {
            this.totemsPulls.put(Difficulty.EASY, easy);
            this.totemsPulls.put(Difficulty.NORMAL, normal);
            this.totemsPulls.put(Difficulty.HARD, hard);
            return this;
        }

        @SafeVarargs
        final RaidLootData setTotemsPool(RegistryObject<Item>... totems)
        {
            this.totemsPool.addAll(Stream.of(totems).map(RegistryObject::get).toList());
            return this;
        }

        @SafeVarargs
        final RaidLootData setArmorLoot(Tuple<ArmorMaterials, Integer>... armorMaterials)
        {
            for(Tuple<ArmorMaterials, Integer> pair : armorMaterials)
            {
                ArmorMaterials a = pair.getA();

                for(int i = 0; i < pair.getB(); i++)
                {
                    if(a.equals(ArmorMaterials.IRON)) this.armorMaterials.addAll(List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS));
                    else if(a.equals(ArmorMaterials.DIAMOND)) this.armorMaterials.addAll(List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS));
                    else if(a.equals(ArmorMaterials.NETHERITE)) this.armorMaterials.addAll(List.of(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS));
                }
            }

            return this;
        }

        final RaidLootData setEnchantmentCount(int count)
        {
            this.enchantmentCount = count;
            return this;
        }

        final RaidLootData setEnchantmentsLoot(EnchantmentLootEntry... enchantments)
        {
            this.enchantmentsPool.addAll(List.of(enchantments));
            return this;
        }

        //Getters
        public final Item pullValuable(Random rand)
        {
            float r = rand.nextFloat();

            for(int i = 0; i < this.valuablesPool.size(); i++)
            {
                float threshold = this.valuablesPool.get(i).chance;
                if(i > 0) for(int j = i - 1; j >= 0; j--) threshold += this.valuablesPool.get(j).chance;

                if(r < threshold) return this.valuablesPool.get(i).item;
            }

            return null;
        }

        public final ItemStack pullEnchantment(Random rand)
        {
            List<EnchantmentLootEntry> pool = new ArrayList<>();
            for(EnchantmentLootEntry e : this.enchantmentsPool) for(int i = 0; i < e.weight(); i++) pool.add(e);

            EnchantmentLootEntry selected = pool.get(rand.nextInt(pool.size()));
            int level = selected.minLevel() == selected.maxLevel() ? selected.maxLevel() : rand.nextInt(selected.minLevel(), selected.maxLevel() + 1);

            return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(selected.enchantment(), level));
        }

        void register()
        {
            RAID_LOOT.put(this.raidDifficulty, this);
        }
    }

    private record EnchantmentLootEntry(Enchantment enchantment, int weight, int minLevel, int maxLevel) {}

    public static class LootEntry
    {
        public Item item;
        public float chance;

        static LootEntry of(Item item, float chance)
        {
            LootEntry entry = new LootEntry();
            entry.item = item;
            entry.chance = chance;
            return entry;
        }
    }
}
