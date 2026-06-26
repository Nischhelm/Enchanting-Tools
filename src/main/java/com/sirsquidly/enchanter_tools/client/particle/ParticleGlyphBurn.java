package com.sirsquidly.enchanter_tools.client.particle;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleGlyphBurn extends ParticleBase
{
    private final Vec3d targetPos;

    private static final ResourceLocation GLYPH_D_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/glyph_d_burn.png");
    private static final ResourceLocation GLYPH_F_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/glyph_f_burn.png");
    private static final ResourceLocation GLYPH_H_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/glyph_h_burn.png");
    private static final ResourceLocation GLYPH_L_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/glyph_l_burn.png");
    private static final ResourceLocation GLYPH_O_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/glyph_o_burn.png");
    private static final ResourceLocation GLYPH_Q_TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/particles/glyph_q_burn.png");
    private static final ResourceLocation[] GLYPH_TEXTURES = new ResourceLocation[] {GLYPH_D_TEXTURE, GLYPH_F_TEXTURE, GLYPH_H_TEXTURE, GLYPH_L_TEXTURE, GLYPH_O_TEXTURE};

    public ParticleGlyphBurn(TextureManager textureManager, World world, double x, double y, double z, double movementX, double movementY, double movementZ, int glowing)
    {
        super(textureManager, world, x, y, z, movementX, movementY, movementZ, GLYPH_TEXTURES[world.rand.nextInt(GLYPH_TEXTURES.length)], 0);
        this.textureManager = textureManager;

        this.targetPos = new Vec3d(movementX, movementY, movementZ);

        Vec3d current = new Vec3d(posX, posY, posZ);
        Vec3d motionVec = this.targetPos.subtract(current).scale(1.0 / (20 + (20 * this.rand.nextFloat())));

        motionX = motionVec.x;
        motionY = motionVec.y;
        motionZ = motionVec.z;

        this.canCollide = false;
        this.particleMaxAge = (int)(Math.random() * 10.0D) + 30;
        this.texSheetSeg = 3;
        this.renderYOffset = this.height / 2;
        this.particleScale = this.rand.nextFloat() * 0.5F + 0.6F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        //Vec3d currentPos = new Vec3d(posX, posY, posZ);
        //if (currentPos.distanceTo(this.targetPos) < 0.5) { setExpired(); }
        this.texSpot = Math.min(this.particleAge * 9 / (this.particleMaxAge), 8);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) { return brightnessIncreaseToFull(partialTicks); }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
    {
        @Override
        public Particle createParticle(int particleId, World world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int... parameters)
        {
            switch (parameters.length)
            {
                case 1:
                    return new ParticleGlyphBurn(Minecraft.getMinecraft().getTextureManager(), world, posX, posY, posZ, speedX, speedY, speedZ, parameters[0]);
            }
            return null;
        }
    }
}