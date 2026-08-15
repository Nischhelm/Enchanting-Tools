package com.sirsquidly.enchanter_tools.asm;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({ "com.sirsquidly.enchanter_tools.asm" })
public class enchanterToolsPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass() { return null; }

    @Override
    public String getModContainerClass() { return null; }

    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data)
    {
        try
        {
            if (GlobalProperties.get(GlobalProperties.Keys.INIT) == null) { MixinBootstrap.init(); }
            MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        }
        catch (Throwable t)
        { enchanterTools.LOGGER.fatal(t); }
    }

    @Override
    public String getAccessTransformerClass() { return null; }
}