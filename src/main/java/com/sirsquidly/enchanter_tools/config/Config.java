package com.sirsquidly.enchanter_tools.config;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.common.config.Config(modid = enchanterTools.MOD_ID, name = enchanterTools.CONFIG_NAME)
@net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.title")
@Mod.EventBusSubscriber(modid = enchanterTools.MOD_ID)
public class Config
{
    @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.configVersion")
    @net.minecraftforge.common.config.Config.Comment({
            "Config Versions help inform modpack makers/config users if changes have been made to the config between updates. These differ from main versioning, since the config file is static.",
            "Basically, you compare the current default of this value, to the default of when you generated it.",
            "",
            "The versioning follows:",
            "0.0.x - Default values have been slightly adjusted.",
            "0.x.0 - Config options have been added.",
            "x.0.0 - Previous Config Options have been completely overhauled and/or removed. Creating a fresh file is recommended."
    })
    public static String configVersion = "1.1.0";


    @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block")
    @net.minecraftforge.common.config.Config.Comment("Config related to blocks")
    public static configBlock block = new configBlock();

    public static class configBlock
    {
        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.arcane_brazier")
        @net.minecraftforge.common.config.Config.Comment("Config for the Arcane Brazier")
        public configBlock.configArcaneBrazier arcaneBrazier = new configBlock.configArcaneBrazier();

        public static class configArcaneBrazier
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.arcane_brazier.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Arcane Brazier.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.arcane_brazier.collisionDamage")
            @net.minecraftforge.common.config.Config.Comment("Colliding with a lit brazier will cause damage.")
            public boolean collisionDamage = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.arcane_brazier.durabilityCost")
            @net.minecraftforge.common.config.Config.Comment("How much Durability an item loses when an enchantment is stripped via the Arcane Brazier. Converted to a percent of the item's Max Durability. ")
            @net.minecraftforge.common.config.Config.RangeDouble(min = -1, max = 1)
            public double durabilityCost = 0.05D;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.arcane_brazier.burningEnchantBlacklist")
            @net.minecraftforge.common.config.Config.Comment("A list of enchantments that cannot be set into the Brazier. This does not prevent existing braziers from removing it still.")
            public String[] burningEnchantBlacklist =
                    {
                            "minecraft:binding_curse",
                            "minecraft:vanishing_curse"
                    };

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.arcane_brazier.burningItemBlacklist")
            @net.minecraftforge.common.config.Config.Comment("A list of items the Arcane Brazier cannot strip Enchantments from.")
            public String[] burningItemBlacklist =
                    {
                            "enchanter_tools:enchanted_inkwell"
                    };
        }

        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.chiseledBookshelf")
        @net.minecraftforge.common.config.Config.Comment("Config for the Chiseled Bookshelf")
        public configBlock.configChiseledBookshelf chiseledBookshelf = new configBlock.configChiseledBookshelf();

        public static class configChiseledBookshelf
        {
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.chiseledBookshelf.acceptedBooks")
            @net.minecraftforge.common.config.Config.Comment("Books that can be placed into the Chiseled Bookshelf.")
            public String[] acceptedBooks =
                    {
                            "minecraft:book",
                            "minecraft:writable_book",
                            "minecraft:written_book",
                            "minecraft:enchanted_book",
                            "minecraft:knowledge_book",
                            "enchanter_tools:extracting_book",
                            "ancientbeasts:shield_book",
                            "ancientbeasts:bestiary",
                            "ee:end_info_book",
                            "harkenscythe:ancient_necronomicon",
                            "harkenscythe:carnage_book",
                            "harkenscythe:shadow_book",
                            "harkenscythe:refresh_tome",
                            "harkenscythe:reaper_guidebook",
                            "quark:ancient_tome"
                    };

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.chiseledBookshelf.acceptedNames")
            @net.minecraftforge.common.config.Config.Comment("Item Ids that contain these words can be placed into the Chiseled Bookshelf.")
            public String[] acceptedNames = new String[]
                    {
                            "book",
                            "tome",
                            "lexicon",
                            "nomicon",
                            "manual",
                            "knowledge",
                            "pedia",
                            "compendium",
                            "guide",
                            "codex",
                            "dictionary",
                            "journal",
                            "tablet",
                            "grimoire",
                            "bestiary"
                    };

            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.chiseledBookshelf.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Chiseled Bookshelf.")
            public boolean enable = true;
        }


        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb")
        @net.minecraftforge.common.config.Config.Comment("Config for the Pondering Orb")
        public configBlock.configPonderingOrb ponderingOrb = new configBlock.configPonderingOrb();

