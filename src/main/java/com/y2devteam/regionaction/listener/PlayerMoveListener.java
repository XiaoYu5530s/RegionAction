package com.y2devteam.regionaction.listener;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.region.RegionChecker;
import com.y2devteam.regionaction.util.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.y2devteam.regionaction.region.RegionVisualizer;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerMoveListener implements Listener {

    private final RegionAction plugin;
    private final RegionChecker regionChecker;

    public PlayerMoveListener(RegionAction plugin, RegionChecker regionChecker) {
        this.plugin = plugin;
        this.regionChecker = regionChecker;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = event.getPlayer();
            if (!player.isOnline()) return;
            if (!player.hasPermission("regionaction.use")) return;
            // ✅ 強制清空上次區域
            plugin.getRegionChecker().clearPlayer(player);
            plugin.getRegionChecker().checkPlayer(
                    player,
                    player.getLocation()
            );
        }, 40L); // 建議 40 tick (2 秒)
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (event.getTo() == null) return;
        if (!event.getPlayer().hasPermission("regionaction.use")) {
            return;
        }
        RegionVisualizer.updateAction(event.getPlayer());
        regionChecker.checkPlayer(event.getPlayer(), event.getTo());
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§6[RAC-DEBUG] §7Move Detected: §a" + event.getPlayer().getName()
                            + " §7-> X:" + event.getTo().getBlockX()
                            + " Y:" + event.getTo().getBlockY()
                            + " Z:" + event.getTo().getBlockZ()
            );
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getWandManager().removeAllWands(event.getPlayer());

        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§6[RAC-DEBUG] §7Removed all wands from " + event.getPlayer().getName()
            );
        }
    }
}

