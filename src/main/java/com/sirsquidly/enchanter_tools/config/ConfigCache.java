package com.sirsquidly.enchanter_tools.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Set;

/**
 * 	This is simply a static form of the config, for reference throughout the mod.
 */
public class ConfigCache
{
    /** Blocks */

    public static List<ItemStack> bookshelfAcceptedBooks = Lists.newArrayList();
    public static Set<String> bookshelfAcceptedNames = Sets.newHashSet();

    public static List<String> brazierBurnEnchantBlacklist = Lists.newArrayList();
    public static List<ItemStack> brazierBurnItemBlacklist = Lists.newArrayList();

    /** Items */

    public static List<String> extractBookExtractEnchantBlacklist = Lists.newArrayList();
    public static List<ItemStack> extractBookExtractItemBlacklist = Lists.newArrayList();

    public static List<ResourceLocation> eightBallInjectLootTables = Lists.newArrayList();
    public static List<Float> eightBallInjectChances = Lists.newArrayList();

    public static List<ResourceLocation> inkwellInjectLootTables = Lists.newArrayList();
    public static List<Float> inkwellInjectChances = Lists.newArrayList();

    /** Effects */

    public static List<ResourceLocation> comprehensionEntityBlacklist = Lists.newArrayList();
}