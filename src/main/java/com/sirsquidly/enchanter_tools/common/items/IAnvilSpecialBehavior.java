package com.sirsquidly.enchanter_tools.common.items;

import com.sirsquidly.enchanter_tools.common.CommonEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;

import javax.annotation.Nullable;

public interface IAnvilSpecialBehavior
{
    void onAnvilUpdate(AnvilUpdateEvent event, ItemStack left, ItemStack right);

    @Nullable
    CommonEvents.AnvilRefresherStorage getAnvilRepairOutput(AnvilRepairEvent event, ContainerRepair container, EntityPlayer player, ItemStack left, ItemStack right);
}