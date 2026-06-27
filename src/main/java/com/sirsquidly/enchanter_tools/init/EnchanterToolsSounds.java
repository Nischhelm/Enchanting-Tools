package com.sirsquidly.enchanter_tools.init;

import com.sirsquidly.enchanter_tools.enchanterTools;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EnchanterToolsSounds
{
    private static List<SoundEvent> soundList = new ArrayList<SoundEvent>();

    public static SoundEvent BLOCK_ARCANE_BRAZIER_STRIP_ITEM = soundReadyForRegister("block.arcane_brazier.use_item");

    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_INSERT = soundReadyForRegister("block.chiseled_bookshelf.insert");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED = soundReadyForRegister("block.chiseled_bookshelf.insert_enchanted");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_PICKUP = soundReadyForRegister("block.chiseled_bookshelf.pickup");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED = soundReadyForRegister("block.chiseled_bookshelf.pickup_enchanted");

    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_BREAK = soundReadyForRegister("block.chiseled_bookshelf.break");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_FALL = soundReadyForRegister("block.chiseled_bookshelf.fall");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_HIT = soundReadyForRegister("block.chiseled_bookshelf.hit");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_PLACE = soundReadyForRegister("block.chiseled_bookshelf.place");
    public static SoundEvent BLOCK_CHISELED_BOOKSHELF_STEP = soundReadyForRegister("block.chiseled_bookshelf.step");

    public static SoundType CHISELED_BOOKSHELF = new SoundType(1.0f, 1.0f, BLOCK_CHISELED_BOOKSHELF_BREAK, BLOCK_CHISELED_BOOKSHELF_STEP, BLOCK_CHISELED_BOOKSHELF_PLACE, BLOCK_CHISELED_BOOKSHELF_HIT, BLOCK_CHISELED_BOOKSHELF_FALL);

    public static SoundEvent ITEM_ENCHANTED_EIGHT_BALL_USE = soundReadyForRegister("item.enchanted_eight_ball.use");

    public static void registerSounds()
    { for (SoundEvent sounds : soundList) ForgeRegistries.SOUND_EVENTS.register(sounds); }

    private static SoundEvent soundReadyForRegister(String name)
    {
        ResourceLocation location = new ResourceLocation(enchanterTools.MOD_ID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        soundList.add(event);

        return event;
    }
}