        public static class configPonderingOrb
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Pondering Orb.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.ponderingExperience")
            @net.minecraftforge.common.config.Config.Comment("How much XP is given by pondering the orb. Setting to 0 disables any free XP.")
            @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 10000)
            public int ponderingExperience = 1;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.ponderingRange")
            @net.minecraftforge.common.config.Config.Comment("The maximum distance from the pondering orb the player can be to receive free XP.")
            @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 16)
            public int ponderingRange = 8;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.ponderingRequiresLook")
            @net.minecraftforge.common.config.Config.Comment("Free XP requires the player to be looking directly at the Pondering Orb.")
            public boolean ponderingRequiresLook = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.ponderingTimer")
            @net.minecraftforge.common.config.Config.Comment("How long (in seconds) the orb checks for pondering players.")
            @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 9999)
            public int ponderingTimer = 5;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.orbRerollXPCost")
            @net.minecraftforge.common.config.Config.Comment("How many Levels are spent to re-roll the Enchanting Table using the Pondering Orb.")
            @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 10000)
            public int orbRerollXPCost = 0;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.block.pondering_orb.orbRerollTickCooldown")
            @net.minecraftforge.common.config.Config.Comment("How long (in ticks) the cooldown for Enchanting Table re-rolls is.")
            @net.minecraftforge.common.config.Config.RangeInt(min = -1, max = 999999)
            public int orbRerollTickCooldown = 20;
        }
    }

    @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item")
    @net.minecraftforge.common.config.Config.Comment("Config related to Items")
    public static configItem item = new configItem();

    public static class configItem
    {
        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.extractingBook")
        @net.minecraftforge.common.config.Config.Comment("All config for blocks related to Trees")
        public configItem.configExtractingBook extractingBook = new configItem.configExtractingBook();

        public static class configExtractingBook
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.extractingBook.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Extracting Book.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.extractingBook.extractEnchantBlacklist")
            @net.minecraftforge.common.config.Config.Comment("A list of enchantments that cannot be extracted via the Extracting Book.")
            public String[] extractEnchantBlacklist =
                    {
                            "minecraft:binding_curse",
                            "minecraft:vanishing_curse"
                    };

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.extractingBook.extractItemBlacklist")
            @net.minecraftforge.common.config.Config.Comment("A list of items the Extracting Book cannot pull Enchantments from.")
            public String[] extractItemBlacklist =
                    {
                            "enchanter_tools:enchanted_inkwell"
                    };
        }

        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedEightBall")
        @net.minecraftforge.common.config.Config.Comment("Config for the Enchanted 8 Ball")
        public configItem.configEnchantedEightBall enchantedEightBall = new configItem.configEnchantedEightBall();

        public static class configEnchantedEightBall
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedEightBall.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Enchanted 8 Ball.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedEightBall.enchantedDivination")
            @net.minecraftforge.common.config.Config.Comment("Sets fortune-telling to be exclusive to an 8 Ball that is Enchanted.")
            public boolean enchantedDivination = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedEightBall.eight_ball_lootTables")
            @net.minecraftforge.common.config.Config.Comment("Loot Tables the Enchanted 8 Ball will be injected into. Non-replacing.")
            public String[] eight_ball_lootTables =
                    {
                            "minecraft:chests/end_city_treasure=0.08",
                            "minecraft:chests/simple_dungeon=0.02",
                            "minecraft:chests/stronghold_corridor=0.02",
                            "minecraft:chests/woodland_mansion=0.069",
                            "da:gaelon_dungeon=0.06",
                            "da:obsidian_arena=0.08"
                    };
        }

        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedInkwell")
        @net.minecraftforge.common.config.Config.Comment("Config for the Enchanted Inkwell")
        public configItem.configInkwell inkwell = new configItem.configInkwell();

        public static class configInkwell
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedInkwell.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Enchanted Inkwell.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.enchantedInkwell.inkwell_lootTables")
            @net.minecraftforge.common.config.Config.Comment("Loot Tables the Enchanted Inkwell will be injected into. Non-replacing.")
            public String[] inkwell_lootTables =
                    {
                            "minecraft:chests/desert_pyramid=0.001",
                            "minecraft:chests/end_city_treasure=0.001",
                            "minecraft:chests/jungle_temple=0.001",
                            "minecraft:chests/simple_dungeon=0.001",
                            "minecraft:chests/stronghold_corridor=0.001",
                            "minecraft:chests/stronghold_crossing=0.001",
                            "minecraft:chests/stronghold_library=0.05",
                            "minecraft:chests/woodland_mansion=0.001",
                            "deeperdepths:ominous_vault=0.01",
                            "da:crypt_forgotten_temple=0.0004",
                            "da:flame_arena_chests=0.0004",
                            "da:frozen_castle_key=0.0001",
                            "da:frozen_castle_secret=0.0001",
                            "da:gaelon_dungeon=0.0004",
                            "da:high_court_city=0.001",
                            "da:lich_tower=0.01",
                            "da:night_lich=0.01",
                            "da:obsidian_arena=0.0006",
                            "da:obsidian_arena_key=0.0006",
                            "da:rot_hold=0.0001",
                            "da:rot_hold_key=0.0001",
                            "db:ocean_temple=0.0005",
                            "db:ocean_temple_level_two=0.001",
                            "mimicfish:structures/lunkertooth_ruins=0.0005"
                    };
        }

        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.lapisRune")
        @net.minecraftforge.common.config.Config.Comment("Config for the Lapis Rune")
        public configItem.configLapisRune lapisRune = new configItem.configLapisRune();

        public static class configLapisRune
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.lapisRune.runeTypes")
            @net.minecraftforge.common.config.Config.Comment({
                    "Registers Lapis Rune Types.",
                    "Formatted as \"[Name]-[Item]-[Level Requirement Multiplier]-[Cost Multiplier]-[Whitelisted Enchantment];[Whitelisted Enchantment]...-[Blacklisted Enchantment];[Blacklisted Enchantment]...\"",
                    "",
                    "[Name] - The ID of the item (appended with '_lapis_rune').",
                    "[Item] - The item this rune will mimic in the table (such as a pickaxe rune getting pickaxe enchantments). Defaults to none.",
                    "[Level Requirement Multiplier] - Multiplies the minimum levels required for each slot. Defaults to 1.0.",
                    "[Cost Multiplier] - Multiplies the experience consumed upon enchanting. Defaults to 1.0.",
                    "[Whitelisted Enchantment] - Enchantments that are in the enchanting pool. Enchants already available to the mimicked item do not need to be copied here.",
                    "[Blacklisted Enchantment] - Enchantments that will never appear in the enchanting pool.",
            })
            public String[] runeTypes = {
                    "axe-minecraft:golden_axe-1.2-1.5",
                    "boots-minecraft:golden_boots-1.2-1.5",
                    "bow-minecraft:bow-1.2-1.5",
                    "chestplate-minecraft:golden_chestplate-1.2-1.5",
                    "fishing_rod-minecraft:fishing_rod-1.2-1.5",
                    "helmet-minecraft:golden_helmet-1.2-1.5",
                    "leggings-minecraft:golden_leggings-1.2-1.5",
                    "pickaxe-minecraft:golden_pickaxe--1.21.5",
                    "sword-minecraft:golden_sword-1.2-1.5",
                    "arbitrary_example---1-minecraft:efficiency;minecraft:fortune-minecraft:silk_touch"};
        }

        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.obsidianBurnisher")
        @net.minecraftforge.common.config.Config.Comment("Config for the Obsidian Burnisher")
        public configItem.configObsidianBurnisher obsidianBurnisher = new configItem.configObsidianBurnisher();

        public static class configObsidianBurnisher
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.obsidianBurnisher.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Obsidian Burnisher.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.obsidianBurnisher.durabilityCost")
            @net.minecraftforge.common.config.Config.Comment("How much Durability the targeted item loses when the Burnisher is used on it. Converted to a percent of the item's Max Durability. ")
            @net.minecraftforge.common.config.Config.RangeDouble(min = -1, max = 1)
            public double durabilityCost = 0.1D;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.obsidianBurnisher.repairCostAltering")
            @net.minecraftforge.common.config.Config.Comment("How much Repair Cost is removed by using the Burnisher.")
            @net.minecraftforge.common.config.Config.RangeInt(min = -100, max = 100)
            public int repairCostAltering = -2;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.item.obsidianBurnisher.tooltipVisibility")
            @net.minecraftforge.common.config.Config.Comment({
                    "Determines when the 'Anvil Uses' tooltip will appear on valid items while the obsidian Burnisher is in the player's inventory.",
                    "ALWAYS: The tooltip will always display",
                    "SHIFT: The tooltip will only display when the shift key is held",
                    "ANVIL: The tooltip will only display when ",
                    "DISABLED: The tooltip will never display"
            })
            public TooltipVisibility tooltipVisibility = TooltipVisibility.SHIFT;

            public enum TooltipVisibility {
                ALWAYS,
                SHIFT,
                ANVIL,
                DISABLED
            }
        }
    }

    @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.potion_effects")
    @net.minecraftforge.common.config.Config.Comment("Config related to Potion Effects")
    public static configPotionEffects potionEffects = new configPotionEffects();

    public static class configPotionEffects
    {
        @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.potion_effects.comprehension")
        @net.minecraftforge.common.config.Config.Comment("Config for the Comprehension effect")
        public configPotionEffects.configComprehension comprehension = new configPotionEffects.configComprehension();

        public static class configComprehension
        {
            @net.minecraftforge.common.config.Config.RequiresMcRestart
            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.potion_effects.comprehension.enable")
            @net.minecraftforge.common.config.Config.Comment("Enable the Comprehension effect.")
            public boolean enable = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.potion_effects.comprehension.bossesAreBlacklisted")
            @net.minecraftforge.common.config.Config.Comment("Makes any mob set as a boss immune to Comprehension.")
            public boolean bossesAreBlacklisted = true;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.potion_effects.comprehension.grantedByBottleOEnchanting")
            @net.minecraftforge.common.config.Config.Comment("How long Comprehension is given (in ticks) to mobs near a Bottle O' Enchanting's impact. -1 Disables this.")
            @net.minecraftforge.common.config.Config.RangeInt(min = -1)
            public int grantedByBottleOEnchanting = 3000;

            @net.minecraftforge.common.config.Config.LangKey("config.enchanter_tools.potion_effects.comprehension.entityBlacklist")
            @net.minecraftforge.common.config.Config.Comment("A list of entities that are immune to Comprehension.")
            public String[] entityBlacklist =
                    {
                            "minecraft:wither"
                    };
        }
    }

    @Mod.EventBusSubscriber(modid = enchanterTools.MOD_ID)
    public static class ConfigSyncHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if(event.getModID().equals(enchanterTools.MOD_ID))
            {
                ConfigManager.sync(enchanterTools.MOD_ID, net.minecraftforge.common.config.Config.Type.INSTANCE);
                ConfigParser.breakupConfigArrays();
            }
        }
    }
}