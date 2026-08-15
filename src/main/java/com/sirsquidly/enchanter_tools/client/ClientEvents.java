package com.sirsquidly.enchanter_tools.client;

import com.sirsquidly.enchanter_tools.common.blocks.BlockChiseledBookshelf;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileArcaneBrazier;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileChiseledBookshelf;
import com.sirsquidly.enchanter_tools.config.Config;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEvents
{
    private static final String MARKER = "\\u00A70\\u00A70";

    @SubscribeEvent
    public static void onRenderOverlayPost(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        Vec3d eyePosition = player.getPositionEyes(1.0F);
        Vec3d lookVector = player.getLook(1.0F);
        double playerReach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);
        RayTraceResult rtresult = mc.player.getEntityWorld().rayTraceBlocks(eyePosition, traceEnd, false, true, false);
        if (rtresult == null || rtresult.typeOfHit != RayTraceResult.Type.BLOCK) return;

        TileEntity tile = mc.world.getTileEntity(rtresult.getBlockPos());

        if (tile instanceof TileChiseledBookshelf)
        {
            renderBookshelfInfo(mc, rtresult, (TileChiseledBookshelf) tile);
        } else if (tile instanceof TileArcaneBrazier) {
            renderBrazierInfo(mc, rtresult, (TileArcaneBrazier) tile);
        }
    }

    private static void renderBookshelfInfo(Minecraft mc, RayTraceResult rtresult, TileChiseledBookshelf bookshelf)
    {
        IBlockState state = mc.world.getBlockState(rtresult.getBlockPos());
        EnumFacing facing = state.getValue(BlockChiseledBookshelf.FACING);
        if (rtresult.sideHit != facing) return;

        Vec3d hitVec = rtresult.hitVec.subtract(rtresult.getBlockPos().getX(), rtresult.getBlockPos().getY(), rtresult.getBlockPos().getZ());
        int slot = TileChiseledBookshelf.getSlotFromHit(facing, (float)hitVec.x, (float)hitVec.y, (float)hitVec.z);
        ItemStack stack = bookshelf.inventory.getStackInSlot(slot);
        if (stack.isEmpty()) return;

        ScaledResolution res = new ScaledResolution(mc);
        int maxWidth = res.getScaledWidth() - 20;
        int centerX = res.getScaledWidth() / 2;
        int centerY = res.getScaledHeight() / 2 + 14;

        List<String> lines = stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL);

        int yOffset = 0;

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            TextFormatting textColor = i == 0 ? stack.getItem().getForgeRarity(stack).getColor() : TextFormatting.GRAY;
            List<String> wrapped = mc.fontRenderer.listFormattedStringToWidth( textColor + line, maxWidth);

            for (String wrappedLine : wrapped)
            {
                int width = mc.fontRenderer.getStringWidth(wrappedLine);
                mc.fontRenderer.drawStringWithShadow( wrappedLine, centerX - width / 2f, centerY + yOffset, -1);
                yOffset += 10;
            }
        }
    }

    private static void renderBrazierInfo(Minecraft mc, RayTraceResult rtresult, TileArcaneBrazier brazier)
    {
        Enchantment enchantment = brazier.getSavedEnchantment();
        if (enchantment == null) return;

        ScaledResolution res = new ScaledResolution(mc);
        int maxWidth = res.getScaledWidth() - 20;
        int centerX = res.getScaledWidth() / 2;
        int centerY = res.getScaledHeight() / 2 + 14;

        int yOffset = 0;

        String name = I18n.format(enchantment.getName());
        List<String> wrapped = mc.fontRenderer.listFormattedStringToWidth( TextFormatting.DARK_PURPLE + name, maxWidth);

        for (String wrappedLine : wrapped)
        {
            int width = mc.fontRenderer.getStringWidth(wrappedLine);
            mc.fontRenderer.drawStringWithShadow( wrappedLine, centerX - width / 2f, centerY + yOffset, -1);
            yOffset += 10;
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        if (!shouldRenderBurnisherTooltip(player, stack)) return;

        List<String> tooltip = event.getToolTip();
        int repairCost = stack.getRepairCost();

        String line = I18n.format("description.enchanter_tools.item.obsidian_burnisher_anvil", getAnvilUses(repairCost));

        if (tooltip.isEmpty()) tooltip.add(TextFormatting.BLUE + line);
        else tooltip.add(1, TextFormatting.BLUE + line);
    }

    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent.PostText event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack stack = event.getStack();
        if (!shouldRenderBurnisherTooltip(player, stack)) return;

        List<String> lines = event.getLines();
        if (lines.isEmpty()) return;

        String expected = I18n.format( "description.enchanter_tools.item.obsidian_burnisher_anvil", getAnvilUses(stack.getRepairCost()));

        int index = -1;

        for (int i = 0; i < lines.size(); i++)
        {
            String stripped = TextFormatting.getTextWithoutFormattingCodes(lines.get(i));
            if (expected.equals(stripped))
            {
                index = i;
                break;
            }
        }

        if (index == -1) return;

        int x = event.getX();
        int y = event.getY();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x - 2, y + index * 10 - 2, 0);
        GlStateManager.scale(0.9, 0.9, 1.0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemIntoGUI( new ItemStack(EnchanterToolsItems.OBSIDIAN_BURNISHER), 0, 0);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private static boolean shouldRenderBurnisherTooltip(EntityPlayer player, ItemStack stack)
    {
        if (player == null) return false;
        if (stack.isEmpty()) return false;
        if (stack.getRepairCost() <= 0) return false;

        switch (Config.item.obsidianBurnisher.tooltipVisibility)
        {
            case ALWAYS:
                return hasBurnisher(player);
            case SHIFT:
                return GuiScreen.isShiftKeyDown() && hasBurnisher(player);
            case ANVIL:
                return player.openContainer instanceof ContainerRepair && hasBurnisher(player);
            case DISABLED:
            default:
                return false;
        }
    }

    private static boolean hasBurnisher(EntityPlayer player)
    {
        for (ItemStack invStack : player.inventory.mainInventory)
        {
            if (!invStack.isEmpty() && invStack.getItem() == EnchanterToolsItems.OBSIDIAN_BURNISHER) { return true; }
        }
        return false;
    }

    private static int getAnvilUses(int repairCost)
    {
        int uses = 0;
        int value = repairCost;

        while (value > 0)
        {
            value = (value - 1) / 2;
            uses++;
        }

        return uses;
    }
}