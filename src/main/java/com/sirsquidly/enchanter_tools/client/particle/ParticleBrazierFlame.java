package com.sirsquidly.enchanter_tools.client.particle;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBrazierFlame extends ParticleBase
{
    final int frameOffset;

    private static final ResourceLocation BRAZIER_FLAME_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/brazier_flame_fading.png");

    public ParticleBrazierFlame(TextureManager textureManager, World world, double x, double y, double z, double movementX, double movementY, double movementZ)
    {
        super(textureManager, world, x, y, z, movementX, movementY, movementZ, BRAZIER_FLAME_TEXTURE, 0);
        this.textureManager = textureManager;
        this.motionX = movementX;
        this.motionY = movementY;
        this.motionZ = movementZ;
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.frameOffset = this.rand.nextInt(3);
        this.particleMaxAge = 15 + this.rand.nextInt(15);
        this.particleScale = (this.rand.nextFloat() * 0.5F) + 2F;
        this.texSheetSeg = 4;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //this.motionY += 0.004D;
        this.motionX *= 0.99;
        this.motionZ *= 0.99;

        this.texSpot = Math.min((this.particleAge * 16 / (this.particleMaxAge)) + this.frameOffset, 15);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) { return super.getBrightnessForRender(partialTicks); }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
    {
        @Override
        public Particle createParticle(int particleId, World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
        {
            return new ParticleBrazierFlame(Minecraft.getMinecraft().getTextureManager(), world, posX, posY, posZ, speedX, speedY, speedZ);
        }
    }
}