package org.mineshark.ms1.me.seb.java.manager;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
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

    public final Map<UUID, Chest> chestSetup = new HashMap<>();

    public final Map<Chest, Inventory> gameChests = new HashMap<>();

    public void setupChest(Player player, Chest chest) {
        Chest chestMain = chest;

        chestMain.setCustomName("&2Refill Chest Content");

        Inventory chestInv = chestMain.getInventory();

        Location chestLoc = chestMain.getLocation();

        player.openInventory(chestInv);
        chestSetup.put(player.getUniqueId(), chestMain);
    }

    @EventHandler
    public void chestClose(InventoryCloseEvent e) {
        if(!e.getInventory().getType().equals(InventoryType.CHEST)) return;

        Player player = (Player) e.getPlayer();

        if(!chestSetup.containsKey(player.getUniqueId())) return;

        if(!chestSetup.get(player.getUniqueId()).equals(e.getInventory())) return;

        Chest chest = chestSetup.get(player.getUniqueId());

        chestSetup.remove(player);
    }

    @EventHandler
    public void inventoryInteract(InventoryInteractEvent e) {
        return;
    }
}
