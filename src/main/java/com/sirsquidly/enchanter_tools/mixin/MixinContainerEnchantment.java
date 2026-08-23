package com.sirsquidly.enchanter_tools.mixin;

import com.sirsquidly.enchanter_tools.common.RuneEnchantmentLogic;
import com.sirsquidly.enchanter_tools.common.items.ItemLapisRune;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ContainerEnchantment.class)
public abstract class MixinContainerEnchantment
{
    @Shadow @Final private World world;
    @Shadow @Final private BlockPos position;

    @Shadow public int[] enchantClue;
    @Shadow public int[] worldClue;
    @Shadow public int[] enchantLevels;

    @Shadow @Final private Random rand;
    @Shadow public int xpSeed;

    @Shadow
    @Final private IInventory tableInventory;

    private List<EnchantmentData>[] cachedRunePools = new List[3];
    private ItemLapisRune.RuneType storedRune;

    @Inject(method = "onCraftMatrixChanged", at = @At("RETURN"))
    private void enchantertools$fixPreview(IInventory inv, CallbackInfo ci)
    {
        ItemStack item = this.tableInventory.getStackInSlot(0);
        ItemStack runeStack = this.tableInventory.getStackInSlot(1);

        /* Flush reset values! */
        if (item.isEmpty() || !(runeStack.getItem() instanceof ItemLapisRune))
        {
            this.storedRune = null;
            Arrays.fill(this.cachedRunePools, null);
            return;
        }

        ItemLapisRune.RuneType rune = ((ItemLapisRune) runeStack.getItem()).getRuneType();
        this.storedRune = rune;

        for (int i = 0; i < 3; i++)
        {
            if (this.enchantLevels[i] <= 0)
            {
                this.enchantClue[i] = -1;
                this.worldClue[i] = -1;
                continue;
            }
            this.rand.setSeed(this.xpSeed + i);

            Set<ResourceLocation> blocked = RuneEnchantmentLogic.getBrazierBlockedEnchants(this.world, this.position);
            List<EnchantmentData> list = RuneEnchantmentLogic.buildRunePool(rune, item, this.enchantLevels[i], false, this.rand, blocked);
            this.cachedRunePools[i] = list;

            this.enchantLevels[i] = Math.max(1, Math.round(this.enchantLevels[i] * rune.getLevelRequirementMult()));

            if (list.isEmpty())
            {
                this.enchantClue[i] = -1;
                this.worldClue[i] = -1;
                continue;
            }

            EnchantmentData chosen = list.get(this.rand.nextInt(list.size()));

            this.enchantClue[i] = Enchantment.getEnchantmentID(chosen.enchantment);
            this.worldClue[i] = chosen.enchantmentLevel;
        }
    }

    @Inject( method = "enchantItem(Lnet/minecraft/entity/player/EntityPlayer;I)Z", at = @At("HEAD"), cancellable = true)
    private void enchantertools$overrideEnchantItem(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir)
    {
        int i = id + 1;
        ItemStack mainSlot = this.tableInventory.getStackInSlot(0);
        ItemStack lapisSlot = this.tableInventory.getStackInSlot(1);
        if (mainSlot.isEmpty()) return;

        List<EnchantmentData> pool = this.cachedRunePools[id];

        if (pool == null || pool.isEmpty()) return;

        boolean enchantingOnBooksCrazyStyle = mainSlot.getItem() instanceof ItemBook;
        ItemStack result = enchantingOnBooksCrazyStyle ? new ItemStack(Items.ENCHANTED_BOOK) : mainSlot.copy();
        player.onEnchant(result, (int) (i * this.storedRune.getCostMult()));

        for (EnchantmentData data : pool)
        {
            if (enchantingOnBooksCrazyStyle) ItemEnchantedBook.addEnchantment(result, data);
            else result.addEnchantment(data.enchantment, data.enchantmentLevel);
        }

        this.tableInventory.setInventorySlotContents(0, result);

        if (!player.capabilities.isCreativeMode)
        {
            lapisSlot.shrink(i);
            if (lapisSlot.isEmpty()) this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
        }

        player.addStat(StatList.ITEM_ENCHANTED);

        if (player instanceof EntityPlayerMP)
        { CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP)player, result, i); }

        this.tableInventory.markDirty();
        this.xpSeed = player.getXPSeed();
        ((ContainerEnchantment) (Object) this).onCraftMatrixChanged(this.tableInventory);
        this.world.playSound(null, this.position, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F);

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(method = "getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void enchantertools$filterEnchantments(ItemStack stack, int enchantSlot, int level, CallbackInfoReturnable<List<EnchantmentData>> cir)
    {
        List<EnchantmentData> list = cir.getReturnValue();
        if (list == null || list.isEmpty()) return;

        Set<ResourceLocation> blocked = RuneEnchantmentLogic.getBrazierBlockedEnchants(this.world, this.position);
        list.removeIf(e -> blocked.contains(e.enchantment.getRegistryName()));

        if (!list.isEmpty()) return;
        /* If a slot got emptied, then re-roll it! */

        for (int attempt = 1; attempt <= 16; attempt++)
        {
            this.rand.setSeed((this.xpSeed + enchantSlot + attempt * 31));

            List<EnchantmentData> reroll = EnchantmentHelper.buildEnchantmentList(this.rand, stack, level, false);
            if (reroll == null || reroll.isEmpty()) continue;

            reroll.removeIf(e -> blocked.contains(e.enchantment.getRegistryName()));

            if (!reroll.isEmpty())
            {
                if (stack.getItem() == Items.BOOK && reroll.size() > 1) reroll.remove(this.rand.nextInt(reroll.size()));

                cir.setReturnValue(reroll);
                return;
            }
        }
    }

    @Inject( method = "transferStackInSlot", at = @At("HEAD"), cancellable = true)
    private void enchantertools$allowSlot2(EntityPlayer player, int index, CallbackInfoReturnable<ItemStack> cir)
    {
        if (index < 2)  return;
        ContainerEnchantment container = (ContainerEnchantment)(Object)this;
        Slot slot = container.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return;

        ItemStack stack = slot.getStack();

        if (!(stack.getItem() instanceof ItemLapisRune)) return;
        ItemStack original = stack.copy();
        Slot runeSlot = container.inventorySlots.get(1);

        if (!runeSlot.isItemValid(stack)) return;
        if (!runeSlot.getHasStack())
        {
            runeSlot.putStack(stack.copy());
            stack.setCount(0);
        }
        else
        {
            ItemStack existing = runeSlot.getStack();
            if (!ItemStack.areItemsEqual(existing, stack) || !ItemStack.areItemStackTagsEqual(existing, stack)) return;
            int amount = Math.min(stack.getCount(), runeSlot.getSlotStackLimit() - existing.getCount());
            if (amount <= 0) return;

            existing.grow(amount);
            stack.shrink(amount);
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        runeSlot.onSlotChanged();
        slot.onTake(player, stack);
        cir.setReturnValue(original);
    }
}