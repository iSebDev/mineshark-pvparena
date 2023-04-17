package org.mineshark.ms1.me.seb.java.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineshark.ms1.me.seb.java.MineShark1;

public class SafeCountdown extends BukkitRunnable {
    private Player player;
    private int countdown;

    private static MineShark1 plugin;

    public SafeCountdown(MineShark1 instance, Player player, Integer time) {
        this.player = player;
        this.countdown = time;

        plugin = instance;
    }

    private static void sendActionBar(Player player, String string) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.format(string)));
    }

    @Override
    public void run() {

        countdown--;

        sendActionBar(
                player,
                plugin.configuration.getConfig().
                        getString("messages.safe-actionbar").
                        replace("%time", String.valueOf(countdown))
        );

        if (countdown == 0) {
            this.cancel();
        }
    }

    public void start() {

        this.runTaskTimer(plugin, 0L, 20L);
    }
}
