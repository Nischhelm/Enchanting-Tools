package com.sirsquidly.enchanter_tools.common.blocks;

import com.sirsquidly.enchanter_tools.client.particle.enchanterToolsParticles;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileArcaneBrazier;
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
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
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
        super(Material.GROUND, MapColor.GRAY);
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
        if (hand != EnumHand.MAIN_HAND) return true;

        player.swingArm(hand);
        TileEntity te = world.getTileEntity(pos);

        if (world.isRemote) return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);

        if (te instanceof TileArcaneBrazier)
        {
            TileArcaneBrazier brazier = ((TileArcaneBrazier) te);
            ItemStack held = player.getHeldItemMainhand();
            if (held.isEmpty()) super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);

            int flameState = state.getValue(FLAME);

            if (flameState == 0)
            {
                if (held.getItem() instanceof ItemFlintAndSteel)
                {
                    world.setBlockState(pos, state.withProperty(FLAME, 1));
                    return true;
                }
            }
            else
            {
                /* Early exit if the config states the Brazier should leave this item alone. */
                if (ConfigParser.isStackInList(held, ConfigCache.brazierBurnItemBlacklist)) return false;

                if (held.getItem() == Items.ENCHANTED_BOOK)
                {
                    Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(held);
                    if (enchants.isEmpty()) return true;

                    LinkedHashMap<Enchantment, Integer> newMap = new LinkedHashMap<>(enchants);
                    Enchantment firstAllowedEnchant = null;

                    for (Enchantment ench : newMap.keySet())
                    {
                        ResourceLocation enchId = ench.getRegistryName();

                        if (enchId == null || ConfigCache.brazierBurnEnchantBlacklist.contains(enchId.toString())) continue;
                        firstAllowedEnchant = ench;
                    }
                    if (firstAllowedEnchant == null) return true;
                    brazier.setEnchantment(firstAllowedEnchant);

                    held.shrink(1);

                    if (state.getValue(FLAME) == 1) world.setBlockState(pos, state.withProperty(FLAME, 2));
                    preformEnchantmentBurnEffects(world, pos);

                    brazier.markDirty();
                    world.notifyBlockUpdate(pos, state, state, 3);
                    world.playSound(null, pos, EnchanterToolsSounds.BLOCK_ARCANE_BRAZIER_STRIP_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    System.out.println("Updated saved enchantment:  " + brazier.getSavedEnchantment());
                    return true;
                }
                /* Enchantment Removal */
                else if (brazier.getSavedEnchantment() != null)
                {
                    System.out.println("NOT NULL, CONTINUE ITEM CHECK");
                    Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(held);
                    if (enchants.containsKey(brazier.getSavedEnchantment()))
                    {
                        System.out.println(">> Enchantment found: " + brazier.getSavedEnchantment());
                        enchants.remove(brazier.getSavedEnchantment());

                        /* This is written this way in-case some mod adds an enchantable stackable item. */
                        ItemStack output = held.copy();
                        output.setCount(1);
                        output.damageItem((int) (output.getMaxDamage() * ConfigCache.brazierDurabilityCost),player);
                        EnchantmentHelper.setEnchantments(enchants, output);
                        held.shrink(1);
                        if (!player.inventory.addItemStackToInventory(output)) player.dropItem(output, false);

                        world.playSound(null, pos, EnchanterToolsSounds.BLOCK_ARCANE_BRAZIER_STRIP_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        preformEnchantmentBurnEffects(world, pos);

                        return true;
                    }
                }

                if (held.getItem() instanceof ItemSpade)
                {
                    world.setBlockState(pos, state.withProperty(FLAME, 0));
                    brazier.setEnchantment(null);
                    return true;
                }
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
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


    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        if (true)
        {
            super.onEntityWalk(worldIn, pos, entityIn);
            return;
        }

        if (entityIn instanceof EntityLivingBase && worldIn.getWorldTime() % 20 == 0)
        {
            worldIn.playSound(null, pos, SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.BLOCKS, 0.5F, (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.2F + 1.0F);
            entityIn.attackEntityFrom(DamageSource.MAGIC, 1.0F);
        }

        super.onEntityWalk(worldIn, pos, entityIn);
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