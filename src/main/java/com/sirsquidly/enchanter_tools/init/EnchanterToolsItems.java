package com.sirsquidly.enchanter_tools.init;

import com.sirsquidly.enchanter_tools.common.items.*;
import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = enchanterTools.MOD_ID)
public class EnchanterToolsItems
{
    public static final List<Item> itemList = new ArrayList<Item>();

    public static Item EXTRACTING_BOOK = new ItemExtractingBook().setCreativeTab(CreativeTabs.TOOLS);
    public static Item ENCHANTED_INKWELL = new ItemEnchantedInkwell().setCreativeTab(CreativeTabs.TOOLS);
    public static Item ENCHANTED_EIGHT_BALL = new ItemEnchanted8Ball().setCreativeTab(CreativeTabs.MISC);
    public static Item OBSIDIAN_BURNISHER = new ItemObsidianBurnisher().setCreativeTab(CreativeTabs.TOOLS);

    public static LinkedHashMap<ItemLapisRune.RuneType, ItemLapisRune> LAPIS_RUNES = new LinkedHashMap<>();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if (ConfigCache.extractBookEnable) itemReadyForRegister(EXTRACTING_BOOK, "extracting_book");
        if (ConfigCache.eightBallEnable) itemReadyForRegister(ENCHANTED_EIGHT_BALL, "enchanted_eight_ball");
        if (ConfigCache.inkwellEnable) itemReadyForRegister(ENCHANTED_INKWELL, "enchanted_inkwell");
        if (ConfigCache.burnisherEnable) itemReadyForRegister(OBSIDIAN_BURNISHER, "obsidian_burnisher");

        ItemLapisRune.RuneType.readFromConfig();
        ItemLapisRune.RuneType.getAll().forEach(type ->
        {
            ItemLapisRune rune = (ItemLapisRune)itemReadyForRegister(new ItemLapisRune(type), type.getName() + "_lapis_rune");
            LAPIS_RUNES.put(type, rune);
        });

        for (Item items : itemList) event.getRegistry().register(items);

        /* Yes, all the runes are still considered Lapis. This allows them into the lapis slot of the Enchanting Table. */
        LAPIS_RUNES.values().forEach(rune -> { OreDictionary.registerOre("gemLapis", rune);});
    }

    public static Item itemReadyForRegister(Item item, String name)
    {
        if (name != null)
        {
            item.setTranslationKey(enchanterTools.MOD_ID + "." + name);
            item.setRegistryName(name);
        }

        itemList.add(item);

        return item;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    { for (Item items : itemList) enchanterTools.proxy.registerItemRenderer(items, 0, "inventory"); }
}