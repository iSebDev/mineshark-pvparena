package org.mineshark.ms1.me.seb.java.events;

import com.sun.istack.internal.NotNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineshark.ms1.me.seb.java.MineShark1;

public class Interact implements Listener {

    private static MineShark1 plugin;

    public Interact(MineShark1 instance) {
        plugin = instance;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = e.getPlayer();

        ItemStack tool = new ItemStack(Material.BLAZE_ROD);

        ItemMeta meta = tool.getItemMeta();

        meta.setDisplayName(plugin.format("&eLocations Tool"));

        tool.setItemMeta(meta);

        if(player.getInventory().getItemInMainHand().equals(tool)
                && plugin.handler.inSetup.containsKey(player.getUniqueId())) {
            plugin.handler.addLocation(player, plugin.handler.inSetup.get(player.getUniqueId()),
                    e.getClickedBlock().getLocation());
        }

    }
}
