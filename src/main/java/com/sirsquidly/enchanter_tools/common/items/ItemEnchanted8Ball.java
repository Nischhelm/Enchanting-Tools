package com.sirsquidly.enchanter_tools.common.items;

import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemEnchanted8Ball extends Item
{
    public ItemEnchanted8Ball()
    {
        this.maxStackSize = 1;
        this.setMaxDamage(1);

        this.addPropertyOverride(new ResourceLocation("damage"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            { return stack.getItemDamage(); }
        });
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.getItemDamage() > 0)
        { return I18n.translateToLocal("item.enchanter_tools.damaged_eight_ball.name").trim(); }
        if (!stack.isItemEnchanted())
        { return I18n.translateToLocal("item.enchanter_tools.eight_ball.name").trim(); }

        return super.getItemStackDisplayName(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() > 0 || !stack.isItemEnchanted() && ConfigCache.eightBallFortunesEnchantExclusive) return new ActionResult<>(EnumActionResult.PASS, stack);

        player.setActiveHand(hand);
        player.swingArm(hand);
        player.sendStatusMessage(new TextComponentTranslation("message.enchanter_tools.enchanted_eight_ball.use" + world.rand.nextInt(20)), true);
        player.getCooldownTracker().setCooldown(stack.getItem(), 3);
        world.playSound(null, player.getPosition(), EnchanterToolsSounds.ITEM_ENCHANTED_EIGHT_BALL_USE, SoundCategory.BLOCKS, 0.5F, 1.0F);


        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }


    public int getItemEnchantability() { return 1; }

    public boolean isEnchantable(ItemStack stack) { return false; }

    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    { return true; }

    public EnumRarity getRarity(ItemStack stack) { return stack.isItemEnchanted() ? EnumRarity.UNCOMMON : super.getRarity(stack); }
}