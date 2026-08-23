package com.sirsquidly.enchanter_tools.common;

import com.sirsquidly.enchanter_tools.common.blocks.BlockArcaneBrazier;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileArcaneBrazier;
import com.sirsquidly.enchanter_tools.common.items.ItemLapisRune;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public final class RuneEnchantmentLogic
{
    /** Generates the enchantments pool.
     *
     * Note: By Default. Target Item is just a Book. */
    public static List<EnchantmentData> buildRunePool(ItemLapisRune.RuneType runeType, ItemStack targetItem, int power, boolean treasureAllowed, Random rand, Set<ResourceLocation> filter)
    {
        List<EnchantmentData> pool = new ArrayList<>(), out = new ArrayList<>();

        for (Enchantment enchantment : buildAllowedSet(runeType, targetItem, treasureAllowed))
        {
            if (filter.contains(enchantment.getRegistryName())) continue;

            for (int level = enchantment.getMaxLevel(); level >= enchantment.getMinLevel(); level--)
            {
                if (power >= enchantment.getMinEnchantability(level) && power <= enchantment.getMaxEnchantability(level))
                {
                    pool.add(new EnchantmentData(enchantment, level));
                    break;
                }
            }
        }

        Collections.shuffle(pool, rand);

        for (EnchantmentData enchantmentData : pool)
        {
            if (out.stream().allMatch(o -> enchantmentData.enchantment.isCompatibleWith(o.enchantment)))
            {
                out.add(enchantmentData);
                if (rand.nextInt(100) < 40) break;
            }
        }

        return out;
    }

    /** Generates the list of every enchantment this Rune can give. */
    public static Set<Enchantment> buildAllowedSet(ItemLapisRune.RuneType runeType, ItemStack item, boolean isMimikedTreasureAllowed)
    {
        Set<Enchantment> allowed = new LinkedHashSet<>();

        if (runeType.getMimicItem() != null)
        {
            ItemStack mimic = new ItemStack(runeType.getMimicItem());

            for (Enchantment e : Enchantment.REGISTRY)
            {
                if (e == null) continue;
                if (!isMimikedTreasureAllowed && (e.isTreasureEnchantment() || e.isCurse())) continue;

                if (e.canApplyAtEnchantingTable(mimic) && (e.canApply(item) || item.getItem() == Items.BOOK)) allowed.add(e);
            }
        }

        /*
        * SORT the enchants so only COMPATIBLE ones are added to the item!
        * Also, no filtering for Enchanting Table check nor treasure, the whitelist is followed exactly
        * */
        for (Enchantment e : runeType.getEnchantmentsWhitelist())
        {
            if (e == null) continue;
            if (e.canApply(item) || item.getItem() == Items.BOOK) allowed.add(e);
        }

        /* Simply dump any blacklisted enchantments. No fancy filters needed! */
        allowed.removeAll(runeType.getEnchantmentsBlacklist());

        return allowed;
    }

    public static Set<ResourceLocation> getBrazierBlockedEnchants(World world, BlockPos tablePos)
    {
        Set<ResourceLocation> blocked = new HashSet<>();

        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                if (x == 0 && z == 0) continue;

                getBrazierEnchantment(world, tablePos.add(x * 2, 0, z * 2), blocked);
                getBrazierEnchantment(world, tablePos.add(x * 2, 1, z * 2), blocked);

                if (x != 0 && z != 0)
                {
                    getBrazierEnchantment(world, tablePos.add(x * 2, 0, z), blocked);
                    getBrazierEnchantment(world, tablePos.add(x * 2, 1, z), blocked);
                    getBrazierEnchantment(world, tablePos.add(x, 0, z * 2), blocked);
                    getBrazierEnchantment(world, tablePos.add(x, 1, z * 2), blocked);
                }
            }
        }

        return blocked;
    }

    private static void getBrazierEnchantment(World world, BlockPos pos, Set<ResourceLocation> blocked)
    {
        IBlockState state = world.getBlockState(pos);
        TileEntity te = world.getTileEntity(pos);
        if (state.getBlock() instanceof BlockArcaneBrazier && te instanceof TileArcaneBrazier)
        {
            Enchantment ench = ((TileArcaneBrazier) te).getSavedEnchantment();
            if (state.getValue(BlockArcaneBrazier.FLAME) != 2 || ench == null || ench.getRegistryName() == null)
                return;
            blocked.add(ench.getRegistryName());
        }
    }
}