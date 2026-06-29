package com.sirsquidly.enchanter_tools.common;

import com.sirsquidly.enchanter_tools.common.items.IAnvilSpecialBehavior;
import com.sirsquidly.enchanter_tools.config.ConfigCache;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsItems;
import com.sirsquidly.enchanter_tools.init.EnchanterToolsPotions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * WHY IS THE ANVIL SO FUCKING HARD CODED GAAAAAAHHHHHHHH FORRRGE
 * */
@Mod.EventBusSubscriber
public class CommonEvents
{
    public static final Map<UUID, AnvilRefresherStorage> anvilRefreshers = new HashMap<>();

    /** This first is used for setting up the Output properly. */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event)
    {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) return;

        if (right.getItem() instanceof IAnvilSpecialBehavior)
        {
            ((IAnvilSpecialBehavior) right.getItem()).onAnvilUpdate(event, left, right);
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (!(player.openContainer instanceof ContainerRepair)) return;
        ContainerRepair container = (ContainerRepair) player.openContainer;

        ItemStack left = event.getItemInput();
        ItemStack right = container.getSlot(1).getStack();

        if (left.isEmpty() || right.isEmpty()) return;
        if (!(right.getItem() instanceof IAnvilSpecialBehavior)) return;

        AnvilRefresherStorage storage = ((IAnvilSpecialBehavior) right.getItem()).getAnvilRepairOutput(event, container, player, left, right);

        if (storage != null) anvilRefreshers.put(player.getUniqueID(), storage);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;

        AnvilRefresherStorage storage = anvilRefreshers.get(event.player.getUniqueID());
        if (storage == null || event.player.openContainer != storage.container) return;
        anvilRefreshers.remove(event.player.getUniqueID());

        if (storage.left != null)
        { storage.container.putStackInSlot(0, storage.left); }
        if (storage.right != null)
        { storage.container.putStackInSlot(1, storage.right); }

        storage.container.detectAndSendChanges();
    }

    /** This is a backup in case the Player breaks the anvil when using any item. */
    @SubscribeEvent
    public static void onContainerClosed(PlayerContainerEvent.Close event)
    {
        if (!(event.getContainer() instanceof ContainerRepair)) return;

        AnvilRefresherStorage storage = anvilRefreshers.remove(event.getEntityPlayer().getUniqueID());
        if (storage == null) return;

        if (storage.left != null)
        {
            if (!event.getEntityPlayer().inventory.addItemStackToInventory(storage.left))
            { event.getEntityPlayer().dropItem(storage.left, false); }
        }

        if (storage.right != null)
        {
            if (!event.getEntityPlayer().inventory.addItemStackToInventory(storage.right))
            { event.getEntityPlayer().dropItem(storage.right, false); }
        }
    }

    /** This stores the Anvil's slot items, because the normal anvil is STUPIDLY INCONSISTENT with it. */
    public static class AnvilRefresherStorage
    {
        public final ContainerRepair container;
        @Nullable public final ItemStack left;
        @Nullable public final ItemStack right;

        public AnvilRefresherStorage(ContainerRepair container, @Nullable ItemStack left, @Nullable ItemStack right)
        {
            this.container = container;
            this.left = left;
            this.right = right;
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event)
    {
        if (!(event.getEntity() instanceof EntityExpBottle)) return;
        if (event.getEntity().getEntityWorld().isRemote) return;

        int duration = ConfigCache.comprehensionXPBottleTime;
        if (duration <= 0) return;

        BlockPos landPos = event.getRayTraceResult().getBlockPos();
        if (event.getRayTraceResult().typeOfHit == RayTraceResult.Type.ENTITY) landPos = event.getRayTraceResult().entityHit.getPosition();

        if (landPos == null) return;

        AxisAlignedBB axisalignedbb = new AxisAlignedBB(landPos).grow(3.0D, 2.0D, 3.0D);
        List<EntityLivingBase> list = event.getEntity().getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

        if (!list.isEmpty())
        {
            for (EntityLivingBase entitylivingbase : list)
            {
                if (entitylivingbase.canBeHitWithPotion())
                {
                    entitylivingbase.addPotionEffect(new PotionEffect(EnchanterToolsPotions.COMPREHENSION, duration, 0));
                }
            }
        }
    }





    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event)
    {
        ResourceLocation tableName = event.getName();

        if (ConfigCache.eightBallEnable && ConfigCache.eightBallInjectLootTables.contains(tableName))
        {
            injectPool(event.getTable(), ConfigCache.eightBallInjectChances.get(ConfigCache.eightBallInjectLootTables.indexOf(tableName)),
                    "enchanted_eight_ball_pool", createLootEntry("enchanted_eight_ball_entry", EnchanterToolsItems.ENCHANTED_EIGHT_BALL, CommonEvents::createEnchantedEightBall));
        }

        if (ConfigCache.inkwellEnable && ConfigCache.inkwellInjectLootTables.contains(tableName))
        {
            injectPool(event.getTable(), ConfigCache.inkwellInjectChances.get(ConfigCache.inkwellInjectLootTables.indexOf(tableName)),
                    "enchanted_inkwell_ball_pool", createLootEntry("enchanted_inkwell_ball_entry", EnchanterToolsItems.ENCHANTED_INKWELL, CommonEvents::createEnchantedInkwell));
        }
    }

    private static void injectPool(LootTable table, float chance, String poolName, LootEntry entry)
    {
        LootPool pool = new LootPool( new LootEntry[] {entry}, new LootCondition[] { new RandomChance(chance) },
                new RandomValueRange(1), new RandomValueRange(0), poolName);
        table.addPool(pool);
    }

    private static LootEntry createLootEntry(String entryName, Item item, Function<Random, ItemStack> stackFactory)
    {
        return new LootEntryItem(item, 1, 0,  new LootFunction[0], new LootCondition[0], entryName)
        {
            @Override
            public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context)
            { stacks.add(stackFactory.apply(rand)); }
        };
    }

    private static ItemStack createEnchantedInkwell(Random rand)
    {
        ItemStack stack = new ItemStack(EnchanterToolsItems.ENCHANTED_INKWELL);
        applyRandomSingleMaxEnchant(rand, stack, 30, 30, true);
        return stack;
    }

    private static ItemStack createEnchantedEightBall(Random rand)
    {
        ItemStack stack = new ItemStack(EnchanterToolsItems.ENCHANTED_EIGHT_BALL);
        applyRandomSingleMaxEnchant(rand, stack, 15, 30, false);
        return stack;
    }

    /** Attaches one, max level enchantment to the given item. */
    private static void applyRandomSingleMaxEnchant(Random rand, ItemStack stack, int minPower, int maxPower, boolean maxLevel)
    {
        int power = minPower;
        if (maxPower > minPower) power += rand.nextInt(maxPower - minPower + 1);

        EnchantmentHelper.addRandomEnchantment(rand, stack, power, true);

        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (enchants.isEmpty()) return;

        List<Map.Entry<Enchantment, Integer>> entries = new ArrayList<>(enchants.entrySet());
        Map.Entry<Enchantment, Integer> chosen = entries.get(rand.nextInt(entries.size()));
        enchants.clear();
        int level = maxLevel ? chosen.getKey().getMaxLevel() : chosen.getValue();

        enchants.put(chosen.getKey(), level);

        EnchantmentHelper.setEnchantments(enchants, stack);
    }
}