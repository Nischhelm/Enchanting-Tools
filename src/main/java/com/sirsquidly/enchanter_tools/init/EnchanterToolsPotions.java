package com.sirsquidly.enchanter_tools.init;

import com.sirsquidly.enchanter_tools.common.PotionComprehension;
import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = enchanterTools.MOD_ID)
public class EnchanterToolsPotions
{
    public static final Potion COMPREHENSION = new PotionComprehension("comprehension", false, 11920498, 0);

    @SubscribeEvent
    public static void onPotionEffectRegister(RegistryEvent.Register<Potion> event)
    {
        if (ConfigCache.comprehensionEnable) event.getRegistry().register(COMPREHENSION);
    }
}