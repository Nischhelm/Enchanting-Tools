package com.sirsquidly.enchanter_tools;

import com.sirsquidly.enchanter_tools.common.CommonProxy;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = enchanterTools.MOD_ID, name = enchanterTools.NAME, version = enchanterTools.VERSION, dependencies = enchanterTools.DEPENDENCIES)
public class enchanterTools {
    public static final String MOD_ID = "enchanter_tools";
    public static final String CHANNEL_ID = "enchanter_tools";
    public static final String NAME = "Enchanter Tools";
    public static final String CONFIG_NAME = "enchanter_tools";
    public static final String VERSION = "1.1.1a";
    public static final String DEPENDENCIES = "";
    public static final String CLIENT_PROXY_CLASS = "com.sirsquidly.enchanter_tools.client.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.sirsquidly.enchanter_tools.common.CommonProxy";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance
    public static enchanterTools instance;

    @SidedProxy(clientSide = enchanterTools.CLIENT_PROXY_CLASS, serverSide = enchanterTools.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    { proxy.preInitRegisteries(event); }

    @EventHandler
    public void init(FMLInitializationEvent event)
    { proxy.initRegisteries(event); }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {  proxy.postInitRegisteries(event);  }

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {}
}
