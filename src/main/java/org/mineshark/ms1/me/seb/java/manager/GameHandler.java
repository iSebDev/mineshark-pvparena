package org.mineshark.ms1.me.seb.java.manager;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mineshark.ms1.me.seb.java.MineShark1;
import org.mineshark.ms1.me.seb.java.commands.AdminCommand;
import org.mineshark.ms1.me.seb.java.commands.PlayerCommands;
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

    public final List<UUID> invulnerables = new ArrayList<>();

    public final Map<String, List<String>> locations = new HashMap<>();

    private final Map<UUID, Inventory> cache = new HashMap<>();

    public final Map<UUID, String> inSetup = new HashMap<>();

    public GameHandler(MineShark1 instance) {
        plugin = instance;

        plugin.getCommand("minesharkpvp").setExecutor(new AdminCommand(plugin));

        plugin.getCommand("join").setExecutor(new PlayerCommands(plugin));
        plugin.getCommand("leave").setExecutor(new PlayerCommands(plugin));

        Bukkit.getServer().getPluginManager().registerEvents(new Interact(plugin), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ChestManager(plugin), plugin);
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

            ItemStack tool2 = new ItemStack(Material.STICK);

            ItemMeta meta2 = tool.getItemMeta();

            meta2.setDisplayName(plugin.format("&eChests Tool"));

            tool2.setItemMeta(meta);

            player.getInventory().addItem(tool);

            GameMode gm = player.getGameMode();

            player.setGameMode(GameMode.CREATIVE);

            World world = Bukkit.getWorld(plugin.getData().getString(id+".world"));

            Location location = world.getSpawnLocation();

            location.setY(location.getY() + 100);
            player.setFlying(false);

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

        if(plugin.configuration.getConfig().getString("spawn") == null) return;

        teleportSpawn(player);
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

        return new Location(world, x, y ,z);
    }

    public void joinPlayer(Player player, String id) {
        if(isInSetup(player.getUniqueId())) {
            setupLeave(player);
        }

        if(plugin.getData().get(id+".locations") == null) {
            player.sendMessage(plugin.format("&7(!) &cError al intentar entrar al juego, contacta con el administrador!"));
        }

        if(!locations.containsKey(id)) return;

        if(players.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.format("&7(!) &cYa estas en un juego !"));
        }else {

            players.put(player.getUniqueId(), id);
            Location location = null;
            if(plugin.configuration.getConfig().getBoolean("game.ran-spawn-loc")) {

                location = randomLocation(id);

            }

            player.teleport(location);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

            player.setGameMode(GameMode.SURVIVAL);

            for(String i : plugin.configuration.getConfig().getStringList("messages.join")) {
                player.sendMessage(plugin.format(i));
            }

            invulnerables.add(player.getUniqueId());

            player.setNoDamageTicks(20*plugin.configuration.getConfig().getInt("game.safe-time"));
            /*
            player.setNoDamageTicks(20*plugin.configuration.getConfig().getInt("game.safe-time"));
            sendActionBar(player, "&aTienes unos segundos de invulnerabilidad!");
            */

            SafeCountdown countdown = new SafeCountdown(plugin, player, plugin.configuration.getConfig().getInt("game.safe-time"));

            countdown.start();
        }
    }

    public void leavePlayer(Player player) {
        players.remove(player.getUniqueId());
        for(String i : plugin.configuration.getConfig().getStringList("messages.leave")) {
            player.sendMessage(plugin.format(i));
        }
    }

    public void setSpawn(Player player) {
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
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

        player.sendMessage(plugin.format("&7(!) &aSpawn seteado correctamente !"));

    }

    public void teleportSpawn(Player player) {
        if(plugin.configuration.getConfig().getString("spawn.world") == null) return;
        World world = Bukkit.getServer().getWorld(
                plugin.configuration.getConfig().getString("spawn.world")
        );
        double x = plugin.configuration.getConfig().getDouble("spawn.x" );
        double y = plugin.configuration.getConfig().getDouble("spawn.y");
        double z = plugin.configuration.getConfig().getDouble("spawn.z");
        float yaw = plugin.configuration.getConfig().getInt("spawn.yaw");
        float pitch = plugin.configuration.getConfig().getInt("spawn.pitch");

        Location location = new Location(world, x, y, z);

        location.setYaw(yaw);

        location.setPitch(pitch);

        player.teleport(location);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
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

        if(!players.containsValue(player.getUniqueId())) return;

        if(plugin.configuration.getConfig().getBoolean("death.summon-lightning")) {

            player.getWorld().strikeLightning(player.getLocation());

        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e) {

        Player player = e.getPlayer();

        if(!players.containsKey(player.getUniqueId())) return;

        for (String i : plugin.configuration.getConfig()
                .getStringList("death.console-execute")) {
            //Bukkit.dispatchCommand(plugin.console(), i.replace("%p", player.getName()));
            Bukkit.getServer().dispatchCommand(plugin.console(), i.replace("%p", player.getName()));
        }

        for (String i : plugin.configuration.getConfig()
                .getStringList("death.player-execute")) {
            Bukkit.getServer().dispatchCommand(player, i.replace("%p", player.getName()));
        }

        leavePlayer(player);

        teleportSpawn(player);

    }

    /*

    =================================              REGLAS              =================================

        Cuando el jugador está jugando y otro lo golpea, y no esta jugando se cancelara el evento!
            When the player is playing and another hits him, and he is not playing,
            the event will be cancelled!

        Cuando el jugador es invulnerable no puede ser golpeado ni puede golpear a los demas!
            When the player is invulnerable they cannot be hit nor can they hit others!

     */
    @EventHandler
    public void takeDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player
                && e.getDamager() instanceof Player) {

            Player player = (Player) e.getEntity();

            Player damager = (Player) e.getEntity();

            if(players.containsKey(player.getUniqueId())
                    && players.containsKey(damager.getUniqueId())) {

                if (!players.get(player.getUniqueId())
                        .equals(players.get(damager.getUniqueId()))) return;

                if (invulnerables.contains(damager.getUniqueId())
                        || invulnerables.contains(player.getUniqueId())) {
                    e.setCancelled(true);
                }
            }else if (!players.containsKey(damager.getUniqueId())
                    && players.containsKey(player.getUniqueId())) {
                e.setCancelled(true);
            }else {
                return;
            }
        }
    }
}

