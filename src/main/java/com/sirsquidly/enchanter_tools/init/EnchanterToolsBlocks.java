package com.sirsquidly.enchanter_tools.init;

import com.sirsquidly.enchanter_tools.common.blocks.BlockArcaneBrazier;
import com.sirsquidly.enchanter_tools.common.blocks.BlockChiseledBookshelf;
import com.sirsquidly.enchanter_tools.common.blocks.BlockPonderingOrb;
import com.sirsquidly.enchanter_tools.config.Config;
import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = enchanterTools.MOD_ID)
public class EnchanterToolsBlocks
{
    /** Contains every block ran through the 'blockReadyForRegister' function. So they all SHOULD be registered. */
    private static List<Block> blockList = new ArrayList<Block>();
    /** Records any blocks that have a unique itemBlock assigned, as the rest are automatically slapped with the default itemBlock.*/
    private static List<Block> itemBlockBlacklist = new ArrayList<Block>();
    /** Any blocks that should skip registering a normal Item Model. */
    private static List<Block> itemModelBlacklist = new ArrayList<Block>();
    /** Used for items settup here, and need to have models registered as such. */
    private static List<Item> blockDirectItemList = new ArrayList<Item>();

    public static Block CHISELED_BOOKSHELF = new BlockChiseledBookshelf().setHardness(1.5F).setCreativeTab(CreativeTabs.DECORATIONS);
    public static Block PONDERING_ORB = new BlockPonderingOrb().setHardness(2.5F).setCreativeTab(CreativeTabs.DECORATIONS);

    public static Block ARCANE_BRAZIER = new BlockArcaneBrazier().setHardness(2.5F).setLightLevel(0.7F).setCreativeTab(CreativeTabs.DECORATIONS);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        if (Config.block.arcaneBrazier.enable) blockReadyForRegister(ARCANE_BRAZIER, "arcane_brazier");
        if (Config.block.chiseledBookshelf.enable) blockReadyForRegister(CHISELED_BOOKSHELF, "chiseled_bookshelf");
        if (Config.block.ponderingOrb.enable) blockReadyForRegister(PONDERING_ORB, "pondering_orb");

        for (Block blocks : blockList) event.getRegistry().register(blocks);
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        /** As stated on itemBlockBlacklist, this registers anything NOT from the blacklist with a generic itemBlock.*/
        for (Block blocks : blockList) if (!(itemBlockBlacklist.contains(blocks))) { registerItemBlock(r, blocks);}
    }


    /** If blocks don't specify the 'addToTab' boolean, assume true.*/
    public static Block blockReadyForRegister(Block block, String name)
    { return blockReadyForRegister(block, name, true);}

    /** Slaps the names to Blocks, and adds them to the blockList to be registered in 'registerBlocks'.*/
    public static Block blockReadyForRegister(Block block, String name, Boolean addNormalModel)
    {
        block.setTranslationKey(enchanterTools.MOD_ID + "." + name);
        block.setRegistryName(name);

        // if (addToTab) block.setCreativeTab(Main.OCEANEXPTAB);
        // else block.setCreativeTab(null);

        blockList.add(block);

        return block;
    }

    public static ItemBlock registerItemBlock(IForgeRegistry<Item> registry, Block block)
    { return registerItemBlock(registry, new ItemBlock(block)); }

    public static <T extends ItemBlock> T registerItemBlock(IForgeRegistry<Item> registry, T item) {
        Block block = item.getBlock();
        item.setTranslationKey(block.getTranslationKey());
        item.setRegistryName(block.getRegistryName());

        registry.register(item);

        return item;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {
        for(Item i : blockDirectItemList)
        { ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory")); }

        for(Block b : blockList)
        { if (!(itemModelBlacklist.contains(b))) ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory")); }
    }
}