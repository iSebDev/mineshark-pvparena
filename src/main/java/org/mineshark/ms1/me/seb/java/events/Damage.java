package org.mineshark.ms1.me.seb.java.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mineshark.ms1.me.seb.java.MineShark1;

public class Damage implements Listener {
    private static MineShark1 plugin;

    public Damage(MineShark1 instance) {
        plugin = instance;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player
                && e.getDamager() instanceof Player)) return;

        Player target = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        if(plugin.handler.arenas.containsValue(target.getUniqueId())
                && plugin.handler.arenas.containsValue(damager.getUniqueId())) {

        }
    }
}
