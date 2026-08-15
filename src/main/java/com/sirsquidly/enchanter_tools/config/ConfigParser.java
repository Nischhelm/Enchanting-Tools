package com.sirsquidly.enchanter_tools.config;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 	This is to break part arrays in the config for use in other areas of the code.
 *
 *  I break it up in this class so that I don't have to break the config arrays every time I want to use them.
 */
public class ConfigParser
{
	/** Goes through the many Arrays in the config, to translate them into lists to be used elsewhere. */
	public static void breakupConfigArrays()
	{
		//Resetting all config caches
		ConfigCache.bookshelfAcceptedBooks.clear();
		ConfigCache.bookshelfAcceptedNames.clear();
		ConfigCache.brazierBurnEnchantBlacklist.clear();
		ConfigCache.brazierBurnItemBlacklist.clear();
		ConfigCache.extractBookExtractEnchantBlacklist.clear();
		ConfigCache.extractBookExtractItemBlacklist.clear();
		ConfigCache.eightBallInjectChances.clear();
		ConfigCache.eightBallInjectLootTables.clear();
		ConfigCache.inkwellInjectChances.clear();
		ConfigCache.inkwellInjectLootTables.clear();
		ConfigCache.comprehensionEntityBlacklist.clear();

		//Chiseled Bookshelf
		for(String S : Config.block.chiseledBookshelf.acceptedBooks)
		{
			ItemStack book = getItemStackFromString(S);

			if (book == ItemStack.EMPTY)
			{
                enchanterTools.LOGGER.error("Chiseled Bookshelf accepted book {} was not found, skipping...", S);
				continue;
			}

			ConfigCache.bookshelfAcceptedBooks.add(book);
		}
		ConfigCache.bookshelfAcceptedNames.addAll(Arrays.asList(Config.block.chiseledBookshelf.acceptedNames));

		//Arcane Brazier
		for(String S : Config.block.arcaneBrazier.burningEnchantBlacklist)
		{
			Enchantment enchant = getEnchantmentFromString(S);

			if (enchant == null)
			{
                enchanterTools.LOGGER.error("Chiseled Bookshelf blacklisted enchant {} was not found, skipping...", S);
				continue;
			}
			ConfigCache.brazierBurnEnchantBlacklist.add(S);
		}
		for(String S : Config.block.arcaneBrazier.burningItemBlacklist)
		{
			ItemStack stack = getItemStackFromString(S);

			if (stack == null)
			{
                enchanterTools.LOGGER.error("Arcane Brazier blacklisted item {} was not found, skipping...", S);
				continue;
			}
			ConfigCache.brazierBurnItemBlacklist.add(stack);
		}

		for(String S : Config.item.extractingBook.extractEnchantBlacklist)
		{
			Enchantment enchant = getEnchantmentFromString(S);

			if (enchant == null)
			{
                enchanterTools.LOGGER.error("Extracting Book blacklisted enchant {} was not found, skipping...", S);
				continue;
			}
			ConfigCache.extractBookExtractEnchantBlacklist.add(S);
		}
		for(String S : Config.item.extractingBook.extractItemBlacklist)
		{
			ItemStack stack = getItemStackFromString(S);

			if (stack == null)
			{
                enchanterTools.LOGGER.error("Extracting Book blacklisted item {} was not found, skipping...", S);
				continue;
			}

			ConfigCache.extractBookExtractItemBlacklist.add(stack);
		}
		for(String S : Config.item.enchantedEightBall.eight_ball_lootTables)
		{
			String[] split = S.split("=");

			if (split.length != 2)
			{
                enchanterTools.LOGGER.error("Enchanted 8 Ball loot table entry {} is improperly written! Did you use a '=' properly?", S);
				continue;
			}

			ConfigCache.eightBallInjectLootTables.add(new ResourceLocation(split[0]));
			ConfigCache.eightBallInjectChances.add(Float.valueOf(split[1]));
		}
		/* Split the Inkwell Loot Table Injections up*/
		for(String S : Config.item.inkwell.inkwell_lootTables)
		{
			String[] split = S.split("=");

			if (split.length != 2)
			{
                enchanterTools.LOGGER.error("Enchanted Inkwell loot table entry {} is improperly written! Did you use a '=' properly?", S);
				continue;
			}

			ConfigCache.inkwellInjectLootTables.add(new ResourceLocation(split[0]));
			ConfigCache.inkwellInjectChances.add(Float.valueOf(split[1]));
		}

		for(String S : Config.potionEffects.comprehension.entityBlacklist)
		{
			ResourceLocation entityResourceloc = getEntityFromString(S);
			if (entityResourceloc == null)
			{
                enchanterTools.LOGGER.error("Comprehension effect entity {} is returning null, is the entity ID written properly/does it exist? Skipping...", S);
				continue;
			}
			ConfigCache.comprehensionEntityBlacklist.add(entityResourceloc);
		}
	}

	public static boolean isStackInList(ItemStack input, List<ItemStack> configList)
	{
        for (ItemStack configStack : configList)
		{
            if (configStack.getItem() == input.getItem() && (configStack.getMetadata() == OreDictionary.WILDCARD_VALUE || configStack.getMetadata() == input.getMetadata()))
			{ return true; }
        }
		return false;
	}

	public static boolean hasWhitelistedName(ItemStack stack, Set<String> validNames)
	{
		if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return false;
		//Blocks cannot be stored in the bookshelf unless specifically whitelisted
		if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) return false;
		String itemId = stack.getItem().getRegistryName().getPath();
		for (String name : validNames)
		{
			if(itemId.contains(name))
			{ return true; }
		}
		return false;
	}

	public static ItemStack getItemStackFromString(String string)
	{
		String[] ripString = string.split(":");

		if (ripString.length < 2) return ItemStack.EMPTY;

		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(ripString[0], ripString[1]));
		if(item == null) return ItemStack.EMPTY;

		int meta = OreDictionary.WILDCARD_VALUE;
		if (ripString.length > 2) meta = Integer.parseInt(ripString[2]);

		return new ItemStack(item, 1, meta);
	}

	public static Enchantment getEnchantmentFromString(String string)
	{
		ResourceLocation rl = new ResourceLocation(string);
		Enchantment ench = Enchantment.REGISTRY.getObject(rl);
		return ench;
	}

	public static ResourceLocation getEntityFromString(String string)
	{
		if(GameData.getEntityRegistry().containsKey(new ResourceLocation(string))) return new ResourceLocation(string);
		return null;
	}

	public static List<IBlockState> getBlockStatesFromString(String string)
	{
		List<IBlockState> states = new ArrayList<>();
		String[] ripString = string.split(":");

		if (ripString.length < 2)
		{ return states; }

		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(ripString[0], ripString[1]));
		Integer meta;

		if(block == null || block == Blocks.AIR)
		{ return states; }
		if(ripString.length > 2)
		{
			if (ripString[2].equals("*")) meta = -1;
			else meta = Integer.parseInt(ripString[2]);

			if(meta == -1) states.addAll(block.getBlockState().getValidStates());
			else states.add(block.getStateFromMeta(meta));
		}
		else states.add(block.getDefaultState());

		return states;
	}
}