package com.sirsquidly.enchanter_tools.common.blocks.tileentity;

import com.sirsquidly.enchanter_tools.config.ConfigCache;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import org.lwjgl.Sys;

import java.util.List;

public class TilePonderingOrb extends TileEntity implements ITickable
{
    public long lastInteractTime = 0;

    @Override
    public void update()
    {
        if(getWorld().isRemote || world.getTotalWorldTime() % (20L * ConfigCache.ponderingOrbPonderTime) != 0L) return;

        boolean pondered = false;
        int range = 8;
        List<EntityPlayer> players = getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)));

        for(EntityPlayer player : players)
        {
            if (playerIsPonderingOrb(player))
            {
                pondered = true;
                world.playSound(null, this.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.01F, 0.75F + (this.world.rand.nextFloat() * 0.5F));
                player.addExperience(ConfigCache.ponderingOrbPonderXP);
                if (world.rand.nextInt(50) == 0) player.sendStatusMessage(new TextComponentTranslation("message.enchanter_tools.pondering_orb.idle" + world.rand.nextInt(22)), true);
            }
        }

        if(pondered)
        {
            double x = getPos().getX() - 0.1 + Math.random() * 1.2;
            double y = getPos().getY() - 0.1 + Math.random() * 1.2;
            double z = getPos().getZ() - 0.1 + Math.random() * 1.2;

            ((WorldServer) getWorld()).spawnParticle(EnumParticleTypes.SPELL_MOB, false, x, y, z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
        }
    }

    public boolean playerIsPonderingOrb(EntityPlayer player)
    {
        Vec3d eyePosition = player.getPositionEyes(1.0F);
        Vec3d lookVector = player.getLook(1.0F);
        double playerReach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        RayTraceResult rtresult = player.getEntityWorld().rayTraceBlocks(eyePosition, traceEnd, false, true, false);;

        if (rtresult == null) return false;
        if (rtresult.typeOfHit == RayTraceResult.Type.BLOCK) return rtresult.getBlockPos().equals(this.getPos());
        return false;
    }
}
