package com.sirsquidly.enchanter_tools.common.blocks;

import com.sirsquidly.enchanter_tools.client.particle.enchanterToolsParticles;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileArcaneBrazier;
import com.sirsquidly.enchanter_tools.config.Config;
import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.config.ConfigParser;
import com.sirsquidly.enchanter_tools.enchanterTools;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsSounds;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class BlockArcaneBrazier extends Block implements ITileEntityProvider
{
    /** 0 = Unlit, 1 = Normal Fire, 2 = Arcane Fire */
    public static final PropertyInteger FLAME = PropertyInteger.create("flame", 0, 2);
    protected static final AxisAlignedBB BRAZIER_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.4375D, 0.75D);

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return BRAZIER_AABB; }

    public BlockArcaneBrazier()
    {
        super(Material.ROCK, MapColor.GRAY);
        setSoundType(SoundType.METAL);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileArcaneBrazier(); }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    { return this.getDefaultState().withProperty(FLAME, 1); }


    /** This handles the Enchantment Saving, and Enchantment Removing. */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileArcaneBrazier)
        {
            TileArcaneBrazier brazier = (TileArcaneBrazier) tile;
            ItemStack heldStack = player.getHeldItem(hand);

            //Lighting the Brazier or reverting the flame to normal
            if(heldStack.getItem() instanceof ItemFlintAndSteel)
            {
                return onFlintAndSteelInteraction(world, pos, state, brazier, player, heldStack);
            }

            //Burning an enchanted book
            if(!heldStack.isEmpty() && heldStack.getItem() instanceof ItemEnchantedBook)
            {
                return onEnchantedBookInteraction(world, pos, state, brazier, heldStack);
            }

            //Burning the enchantments off an item
            if(!player.isSneaking() && !heldStack.isEmpty() && heldStack.isItemEnchanted() && brazier.getSavedEnchantment() != null)
            {
                //Denying blacklisted items
                if(!ConfigParser.isStackInList(heldStack, ConfigCache.brazierBurnItemBlacklist))
                    return onEnchantedItemInteraction(world, pos, player, heldStack, brazier.getSavedEnchantment());
            }

            //Extinguishing the Brazier either with a shovel or sneak + right-clicking with an empty hand
            if((heldStack.isEmpty() && player.isSneaking()) || isShovel(heldStack))
            {
                return onExtinguishInteraction(world, pos, state, brazier, player, heldStack);
            }
        }
        return false;
    }

    public boolean onFlintAndSteelInteraction(World world, BlockPos pos, IBlockState state, TileArcaneBrazier brazier, EntityPlayer player, ItemStack stack)
    {
        world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
        stack.damageItem(1, player);

        if(state.getValue(FLAME) == 0 && brazier.getSavedEnchantment() != null)
        {
            //Interacting with an unlit brazier with a saved enchantment relights the purple flame
            world.setBlockState(pos, state.withProperty(FLAME, 2));
        }
        else
        {
            //Interacting with a lit Arcane Brazier with Flint and Steel resets the flame to normal and removes the saved enchantment
            world.setBlockState(pos, state.withProperty(FLAME, 1));
            brazier.setEnchantment(null);
        }
        return true;
    }

    public boolean onExtinguishInteraction(World world, BlockPos pos, IBlockState state, TileArcaneBrazier brazier, EntityPlayer player, ItemStack stack)
    {
        if (state.getValue(FLAME) != 0) {
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, world.rand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, state.withProperty(FLAME, 0));
            return true;
        }
        return false;
    }

    public boolean onEnchantedBookInteraction(World world, BlockPos pos, IBlockState state, TileArcaneBrazier brazier, ItemStack stack)
    {
        int flame = state.getValue(FLAME);
        if (flame == 0) return false;

        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (enchants.isEmpty()) return false;

        LinkedHashMap<Enchantment, Integer> newMap = new LinkedHashMap<>(enchants);
        Enchantment firstAllowedEnchant = null;

        for (Enchantment ench : newMap.keySet())
        {
            ResourceLocation enchId = ench.getRegistryName();

            if (enchId == null || ConfigCache.brazierBurnEnchantBlacklist.contains(enchId.toString())) continue;
            firstAllowedEnchant = ench;
        }

        if (firstAllowedEnchant != null) {
            if (flame == 1) world.setBlockState(pos, state.withProperty(FLAME, 2));

            stack.shrink(1);
            brazier.setEnchantment(firstAllowedEnchant);
            world.playSound(null, pos, EnchanterToolsSounds.BLOCK_ARCANE_BRAZIER_STRIP_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            preformEnchantmentBurnEffects(world, pos);
            enchanterTools.LOGGER.debug("Updated saved enchantment:  {}", brazier.getSavedEnchantment());
            return true;
        }
        return false;
    }

    public boolean onEnchantedItemInteraction(World world, BlockPos pos, EntityPlayer player, ItemStack stack, Enchantment savedEnchant)
    {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (enchants.containsKey(savedEnchant))
        {
            if (!world.isRemote) {
                enchanterTools.LOGGER.debug(">> Enchantment found: {}", savedEnchant);
                enchants.remove(savedEnchant);

                /* This is written this way in-case some mod adds an enchantable stackable item. */
                ItemStack output = stack.splitStack(1);
                output.damageItem((int) (output.getMaxDamage() * Config.block.arcaneBrazier.durabilityCost), player);
                EnchantmentHelper.setEnchantments(enchants, output);
                ItemHandlerHelper.giveItemToPlayer(player, output, player.inventory.currentItem);
                world.playSound(null, pos, EnchanterToolsSounds.BLOCK_ARCANE_BRAZIER_STRIP_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
                preformEnchantmentBurnEffects(world, pos);
            }
            return true;
        }
        return false;
    }

    public boolean isShovel(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof ItemSpade || stack.getItem().getToolClasses(stack).contains("shovel"));
    }

    public static void preformEnchantmentBurnEffects(World world, BlockPos pos)
    {
        for (int j = 0; j < 10; j++)
        {
            double thisX = pos.getX() + 0.5 + (world.rand.nextDouble() * 0.3) - 0.15;
            double thisY = pos.getY() + 0.3 + (world.rand.nextDouble() * 0.4) - 0.2;
            double thisZ = pos.getZ() + 0.5 + (world.rand.nextDouble() * 0.3) - 0.15;

            enchanterTools.proxy.spawnParticle(enchanterToolsParticles.BRAZIER_FLAME_FADE, world, thisX, thisY, thisZ, 0, world.rand.nextDouble() * 0.6, 0, 0);

            enchanterTools.proxy.spawnParticle(enchanterToolsParticles.BRAZIER_RUNE_BURN, world, thisX + ((world.rand.nextDouble() * 0.3) - 0.15), thisY + (world.rand.nextDouble() * 0.4), thisZ + ((world.rand.nextDouble() * 0.3) - 0.15), thisX, thisY, thisZ, 0);
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!Config.block.arcaneBrazier.collisionDamage) return;
        if (worldIn.isRemote) return;
        if (worldIn.getTotalWorldTime() % 20L != 0) return;
        if (entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isCreative()) return;

        int flame = state.getValue(FLAME);
        if(flame == 0) return;

        worldIn.playSound(null, pos, SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.BLOCKS, 0.5F, (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.2F + 1.0F);
        //Deal fire damage for normal flame, magic damage for arcane flame
        entityIn.attackEntityFrom(flame == 1 ? DamageSource.IN_FIRE : DamageSource.MAGIC, 1.0f);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        int flame = stateIn.getValue(FLAME);
        if (flame == 0) return;

        double thisX = pos.getX() + 0.5 + (worldIn.rand.nextDouble() * 0.4) - 0.2;
        double thisY = pos.getY() + 0.4 + (worldIn.rand.nextDouble() * 0.4) - 0.2;
        double thisZ = pos.getZ() + 0.5 + (worldIn.rand.nextDouble() * 0.4) - 0.2;

        double motionX = 0;
        double motionY = 0;
        double motionZ = 0;

        enchanterTools.proxy.spawnParticle(enchanterToolsParticles.BRAZIER_SMOKE, worldIn, thisX, thisY, thisZ, motionX, motionY, motionZ);

        if (rand.nextBoolean())
        {
            enchanterTools.proxy.spawnParticle(enchanterToolsParticles.BRAZIER_EMBER, worldIn, thisX, thisY, thisZ, motionX, worldIn.rand.nextDouble() * 0.4, motionZ, flame == 1 ? 1 : 0);
        }
    }

    /** Ha ha, REDUCE Enchanting Power! The Brazier burns it all! */
    public float getEnchantPowerBonus(World world, BlockPos pos)
    { return world.getBlockState(pos).getValue(FLAME) == 2 ? -3 : 0; }

    public boolean isOpaqueCube(IBlockState state) { return false; }

    public boolean isFullCube(IBlockState state) { return false; }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    { return BlockFaceShape.UNDEFINED; }

    @Override
    public IBlockState getStateFromMeta(int meta) { return this.getDefaultState().withProperty(FLAME, meta); }
    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(FLAME); }
    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FLAME); }
}