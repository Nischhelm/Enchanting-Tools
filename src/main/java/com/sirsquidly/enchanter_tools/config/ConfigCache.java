package com.sirsquidly.enchanter_tools.config;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * 	This is simply a static form of the config, for reference throughout the mod.
 */
public class ConfigCache
{
    /** Blocks */

    public static List<String> brazierBurnEnchantBlacklist = Lists.newArrayList();
    public static List<ItemStack> brazierBurnItemBlacklist = Lists.newArrayList();
    public static double brazierDurabilityCost = Config.block.arcaneBrazier.durabilityCost;
    public static boolean brazierEnable = Config.block.arcaneBrazier.enable;

    public static List<ItemStack> bookshelfAcceptedBooks = Lists.newArrayList();
    public static boolean bookshelfEnable = Config.block.chiseledBookshelf.enable;

    public static boolean ponderingOrbEnable = Config.block.ponderingOrb.enable;
    public static int ponderingOrbPonderXP = Config.block.ponderingOrb.ponderingExperience;
    public static int ponderingOrbPonderTime = Config.block.ponderingOrb.ponderingTimer;
    public static int ponderingOrbRerollCost = Config.block.ponderingOrb.orbRerollXPCost;
    public static int ponderingOrbRecollCooldown = Config.block.ponderingOrb.orbRerollTickCooldown;

    /** Items */

    public static boolean extractBookEnable = Config.item.extractingBook.enable;
    public static List<String> extractBookExtractEnchantBlacklist = Lists.newArrayList();
    public static List<ItemStack> extractBookExtractItemBlacklist = Lists.newArrayList();

    public static boolean eightBallEnable = Config.item.enchantedEightBall.enable;
    public static boolean eightBallFortunesEnchantExclusive = Config.item.enchantedEightBall.enchantedDivination;
    public static List<ResourceLocation> eightBallInjectLootTables = Lists.newArrayList();
    public static List<Float> eightBallInjectChances = Lists.newArrayList();

    public static boolean inkwellEnable = Config.item.inkwell.enable;
    public static List<ResourceLocation> inkwellInjectLootTables = Lists.newArrayList();
    public static List<Float> inkwellInjectChances = Lists.newArrayList();

    public static boolean burnisherEnable = Config.item.obsidianBurnisher.enable;
    public static double burnisherDurabilityCost = Config.item.obsidianBurnisher.durabilityCost;
    public static int burnisherRepairCost = Config.item.obsidianBurnisher.repairCostAltering;

    /** Effects */

    public static boolean comprehensionEnable = Config.potionEffects.comprehension.enable;
    public static boolean comprehensionBlacklistBosses = Config.potionEffects.comprehension.bossesAreBlacklisted;
    public static int comprehensionXPBottleTime = Config.potionEffects.comprehension.grantedByBottleOEnchanting;
    public static List<ResourceLocation> comprehensionEntityBlacklist = Lists.newArrayList();
}