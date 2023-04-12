package org.mineshark.ms1.me.seb.java.manager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mineshark.ms1.me.seb.java.MineShark1;
import org.mineshark.ms1.me.seb.java.commands.AdminCommand;
import org.mineshark.ms1.me.seb.java.events.Interact;

import java.util.*;

public class GameHandler {
    /*
    Setup Game Handler

    arenas: Id, Locations List, Player List

    Save Arena Method
     */

    private static MineShark1 plugin;
    public final Map<String, Map<UUID, Long>> arenas = new HashMap<>();

    public final Map<UUID, String> players = new HashMap<>();

    public final Map<String, List<String>> locations = new HashMap<>();

    private final Map<UUID, Inventory> cache = new HashMap<>();

    public final Map<UUID, String> inSetup = new HashMap<>();

    public GameHandler(MineShark1 instance) {
        plugin = instance;

        plugin.getCommand("minesharkarena").setExecutor(new AdminCommand(plugin));

        Bukkit.getServer().getPluginManager().registerEvents(new Interact(plugin), plugin);
    }

    public void addLocation(Player player, String id, Location location) {
        if(plugin.getData().getString(id) == null) {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cno existe !".replace("{0}", id)));
        }else {

            plugin.getData().set(id+".world", location.getWorld().getName());

            List<String> stringList = new ArrayList<>();

            if(plugin.getData().get(id+".locations") == null) {
                plugin.getData().set(id+".locations", stringList);
            }

            plugin.updateFiles();

            stringList = plugin.getData().getStringList(id+".locations");
            String loc = "{x},{y},{z}";
            for(int i=0; i!=1; i++) {
                loc = loc.replace(" ", "");
                loc = loc.replace("{x}", String.valueOf(location.getX()));
                loc = loc.replace("{y}", String.valueOf(location.getY()+1));
                loc = loc.replace("{z}", String.valueOf(location.getZ()));
            }
            if(stringList.contains(loc)) {
                player.sendMessage(plugin.format("&7(!) &cYa hay un lugar puesto aqui &c!"));
            }else {
                stringList.add(loc);
            }

            plugin.getData().set(id+".locations", stringList);

            player.sendMessage(plugin.format("&7(!) &aSe añadió un lugar de aparición en juego &e{0} &a!".replace("{0}", id)));
        }

        plugin.updateFiles();
    }

    public void createArena(Player player, String id) {
        if(plugin.getData().getString(id) == null) {
            plugin.getData().set(id+".world", "world");
            player.sendMessage(plugin.format("&7(!) &aCreaste un nuevo juego."));
        } else {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cya existe !".replace("{0}", id)));
        }

        plugin.updateFiles();
    }

    public void removeArena(Player player, String id) {
        if(plugin.getData().getString(id) == null) {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cno existe !".replace("{0}", id)));
        } else {
            plugin.getData().set(id, null);
            player.sendMessage(plugin.format("&7(!) &aEl juego &b{0} &afue eliminado !".replace("{0}", id)));
        }

        plugin.updateFiles();
    }

    @NotNull
    public boolean isInSetup(UUID uuid) {
        if(this.inSetup.containsKey(uuid)) return true;
        return false;
    }

    public void setupArena(Player player, String id) {
        if(plugin.getData().getString(id) == null) {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cno existe !".replace("{0}", id)));
        } else {
            if(isInSetup(player.getUniqueId())) return;

            inSetup.put(player.getUniqueId(), id);
            Inventory inv = player.getInventory();
            cache.put(player.getUniqueId(), inv);

            player.getInventory().clear();

            ItemStack tool = new ItemStack(Material.BLAZE_ROD);

            ItemMeta meta = tool.getItemMeta();

            meta.setDisplayName(plugin.format("&eLocations Tool"));

            tool.setItemMeta(meta);

            player.getInventory().addItem(tool);

            GameMode gm = player.getGameMode();


            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(plugin.format("&7(!) &aEntraste al modo setup"));
        }
    }

    public void setupLeave(Player player) {
        if(!isInSetup(player.getUniqueId())) return;
        inSetup.remove(player.getUniqueId());

        player.getInventory().setContents(cache.get(player.getUniqueId()).getContents());
        cache.remove(player.getUniqueId());
        player.sendMessage(plugin.format("&7(!) &aSaliste del modo setup"));
    }
}
