package org.mineshark.ms1.me.seb.java.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mineshark.ms1.me.seb.java.MineShark1;
import org.mineshark.ms1.me.seb.java.commands.AdminCommand;
import org.mineshark.ms1.me.seb.java.events.Interact;

import java.util.*;

public class GameHandler implements Listener {
    /*
    Setup Game Handler

    arenas: Id, Locations List, Player List

    Save Arena Method
     */

    private static MineShark1 plugin;
    public final Map<String, Map<UUID, Integer>> arenas = new HashMap<>();

    public final Map<UUID, String> players = new HashMap<>();

    public final Map<String, List<String>> locations = new HashMap<>();

    private final Map<UUID, Inventory> cache = new HashMap<>();

    public final Map<UUID, String> inSetup = new HashMap<>();

    public GameHandler(MineShark1 instance) {
        plugin = instance;

        plugin.getCommand("minesharkarena").setExecutor(new AdminCommand(plugin));

        Bukkit.getServer().getPluginManager().registerEvents(new Interact(plugin), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
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
        if(players.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.format("&7(!) &cTienes que no estar en juego para poder estar en Modo Setup !"));
            return;
        }

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

            World world = Bukkit.getWorld(plugin.getData().getString(id+".world"));

            Location location = world.getSpawnLocation();

            player.teleport(location);

            player.sendMessage(plugin.format("&7(!) &aEntraste al modo setup"));

            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
        }
    }

    public void setupLeave(Player player) {
        if(!isInSetup(player.getUniqueId())) return;
        inSetup.remove(player.getUniqueId());

        player.getInventory().setContents(cache.get(player.getUniqueId()).getContents());
        cache.remove(player.getUniqueId());
        player.sendMessage(plugin.format("&7(!) &aSaliste del modo setup"));

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 1f);
    }

    private Location randomLocation(String arena) {
        Random random = new Random();
        double x;
        double y;
        double z;

        World world = Bukkit.getServer().getWorld(
            plugin.getData().getString(arena+".world")
        );

        List<String> locs = locations.get(arena);
        String nextLoc = locs.get(
                random.nextInt(locs.size())
        );

        String[] finalLocString = nextLoc.split(",");

        x = Double.parseDouble(finalLocString[0]);
        y = Double.parseDouble(finalLocString[1]);
        z = Double.parseDouble(finalLocString[2]);

        Location finalLocation = new Location(world, x, y ,z);

        return finalLocation;
    }

    private static void sendActionBar(Player player, String string) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.format(string)));
    }

    public void joinPlayer(Player player, String id) {
        if(isInSetup(player.getUniqueId())) {
            setupLeave(player);
        }

        if(!locations.containsKey(id)) return;

        if(players.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.format("&7(!) &cYa estas en un juego !"));
        }else {

            players.put(player.getUniqueId(), id);
            Location location = null;
            if(plugin.configuration.getConfig().getBoolean("game.ran-spawn-loc")) {

                location = randomLocation(id);

            }else {

                plugin.console().sendMessage(plugin.format("&e[MineShark] &cStep Safe Locations are indev"));

            }

            player.teleport(location);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

            for(String i : plugin.configuration.getConfig().getStringList("messages.join")) {
                player.sendMessage(plugin.format(i));
            }

            player.setNoDamageTicks(20*plugin.configuration.getConfig().getInt("game.safe-time"));
            sendActionBar(player, "&aTienes unos segundos de invulnerabilidad!");
        }
    }

    public void leavePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public void setSpawn(Player player) {
        double x = player.getLocation().getX();
        double y = player.getLocation().getX();
        double z = player.getLocation().getX();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        World world = player.getLocation().getWorld();

        plugin.configuration.getConfig().set("spawn.world", world.getName());
        plugin.configuration.getConfig().set("spawn.x", x);
        plugin.configuration.getConfig().set("spawn.y", y);
        plugin.configuration.getConfig().set("spawn.z", z);
        plugin.configuration.getConfig().set("spawn.yaw", yaw);
        plugin.configuration.getConfig().set("spawn.pitch", pitch);

        plugin.updateFiles();

        player.sendMessage(plugin.format("&7(!) &aSpawn seteo completado !"));
    }

    public void teleportSpawn(Player player) {
        if(!(plugin.configuration.getConfig().getString("world") == null)) return;
        World world = Bukkit.getServer().getWorld(
                plugin.configuration.getConfig().getString("spawn.world")
        );
        double x = plugin.configuration.getConfig().getDouble("spawn.x" );
        double y = plugin.configuration.getConfig().getDouble("spawn.y");
        double z = plugin.configuration.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.configuration.getConfig().get("spawn.yaw");
        float pitch = (float) plugin.configuration.getConfig().get("spawn.pitch");

        Location location = new Location(world, x, y, z);

        location.setYaw(yaw);

        location.setPitch(pitch);

        player.teleport(location);
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();

        if(players.containsValue(player.getUniqueId())) {

            leavePlayer(player);

        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        if(!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if(players.containsValue(player.getUniqueId())) {

            leavePlayer(player);

        }

        if(plugin.configuration.getConfig().getBoolean("death.strike-lightning")) {

            player.getWorld().strikeLightning(player.getLocation());

        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e) {

        Player player = e.getPlayer();
        for (String i : plugin.configuration.getConfig()
                .getStringList("death.console-execute")) {
            Bukkit.dispatchCommand(plugin.console(), i.replace("%p", player.getName()));
        }

        for (String i : plugin.configuration.getConfig()
                .getStringList("death.player-execute")) {
            Bukkit.dispatchCommand(player, i.replace("%p", player.getName()));
        }
    }
}

