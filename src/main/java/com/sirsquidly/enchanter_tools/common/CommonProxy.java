package com.sirsquidly.enchanter_tools.common;

import com.sirsquidly.enchanter_tools.client.particle.enchanterToolsParticles;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileArcaneBrazier;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileChiseledBookshelf;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TilePonderingOrb;
import com.sirsquidly.enchanter_tools.config.ConfigParser;
import com.sirsquidly.enchanter_tools.enchanterTools;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsSounds;
import com.sirsquidly.enchanter_tools.network.enchanterToolsPacketHandler;
import com.sirsquidly.enchanter_tools.network.enchanterToolsPacketSpawnParticles;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy
{
    public void preInitRegisteries(FMLPreInitializationEvent event)
    {
        GameRegistry.registerTileEntity(TileArcaneBrazier.class, new ResourceLocation(enchanterTools.MOD_ID, "arcane_brazier"));
        GameRegistry.registerTileEntity(TileChiseledBookshelf.class, new ResourceLocation(enchanterTools.MOD_ID, "chiseled_bookshelf"));
        GameRegistry.registerTileEntity(TilePonderingOrb.class, new ResourceLocation(enchanterTools.MOD_ID, "pondering_orb"));
        EnchanterToolsSounds.registerSounds();
        enchanterToolsPacketHandler.registerMessages();
    }

    public void initRegisteries(FMLInitializationEvent event) {}

    public void postInitRegisteries(FMLPostInitializationEvent event)
    { ConfigParser.breakupConfigArrays(); }

    @SideOnly(Side.CLIENT)
    public void registerItemRenderer(Item item, int meta, String id) {}

    /**
     *  Specialized particle method that sends particles on servers
     * */
    public void spawnParticle(enchanterToolsParticles particle, World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
    {
        if (world.isRemote)
        { spawnParticle(particle, posX, posY, posZ, speedX, speedY, speedZ, parameters); }
        else
        { enchanterToolsPacketHandler.CHANNEL.sendToAllTracking( new enchanterToolsPacketSpawnParticles(particle.getId(), posX, posY, posZ, speedX, speedY, speedZ, parameters), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 0.0D)); }
    }

    public void spawnParticle(enchanterToolsParticles particle, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
    {}
}