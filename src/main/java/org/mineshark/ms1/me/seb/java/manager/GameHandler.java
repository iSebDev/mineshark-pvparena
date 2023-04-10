package org.mineshark.ms1.me.seb.java.manager;

import com.sun.istack.internal.NotNull;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.mineshark.ms1.me.seb.java.MineShark1;

import java.util.*;

public class GameHandler {
    /*
    Setup Game Handler

    arenas: Id, Locations List, Player List

    Save Arena Method
     */

    private static MineShark1 plugin;
    public final Map<String, UUID[]> arenas = new HashMap<>();

    public final Map<String, List<String>> locations = new HashMap<>();

    private final Map<UUID, Inventory> cache = new HashMap<>();

    public final Map<UUID, String> inSetup = new HashMap<>();

    public GameHandler(MineShark1 instance) {
        plugin = instance;
    }

    public void addLocation(Player player, String id, Location location) {
        if(plugin.getData().getString(id) == null) {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cno existe !".replace("{0}", id)));
        }else {

            plugin.getData().set(id+".world", location.getWorld().getName());

            List<String> stringList = new ArrayList<>();

            if(plugin.getData().getStringList(id+".locations") == null) {
                String loc = "{x},{y},{z}";
                for(int i=0; i!=2; i++) {
                    if(i != 0) {
                        loc = loc.replace(" ", "");
                        loc = loc.replace("{x}", String.valueOf(location.getX()));
                        loc = loc.replace("{y}", String.valueOf(location.getY()+1));
                        loc = loc.replace("{z}", String.valueOf(location.getZ()));
                    }
                }
                stringList.add(loc);
            }else {
                stringList = plugin.getData().getStringList(id+".locations");
                String loc = "{x},{y},{z}";
                for(int i=0; i!=2; i++) {
                    if(i != 0) {
                        loc = loc.replace(" ", "");
                        loc = loc.replace("{x}", String.valueOf(location.getX()));
                        loc = loc.replace("{y}", String.valueOf(location.getY()+1));
                        loc = loc.replace("{z}", String.valueOf(location.getZ()));
                    }
                }
                stringList.add(loc);
            }

            plugin.getData().set(id+".locations", stringList);

            player.sendMessage(plugin.format("&7(!) &aSe añadió un lugar de aparición en juego &e{0} &a!".replace("{0}", id)));
        }

        plugin.updateFiles();
    }

    public void createArena(Player player, String id) {
        if(plugin.getData().getString(id) == null) {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cno existe !".replace("{0}", id)));
        } else {
            plugin.getData().set(id+".world", "world");
        }

        plugin.updateFiles();
    }

    public void removeArena(Player player, String id) {
        if(plugin.getData().getString(id) == null) {
            player.sendMessage(plugin.format("&7(!) &cEl juego &b{0} &cno existe !".replace("{0}", id)));
        } else {
            plugin.getData().set(id, null);
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
            Inventory inv = player.getInventory();

            player.getInventory().clear();

            GameMode gm = player.getGameMode();

            cache.put(player.getUniqueId(), inv);

            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public void setupLeave(Player player) {
        if(!isInSetup(player.getUniqueId())) return;
        inSetup.remove(player.getUniqueId());

        player.getInventory().clear();

        player.getInventory().setContents(cache.get(player.getUniqueId()).getContents());
    }
}
