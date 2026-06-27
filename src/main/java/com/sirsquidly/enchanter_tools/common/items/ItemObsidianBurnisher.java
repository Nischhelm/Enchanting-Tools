package com.sirsquidly.enchanter_tools.common.items;

import com.sirsquidly.enchanter_tools.config.ConfigCache;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemObsidianBurnisher extends Item implements IAnvilSpecialBehavior
{
    public ItemObsidianBurnisher()
    {
        this.maxStackSize = 1;
    }

    @Override
    public void onAnvilUpdate(AnvilUpdateEvent event, ItemStack left, ItemStack right)
    {
        int leftRepairCost = left.getRepairCost();

        if (leftRepairCost <= 0) return;
        if (left.getMaxDamage() - left.getItemDamage() < (int)(left.getMaxDamage() * ConfigCache.burnisherDurabilityCost)) return;
        ItemStack newLeft = left.copy();

        /* Damages the item after repairing it. */
        if (left.isItemStackDamageable()) newLeft.setItemDamage((int) Math.max(0, left.getItemDamage() + (left.getMaxDamage() * ConfigCache.burnisherDurabilityCost)));

        /* This is used for lowering Repair Cost using an inverse of the formula used to increase it. */
        if (ConfigCache.burnisherRepairCost < 0)
        {
            for (int i = 0; i < -ConfigCache.burnisherRepairCost && leftRepairCost > 0; i++)
            {
                leftRepairCost = (leftRepairCost - 1) / 2;
            }
        }

        newLeft.setRepairCost(leftRepairCost);
        event.setOutput(newLeft);
        event.setMaterialCost(1);
    }

    @Nullable
    @Override
    public ItemStack getAnvilRepairOutput(AnvilRepairEvent event, ContainerRepair container, EntityPlayer player, ItemStack left, ItemStack right) { return null; }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.anvil_ingredient.label"));
        tooltip.add(TextFormatting.GRAY + I18n.format(""));
        tooltip.add(TextFormatting.GRAY + I18n.format("description.enchanter_tools.anvil_ingredient.effect"));
        tooltip.add(TextFormatting.BLUE + I18n.format("description.enchanter_tools.obsidian_burnisher.stats", ConfigCache.burnisherRepairCost));
    }
}