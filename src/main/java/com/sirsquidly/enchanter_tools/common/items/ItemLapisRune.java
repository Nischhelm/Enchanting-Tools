package com.sirsquidly.enchanter_tools.common.items;

import com.google.common.collect.Maps;
import com.sirsquidly.enchanter_tools.config.Config;
import com.sirsquidly.enchanter_tools.config.ConfigParser;
import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

public class ItemLapisRune extends Item
{
    private final RuneType runeType;

    public ItemLapisRune(RuneType spearMaterialIn)
    {
        this.runeType = spearMaterialIn;
        this.setCreativeTab(CreativeTabs.MISC);
    }

    public RuneType getRuneType() { return runeType; }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.enchantment_table_ingredient.label"));
        tooltip.add("");
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.enchantment_table_ingredient.applies"));
        tooltip.add(TextFormatting.BLUE + I18n.format("description.enchanter_tools.enchantment_table_ingredient.books"));
    }

    public static class RuneType
    {
        private static final LinkedHashMap<String, RuneType> TYPES = Maps.newLinkedHashMap();

        public static void readFromConfig()
        {
            for (String string : Config.item.lapisRune.runeTypes)
            {
                if (string == null || string.isEmpty()) continue;

                String[] split = string.split("-");
                String name = split[0];

                Item mimicItem = ItemStack.EMPTY.getItem();
                float lvlRqrMult = 1.0F;
                float costMult = 1.0F;
                Set<Enchantment> whitelist = new HashSet<>();
                Set<Enchantment> blacklist = new HashSet<>();

                if (split.length > 1 && !split[1].isEmpty())
                {
                    ItemStack stack = ConfigParser.getItemStackFromString(split[1].trim());
                    if (!stack.isEmpty())
                    { mimicItem = stack.getItem(); }
                    else
                    { enchanterTools.LOGGER.error("Invalid mimic item in rune: " + split[1]); }
                }
                if (split.length > 2 && !split[2].isEmpty())
                {
                    try { lvlRqrMult = Float.parseFloat(split[2].trim()); }
                    catch (Exception e)
                    { enchanterTools.LOGGER.error(split[2] + " is invalid level requirement multiplier."); }
                }
                if (split.length > 3 && !split[3].isEmpty())
                {
                    try { costMult = Float.parseFloat(split[3].trim()); }
                    catch (Exception e)
                    { enchanterTools.LOGGER.error(split[3] + " is invalid cost multiplier."); }
                }
                if (split.length > 4 && !split[4].isEmpty())
                {
                    String[] enchants = split[4].split(";");

                    for (String e : enchants)
                    {
                        if (e == null || e.trim().isEmpty()) continue;

                        Enchantment ench = ConfigParser.getEnchantmentFromString(e.trim());

                        if (ench != null) { whitelist.add(ench); }
                        else
                        { enchanterTools.LOGGER.error(e + " is not a valid enchantment."); }
                    }
                }
                if (split.length > 5 && !split[5].isEmpty())
                {
                    String[] enchants = split[5].split(";");

                    for (String e : enchants)
                    {
                        if (e == null || e.trim().isEmpty()) continue;

                        Enchantment ench = ConfigParser.getEnchantmentFromString(e.trim());

                        if (ench != null) { blacklist.add(ench); }
                        else
                        { enchanterTools.LOGGER.error(e + " is not a valid enchantment."); }
                    }
                }

                register(name, mimicItem, lvlRqrMult, costMult, whitelist, blacklist);
            }
        }

        public static ItemLapisRune.RuneType register(String name, Item mimicItemIn, float levelRequirementMultIn, float costMultIn, Set<Enchantment> enchantmentsWhitelistIn, Set<Enchantment> enchantmentsBlacklistIn)
        {
            ItemLapisRune.RuneType type = new ItemLapisRune.RuneType(name, mimicItemIn, levelRequirementMultIn, costMultIn, enchantmentsWhitelistIn, enchantmentsBlacklistIn);
            if (!TYPES.containsKey(name)) TYPES.put(name, type);
            return type;
        }

        public static ItemLapisRune.RuneType get(String name) { return TYPES.get(name); }
        public static Collection<RuneType> getAll() { return TYPES.values(); }


        private final String name;
        private final Item mimicItem;
        private final float levelRequirementMult;
        private final float costMult;
        private final Set<Enchantment> enchantmentsWhitelist;
        private final Set<Enchantment> enchantmentsBlacklist;

        private RuneType(String name, Item mimicItemIn, float levelRequirementMultIn, float costMultIn, Set<Enchantment> enchantmentsWhitelistIn, Set<Enchantment> enchantmentsBlacklistIn)
        {
            this.name = name;
            this.mimicItem = mimicItemIn;
            this.levelRequirementMult = levelRequirementMultIn;
            this.costMult = costMultIn;
            this.enchantmentsWhitelist = enchantmentsWhitelistIn;
            this.enchantmentsBlacklist = enchantmentsBlacklistIn;
        }

        public String getName() { return this.name; }
        public Item getMimicItem() { return this.mimicItem; }
        public float getLevelRequirementMult() { return this.levelRequirementMult; }
        public float getCostMult() { return this.costMult; }
        public Set<Enchantment> getEnchantmentsWhitelist() { return this.enchantmentsWhitelist; }
        public Set<Enchantment> getEnchantmentsBlacklist() { return this.enchantmentsBlacklist; }
    }
}