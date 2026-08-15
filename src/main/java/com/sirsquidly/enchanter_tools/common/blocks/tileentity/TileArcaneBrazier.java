package com.sirsquidly.enchanter_tools.common.blocks.tileentity;

import com.sirsquidly.enchanter_tools.client.particle.enchanterToolsParticles;
import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileArcaneBrazier extends TileEntity implements ITickable
{
    private Enchantment savedEnchantment;
    private final Set<BlockPos> nearbyTables = new HashSet<>();

    @Override
    public void update()
    {
        if (world.isRemote) return;

        if (this.getSavedEnchantment() == null) return;

        /* Only update tables every 5 seconds. */
        if (!ayoIsAnyoneHere(20)) return;

        /* Only update tables every 5 seconds. */
        if (world.getTotalWorldTime() % 100 == 0) refreshNearbyTables();

        if (!nearbyTables.isEmpty())
        {
            for (BlockPos tablePos : nearbyTables)
            {
                if (world.rand.nextInt(16) != 0) continue;

                double thisX = pos.getX() + 0.5 + (world.rand.nextDouble() * 0.4) - 0.2;
                double thisY = pos.getY() + 0.4 + (world.rand.nextDouble() * 0.4) - 0.2;
                double thisZ = pos.getZ() + 0.5 + (world.rand.nextDouble() * 0.4) - 0.2;

                double tableX = tablePos.getX() + 0.5;
                double tableY = tablePos.getY() + 0.8;
                double tableZ = tablePos.getZ() + 0.5;

                enchanterTools.proxy.spawnParticle(enchanterToolsParticles.BRAZIER_RUNE_BURN, world, tableX, tableY, tableZ, thisX, thisY, thisZ, 0);
            }
        }
        
        int playerItemDetectRange = 3;

        for(EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(-playerItemDetectRange, -playerItemDetectRange, -playerItemDetectRange), pos.add(playerItemDetectRange + 1, playerItemDetectRange + 1, playerItemDetectRange + 1))))
        {
            int sphereCheck = (playerItemDetectRange + 1) * (playerItemDetectRange + 1);
            if (player.getDistanceSqToCenter(pos) < sphereCheck)
            {
                ItemStack heldMainStack = player.getHeldItemMainhand();

                Map<Enchantment, Integer> playerHeldItemEnchants = EnchantmentHelper.getEnchantments(heldMainStack);

                 if (playerHeldItemEnchants.containsKey(this.getSavedEnchantment()))
                 {
                     double thisX = pos.getX() + 0.5 + (world.rand.nextDouble() * 0.4) - 0.2;
                     double thisY = pos.getY() + 0.3 + (world.rand.nextDouble() * 0.4) - 0.2;
                     double thisZ = pos.getZ() + 0.5 + (world.rand.nextDouble() * 0.4) - 0.2;

                     double motionX = 0;
                     double motionY = (world.rand.nextDouble() * 0.5);
                     double motionZ = 0;

                     enchanterTools.proxy.spawnParticle(enchanterToolsParticles.BRAZIER_EMBER, world, thisX, thisY, thisZ, motionX, motionY, motionZ, 0);
                 }
            }
        }
    }

    private void refreshNearbyTables()
    {
        nearbyTables.clear();

        for (BlockPos pos : BlockPos.getAllInBox( pos.add(-3, -2, -3), pos.add(3, 2, 3)))
        {
            if (world.getBlockState(pos).getBlock() == Blocks.ENCHANTING_TABLE) nearbyTables.add(pos.toImmutable());
        }
    }

    private boolean ayoIsAnyoneHere(double range)
    {
        return !world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB( pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range)).isEmpty();
    }

    /** This prevents state changes from wiping the Tile Entity. */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    public void setEnchantment(Enchantment enchant)
    {
        savedEnchantment = enchant;
        markDirty();
    }

    public Enchantment getSavedEnchantment()
    {
        return savedEnchantment;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (savedEnchantment != null) compound.setString("enchantment", getSavedEnchantment().getRegistryName().toString());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("enchantment")) savedEnchantment = Enchantment.getEnchantmentByLocation(compound.getString("enchantment"));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }
}