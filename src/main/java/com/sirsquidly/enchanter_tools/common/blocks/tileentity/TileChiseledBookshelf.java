package com.sirsquidly.enchanter_tools.common.blocks.tileentity;

import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.config.ConfigParser;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsSounds;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileChiseledBookshelf extends TileEntity
{
    private int lastInteractedSlot = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(6)
    {
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack)
        {
            validateSlotIndex(slot);

            ItemStack oldStack = this.stacks.get(slot).copy();

            if (world != null && !world.isRemote)
            {
                if (oldStack.isEmpty() && !stack.isEmpty())
                {
                    SoundEvent sound = stack.getItem() == Items.ENCHANTED_BOOK ? EnchanterToolsSounds.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED : EnchanterToolsSounds.BLOCK_CHISELED_BOOKSHELF_INSERT;
                    world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                else
                {
                    SoundEvent sound = oldStack.getItem() == Items.ENCHANTED_BOOK ? EnchanterToolsSounds.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED : EnchanterToolsSounds.BLOCK_CHISELED_BOOKSHELF_PICKUP;
                    world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }

            this.stacks.set(slot, stack);
            onContentsChanged(slot);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            if (world != null)
            {
                setLastInteractedSlot(slot + 1);
                markDirty();
                if (!world.isRemote)
                {
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
                }
            }
        }

        /** ONLY allow inserting Books. */
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!isValidBook(stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public int getSlotLimit(int slot)  { return 1; }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return isValidBook(stack); }
    };

    public boolean isValidBook(ItemStack stack)
    {
        if (ConfigCache.bookshelfAcceptedBooks.isEmpty()) return false;
        return ConfigParser.isStackInList(stack, ConfigCache.bookshelfAcceptedBooks);
    }

    public static int getSlotFromHit(EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        float rotatedX;

        switch (facing)
        {
            case NORTH: rotatedX = 1f - hitX; break;
            case SOUTH: rotatedX = hitX; break;
            case WEST: rotatedX = hitZ; break;
            case EAST: rotatedX = 1f - hitZ; break;
            default: rotatedX = hitX;
        }

        int column = (int)(rotatedX * 3f);
        int row = hitY > 0.5f ? 0 : 1;

        column = MathHelper.clamp(column, 0, 2);

        return row * 3 + column;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    { return writeToNBT(new NBTTagCompound()); }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    { return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());}

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        if (world != null) world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public int getLastInteractedSlot() { return lastInteractedSlot; }
    public void setLastInteractedSlot(int slotIn) { lastInteractedSlot = slotIn; }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("inv", inventory.serializeNBT());
        compound.setInteger("LastInteractedSlot", getLastInteractedSlot());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inv"));
        setLastInteractedSlot(compound.getInteger("LastInteractedSlot"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    { return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing); }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        { return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory); }
        return super.getCapability(capability, facing);
    }
}