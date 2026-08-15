package com.sirsquidly.enchanter_tools.common.blocks;

import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileChiseledBookshelf;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsSounds;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockChiseledBookshelf extends BlockContainer
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool BOOK_0 = PropertyBool.create("book0");
    public static final PropertyBool BOOK_1 = PropertyBool.create("book1");
    public static final PropertyBool BOOK_2 = PropertyBool.create("book2");
    public static final PropertyBool BOOK_3 = PropertyBool.create("book3");
    public static final PropertyBool BOOK_4 = PropertyBool.create("book4");
    public static final PropertyBool BOOK_5 = PropertyBool.create("book5");


    public BlockChiseledBookshelf()
    {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setSoundType(EnchanterToolsSounds.CHISELED_BOOKSHELF);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileChiseledBookshelf(); }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileChiseledBookshelf)) return state;
        TileChiseledBookshelf shelf = (TileChiseledBookshelf) tile;

        return state
                .withProperty(BOOK_0, !shelf.inventory.getStackInSlot(0).isEmpty())
                .withProperty(BOOK_1, !shelf.inventory.getStackInSlot(1).isEmpty())
                .withProperty(BOOK_2, !shelf.inventory.getStackInSlot(2).isEmpty())
                .withProperty(BOOK_3, !shelf.inventory.getStackInSlot(3).isEmpty())
                .withProperty(BOOK_4, !shelf.inventory.getStackInSlot(4).isEmpty())
                .withProperty(BOOK_5, !shelf.inventory.getStackInSlot(5).isEmpty());
    }


    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    { return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()); }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(facing != state.getValue(FACING)) return false;
        if(worldIn.isRemote) return true;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileChiseledBookshelf)) return false;

        TileChiseledBookshelf tileBookshelf = (TileChiseledBookshelf) tile;
        ItemStack held = playerIn.getHeldItem(hand);
        int slot = TileChiseledBookshelf.getSlotFromHit(state.getValue(FACING), hitX, hitY, hitZ);

        ItemStack getSlotContents = tileBookshelf.inventory.getStackInSlot(slot);
        if (getSlotContents.isEmpty())
        {
            if (tileBookshelf.isValidBook(held))
            {
                ItemStack copy = held.copy();
                copy.setCount(1);

                tileBookshelf.inventory.setStackInSlot(slot, copy);

                if(!playerIn.isCreative()) held.shrink(1);

                return true;
            }
        }
        else
        {
            ItemHandlerHelper.giveItemToPlayer(playerIn, getSlotContents.copy(), playerIn.inventory.currentItem);
            tileBookshelf.inventory.setStackInSlot(slot, ItemStack.EMPTY);

            return true;
        }

        return false;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileChiseledBookshelf)
        {
            TileChiseledBookshelf tileBookshelf = (TileChiseledBookshelf)tileentity;

            for (int i = 0; i < tileBookshelf.inventory.getSlots(); ++i)
            {
                ItemStack itemstack = tileBookshelf.inventory.getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    double d0 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
                    double d1 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
                    double d2 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;

                    EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, itemstack);
                    entityitem.setDefaultPickupDelay();
                    worldIn.spawnEntity(entityitem);
                }
            }
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileChiseledBookshelf) { return ((TileChiseledBookshelf) tileentity).getLastInteractedSlot(); }

        return 0;
    }


    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }

    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.byIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        { enumfacing = EnumFacing.NORTH; }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getIndex(); }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, BOOK_0, BOOK_1, BOOK_2, BOOK_3, BOOK_4, BOOK_5);
    }
}