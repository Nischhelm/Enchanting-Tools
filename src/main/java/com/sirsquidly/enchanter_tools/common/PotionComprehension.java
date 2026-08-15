package com.sirsquidly.enchanter_tools.common;

import com.sirsquidly.enchanter_tools.config.Config;
import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.enchanterTools;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PotionComprehension extends Potion
{
    protected static final ResourceLocation TEXTURE = new ResourceLocation(enchanterTools.MOD_ID, "textures/gui/potion_effects.png");

    public PotionComprehension(String name, boolean isBad, int color, int icon)
    {
        super(isBad, color);
        this.setPotionName("effect." + enchanterTools.MOD_ID + "." + name + ".name");
        this.setRegistryName(name);
        this.setIconIndex(icon % 8, icon / 8);
    }

    @SubscribeEvent
    public static void onPickupXp(PlayerPickupXpEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        EntityXPOrb orb = event.getOrb();

        if (player.isPotionActive(EnchanterToolsPotions.COMPREHENSION))
        {
            PotionEffect effect = player.getActivePotionEffect(EnchanterToolsPotions.COMPREHENSION);
            int amplifier = effect.getAmplifier();

            float multiplier = (1 + amplifier) * 1.5F;
            orb.xpValue = (int) (orb.xpValue * (multiplier));
        }
    }

    @SubscribeEvent
    public static void onExperiencingDeath(LivingExperienceDropEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();

        /* A few filters to remove the effect. */
        if (event.getDroppedExperience() <= 0) entity.removePotionEffect(EnchanterToolsPotions.COMPREHENSION);
        if (Config.potionEffects.comprehension.bossesAreBlacklisted && !entity.isNonBoss()) entity.removePotionEffect(EnchanterToolsPotions.COMPREHENSION);
        if (ConfigCache.comprehensionEntityBlacklist.contains(EntityList.getKey(entity))) entity.removePotionEffect(EnchanterToolsPotions.COMPREHENSION);

        if (!entity.isPotionActive(EnchanterToolsPotions.COMPREHENSION)) return;

        PotionEffect effect = entity.getActivePotionEffect(EnchanterToolsPotions.COMPREHENSION);
        int amplifier = effect.getAmplifier();

        float multiplier = (1 + amplifier) * 1.5F;

        event.setDroppedExperience((int) (event.getDroppedExperience() * multiplier));

        /* Remove after preforming the effect once. */
        entity.removePotionEffect(EnchanterToolsPotions.COMPREHENSION);
    }

    /* Required so the effect actually runs. */
    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        return super.getStatusIconIndex();
    }
}