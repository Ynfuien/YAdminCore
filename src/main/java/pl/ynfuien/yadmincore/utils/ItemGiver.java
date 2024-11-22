package pl.ynfuien.yadmincore.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemGiver {
    // Gives player items. Returns:
    // true - when all items were given to inventory
    // false - when some items were dropped on ground
    public static boolean give(Player p, ItemStack item) {
        return give(p, new ItemStack[] {item});
    }

    public static boolean give(Player p, ItemStack item, boolean extendMaxStackSizes) {
        return give(p, new ItemStack[] {item}, extendMaxStackSizes);
    }

    public static boolean give(Player p, ItemStack[] items) {
        return give(p, items, false);
    }

    public static boolean give(Player p, ItemStack[] items, boolean extendMaxStackSizes) {
        if (!extendMaxStackSizes) {
            items = fixStackSizes(items);
        }

        // Items that couldn't be added to player's inventory
        HashMap<Integer, ItemStack> remainingItems = p.getInventory().addItem(items);

        if (remainingItems.size() == 0) return true;

        // Get player's world
        World world = p.getWorld();
        // Get player's eye location
        Location loc = p.getEyeLocation();
        // Loop through remaining items
        for (ItemStack remainingItem : remainingItems.values()) {
            world.dropItem(loc, remainingItem);
        }

        return false;
    }

    // Corrects stack sizes to respect materials max stack size.
    private static ItemStack[] fixStackSizes(ItemStack[] items) {
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack item : items) {
            int amount = item.getAmount();
            int maxStackSize = item.getMaxStackSize();
            if (amount <= maxStackSize) {
                result.add(item);
                continue;
            }

            int stacks = amount / maxStackSize;
            int rest = amount % maxStackSize;
            ItemStack maxStackSizedItemStack = new ItemStack(item);
            maxStackSizedItemStack.setAmount(maxStackSize);

            for (int i = 0; i < stacks; i++) {
                result.add(new ItemStack(maxStackSizedItemStack));
            }


            ItemStack restSizedItemStack = new ItemStack(item);
            restSizedItemStack.setAmount(rest);
            result.add(restSizedItemStack);
        }

        return result.toArray(ItemStack[]::new);
    }
}
