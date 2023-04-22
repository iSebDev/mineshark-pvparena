package org.mineshark.ms1.me.seb.java.manager;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.mineshark.ms1.me.seb.java.MineShark1;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestManager implements Listener {
    private static MineShark1 plugin;
    public ChestManager(MineShark1 instance) {
        plugin = instance;
    }

    public Map<UUID, Chest> chestSetup = new HashMap<>();

    public void setupChest(Player player, Chest chest) {
        Chest chestMain = chest;

        chestMain.setCustomName("&2Refill Chest Content");

        Inventory chestInv = chestMain.getInventory();

        player.openInventory(chestInv);
    }

    @EventHandler
    public void inventoryInteract(InventoryInteractEvent e) {
        return;
    }
}
