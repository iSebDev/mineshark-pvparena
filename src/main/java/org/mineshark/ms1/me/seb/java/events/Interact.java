package org.mineshark.ms1.me.seb.java.events;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineshark.ms1.me.seb.java.MineShark1;

import java.util.List;

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

        ItemStack tool2 = new ItemStack(Material.STICK);

        ItemMeta meta2 = tool.getItemMeta();

        meta2.setDisplayName(plugin.format("&eChests Tool"));

        tool2.setItemMeta(meta);

        if(player.getInventory().getItemInMainHand().equals(tool)
                && plugin.handler.inSetup.containsKey(player.getUniqueId())) {
            plugin.console().sendMessage(plugin.format(
                    "&e[MineShark] &fPlayer &b{} &finteract in Setup Mode!".replace("{}", player.getName())));
            plugin.handler.addLocation(player, plugin.handler.inSetup.get(player.getUniqueId()),
                    e.getClickedBlock().getLocation());
        }else if(player.getInventory().getItemInMainHand().equals(tool2)
                && plugin.handler.inSetup.containsKey(player.getUniqueId())) {
            plugin.console().sendMessage(plugin.format(
                    "&e[MineShark] &fPlayer &b{} &finteract in Setup Mode!".replace("{}", player.getName())));
        }

    }

    @EventHandler
    public void onChest(@NotNull InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

        if(!e.getInventory().getType().equals(InventoryType.CHEST)) return;

        List<String> effects = plugin.configuration.getConfig().getStringList("game.chest-open.effect");

        if(!(effects.size() >= 1)) return;

        for(String effect : effects) {
            String[] data = effect.replace(" ", "")
                    .split(",");

            PotionEffect finalEffect = new PotionEffect(
                    PotionEffectType.getByName(data[0]),
                    Integer.valueOf(data[1])*20,
                    Integer.valueOf(data[2])
            );

            player.addPotionEffect(finalEffect);
        }

    }
}
