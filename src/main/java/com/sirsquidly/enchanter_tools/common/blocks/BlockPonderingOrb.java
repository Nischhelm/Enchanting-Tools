package com.sirsquidly.enchanter_tools.common.blocks;

import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TilePonderingOrb;
import com.sirsquidly.enchanter_tools.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockPonderingOrb extends Block implements ITileEntityProvider
{
    protected static final AxisAlignedBB ORB_AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.6875D, 0.8125D);

    public BlockPonderingOrb()
    {
        super(Material.ROCK, MapColor.BLUE);
        setSoundType(SoundType.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { return new TilePonderingOrb(); }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return ORB_AABB; }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TilePonderingOrb && hand == EnumHand.MAIN_HAND)
        {
            TilePonderingOrb orb = (TilePonderingOrb) tile;
            long time = world.getTotalWorldTime();
            int xpCost = Config.block.ponderingOrb.orbRerollXPCost;
            if (time <= orb.lastInteractTime)
            {
                sendPlayerMessage(player, "warn_cooldown");
                return false;
            }
            else if (player.experienceLevel < xpCost && !player.isCreative())
            {
                sendPlayerMessage(player, "warn_cost");
                return false;
            }

            if(world.isRemote)
                world.playSound(player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.4F, world.rand.nextFloat() * 0.4F + 0.8F);
            player.onEnchant(ItemStack.EMPTY, !player.isCreative() ? xpCost : 0);
            orb.lastInteractTime = time + Config.block.ponderingOrb.orbRerollTickCooldown;
            sendPlayerMessage(player, "reroll");
            return true;
        }
        return false;
    }

    public void sendPlayerMessage(EntityPlayer player, String messageType)
    {
        if(player.world.isRemote) return;
        player.sendStatusMessage(new TextComponentTranslation("message.enchanter_tools.pondering_orb." + messageType), true);
    }

    public boolean isOpaqueCube(IBlockState state) { return false; }

    public boolean isFullCube(IBlockState state) { return false; }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.TRANSLUCENT; }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    { return BlockFaceShape.UNDEFINED; }
}