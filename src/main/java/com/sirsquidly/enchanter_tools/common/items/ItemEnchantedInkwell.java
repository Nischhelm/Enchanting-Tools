package com.sirsquidly.enchanter_tools.common.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemEnchantedInkwell extends Item implements IAnvilSpecialBehavior
{
    public ItemEnchantedInkwell()
    {
        this.maxStackSize = 1;
        this.setMaxDamage(11);
    }

    public boolean isEnchantable(ItemStack stack) { return !stack.isItemEnchanted(); }

    /* Accepts all enchantments ever. */
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    { return !stack.isItemEnchanted(); }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    { return true; }

    public int getItemEnchantability() { return 1; }

    public EnumRarity getRarity(ItemStack stack) { return EnumRarity.EPIC; }

    @Override
    public void onAnvilUpdate(AnvilUpdateEvent event, ItemStack left, ItemStack right)
    {
        if (left.getItem() != Items.BOOK && left.getItem() != Items.ENCHANTED_BOOK) return;

        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(right);
        if (enchants.isEmpty()) return;

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        enchants.forEach((ench, lvl) -> ItemEnchantedBook.addEnchantment(book, new EnchantmentData(ench, lvl)));

        event.setOutput(book);
        event.setCost(Math.max(1, enchants.size() * 2));
        event.setMaterialCost(1);
    }

    @Nullable
    @Override
    public ItemStack getAnvilRepairOutput(AnvilRepairEvent event, ContainerRepair container, EntityPlayer player, ItemStack left, ItemStack right)
    {
        ItemStack inkwell = right.copy();
        int damage = EnchantmentHelper.getEnchantments(right).size();
        inkwell.damageItem(damage, player);

        return inkwell;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.anvil_ingredient.label"));
        tooltip.add(TextFormatting.GRAY + I18n.format(""));
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.anvil_ingredient.applies"));
        tooltip.add(TextFormatting.BLUE + I18n.format("description.enchanter_tools.anvil_ingredient.books"));
        tooltip.add(TextFormatting.GRAY + I18n.format(""));
    }
}