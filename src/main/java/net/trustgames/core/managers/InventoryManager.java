package net.trustgames.core.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Handles the creation of the inventory (gui)
 */
public class InventoryManager {

    /**
     * @param player Owner of the inventory
     * @param columns Number of columns (max 6)
     * @param title Title of the inventory
     * @return created Inventory
     */
    public static Inventory createInventory(Player player, int columns, String title) {
        int slots = columns * 9;
        if (columns > 6)
            Bukkit.getLogger().severe("Inventory of player " + player.getName() + " has " + columns + " columns! The maximum is 6 (54 slots)");
        return Bukkit.createInventory(player, slots, Component.text(title));
    }
}
