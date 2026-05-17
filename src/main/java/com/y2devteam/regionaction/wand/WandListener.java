package com.y2devteam.regionaction.wand;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.region.RegionVisualizer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WandListener implements Listener {

    private final RegionAction plugin;

    private final Map<UUID, Location> pos1Map = new HashMap<>();
    private final Map<UUID, Location> pos2Map = new HashMap<>();
    private final Map<UUID, BukkitTask> visualizeTasks = new HashMap<>();
    private final Map<UUID, Long> lastAction = new HashMap<>();

    private static final long TIMEOUT = 5 * 60 * 1000; // 5分鐘

    public WandListener(RegionAction plugin) {
        this.plugin = plugin;
    }

    // =======================================================
    // 玩家點擊（設定 A/B 點）
    // =======================================================
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() == null) return;
        if (!plugin.getWandManager().isWand(event.getItem())) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Action action = event.getAction();

        // 右鍵取消（避免打開箱子/門等）
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);
        }

        // 嘗試取得目標方塊
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            clicked = player.getTargetBlockExact(6);
        }
        if (clicked == null) return;

        Location loc = clicked.getLocation();
        lastAction.put(uuid, System.currentTimeMillis());

        String locText = loc.getBlockX() + ", "
                + loc.getBlockY() + ", "
                + loc.getBlockZ();

        // 左鍵 → pos1
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {

            pos1Map.put(uuid, loc);
            plugin.getMessageManager().send(player, "pos1-set", "%location%", locText);

            startVisualizing(player);
            showPreviewIfReady(player);
            return;
        }

        // 右鍵 → pos2
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {

            pos2Map.put(uuid, loc);
            plugin.getMessageManager().send(player, "pos2-set", "%location%", locText);

            showPreviewIfReady(player);
        }
    }

    // =======================================================
    // 禁止 wand 拆方塊
    // =======================================================
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // 主手使用 wand
        if (plugin.getWandManager().isWand(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            return;
        }

        // 副手使用 wand
        if (plugin.getWandManager().isWand(player.getInventory().getItemInOffHand())) {
            event.setCancelled(true);
        }
    }

    // =======================================================
    // 顯示預覽框架（A B 都有時）
    // =======================================================
    private void showPreviewIfReady(Player player) {
        UUID uuid = player.getUniqueId();

        Location pos1 = pos1Map.get(uuid);
        Location pos2 = pos2Map.get(uuid);

        if (pos1 != null && pos2 != null) {
            RegionVisualizer.showOutlineFromLocations(pos1, pos2);
        }
    }

    // =======================================================
    // 持續顯示粒子邊框
    // =======================================================
    private void startVisualizing(Player player) {
        UUID uuid = player.getUniqueId();

        // 取消舊任務
        if (visualizeTasks.containsKey(uuid)) {
            visualizeTasks.get(uuid).cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!player.isOnline()) {
                clearPositions(player);
                return;
            }

            long last = lastAction.getOrDefault(uuid, 0L);
            if (System.currentTimeMillis() - last >= TIMEOUT) {

                clearPositions(player);
                plugin.getMessageManager().send(player, "edit-timeout");
                return;
            }

            Location pos1 = pos1Map.get(uuid);
            Location pos2 = pos2Map.get(uuid);

            if (pos1 == null || pos2 == null) return;
            if (!pos1.getWorld().equals(pos2.getWorld())) return;

            RegionVisualizer.showOutlineFromLocations(pos1, pos2);

        }, 0L, 10L);

        visualizeTasks.put(uuid, task);
    }

    // =======================================================
    // Getter
    // =======================================================
    public boolean hasBothPositions(Player player) {
        UUID uuid = player.getUniqueId();
        return pos1Map.containsKey(uuid) && pos2Map.containsKey(uuid);
    }

    public Location getPos1(Player player) {
        return pos1Map.get(player.getUniqueId());
    }

    public Location getPos2(Player player) {
        return pos2Map.get(player.getUniqueId());
    }

    // =======================================================
    // 清除資料
    // =======================================================
    public void clearPositions(Player player) {

        UUID uuid = player.getUniqueId();

        pos1Map.remove(uuid);
        pos2Map.remove(uuid);
        lastAction.remove(uuid);

        if (visualizeTasks.containsKey(uuid)) {
            visualizeTasks.get(uuid).cancel();
            visualizeTasks.remove(uuid);
        }
    }
}
