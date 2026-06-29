package com.sirsquidly.enchanter_tools.common.items;

import com.sirsquidly.enchanter_tools.common.CommonEvents;
import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.config.ConfigParser;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemExtractingBook extends Item implements IAnvilSpecialBehavior
{
    @Override
    public void onAnvilUpdate(AnvilUpdateEvent event, ItemStack left, ItemStack right)
    {
        if (left.getItem() == Items.ENCHANTED_BOOK) return;
        /* Early exit if the config states the Extracting Book should leave this item alone. */
        if (ConfigParser.isStackInList(left, ConfigCache.extractBookExtractItemBlacklist)) return;

        Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.getEnchantments(left);
        if (leftEnchants.isEmpty()) return;

        Enchantment selected = null;
        int level = 0;

        for (Map.Entry<Enchantment, Integer> entry : leftEnchants.entrySet())
        {
            Enchantment ench = entry.getKey();
            ResourceLocation enchId = ench.getRegistryName();

            if (enchId == null || ConfigCache.extractBookExtractEnchantBlacklist.contains(enchId.toString())) continue;

            selected = ench;
            level = entry.getValue();
            break;
        }

        /* If found nothing, then just exit. */
        if (selected == null) return;

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantedBook.addEnchantment(book, new EnchantmentData(selected, level));

        leftEnchants.forEach((ench, lvl) ->
        {
            if (ench.isCurse())
            { ItemEnchantedBook.addEnchantment(book, new EnchantmentData(ench, lvl)); }
        });

        event.setOutput(book);
        event.setCost(((10 / selected.getRarity().getWeight()) + 3) * level);
        event.setMaterialCost(1);
    }

    @Nullable
    @Override
    public CommonEvents.AnvilRefresherStorage getAnvilRepairOutput(AnvilRepairEvent event, ContainerRepair container, EntityPlayer player, ItemStack left, ItemStack right)
    {
        /* Early exit if the config states the Extracting Book should leave this item alone. */
        if (ConfigParser.isStackInList(left, ConfigCache.extractBookExtractItemBlacklist)) return null;


        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(left);
        if (enchants.isEmpty()) return null;

        LinkedHashMap<Enchantment, Integer> newMap = new LinkedHashMap<>(enchants);
        Enchantment removedEnchant = null;

        for (Enchantment ench : newMap.keySet())
        {
            ResourceLocation enchId = ench.getRegistryName();

            if (enchId == null || ConfigCache.extractBookExtractEnchantBlacklist.contains(enchId.toString())) continue;

            removedEnchant = ench;
            break;
        }
        if (removedEnchant == null) return null;

        newMap.remove(removedEnchant);
        ItemStack modified = left.copy();
        EnchantmentHelper.setEnchantments(newMap, modified);

        return new CommonEvents.AnvilRefresherStorage(container, modified, ItemStack.EMPTY);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.anvil_ingredient.label"));
        tooltip.add(TextFormatting.GRAY + I18n.format(""));
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.anvil_ingredient.applies"));
        tooltip.add(TextFormatting.BLUE + I18n.format("description.enchanter_tools.anvil_ingredient.enchanted_items"));
    }
}