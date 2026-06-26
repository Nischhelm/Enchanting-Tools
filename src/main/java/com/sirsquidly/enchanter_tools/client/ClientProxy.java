package com.sirsquidly.enchanter_tools.client;

import com.sirsquidly.enchanter_tools.client.particle.*;
import com.sirsquidly.enchanter_tools.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy
{
    public void registerItemRenderer(Item item, int meta, String id)
    { ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id)); }

    public void preInitRegisteries(FMLPreInitializationEvent event)
    {
        super.preInitRegisteries(event);

        //ClientRegistry.bindTileEntitySpecialRenderer(TileChiseledBookshelf.class, new RenderChiseledBookshelf());
    }

    @Override
    public void spawnParticle(enchanterToolsParticles particle, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.world;
        minecraft.effectRenderer.addEffect(getFactory(particle.getId()).createParticle(0, world, posX, posY, posZ, speedX, speedY, speedZ, parameters));
    }

    /**
     * This is used by the Particle Spawning as an ID system for out Particles.
     * We do not require Ids for Particles, it's just more convenient for sending over packets!
     */
    @SideOnly(Side.CLIENT)
    public static IParticleFactory getFactory(int particleId)
    {
        switch (enchanterToolsParticles.fromId(particleId))
        {
            case BRAZIER_SMOKE: return new ParticleBrazierSmoke.Factory();
            case BRAZIER_EMBER: return new ParticleBrazierEmber.Factory();
            case BRAZIER_FLAME_FADE: return new ParticleBrazierFlame.Factory();
            default:
            case BRAZIER_RUNE_BURN: return new ParticleGlyphBurn.Factory();
        }
    }
}