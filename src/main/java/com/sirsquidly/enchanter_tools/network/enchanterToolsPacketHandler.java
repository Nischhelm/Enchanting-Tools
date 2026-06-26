package com.sirsquidly.enchanter_tools.network;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class enchanterToolsPacketHandler
{
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(enchanterTools.CHANNEL_ID);
	
	public static void registerMessages()
	{
		int messageId = 0;
		CHANNEL.registerMessage(enchanterToolsPacketSpawnParticles.Handler.class, enchanterToolsPacketSpawnParticles.class, messageId++, Side.CLIENT);
	}
}
