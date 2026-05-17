package com.y2devteam.regionaction.region;

import com.y2devteam.regionaction.RegionAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class RegionVisualizer {

    private static final Map<UUID, Integer> taskMap = new HashMap<>();
    private static final Map<UUID, Long> lastAction = new HashMap<>();
    private static final Set<UUID> editing = new HashSet<>();

    private static final long TIMEOUT = 5 * 60 * 1000;

    public static void start(Player player, Location pos1, Location pos2) {
        UUID uuid = player.getUniqueId();
        RegionAction plugin = RegionAction.getInstance();

        stop(player);

        editing.add(uuid);
        lastAction.put(uuid, System.currentTimeMillis());

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            if (!player.isOnline() || !editing.contains(uuid)) {
                stop(player);
                return;
            }

            long last = lastAction.getOrDefault(uuid, 0L);
            if (System.currentTimeMillis() - last >= TIMEOUT) {
                stop(player);
                return;
            }
            showOutlineFromLocations(pos1, pos2);

        }, 0L, 10L); // 每 0.5 秒顯示一次

        taskMap.put(uuid, taskId);
    }
    public static void stop(Player player) {
        UUID uuid = player.getUniqueId();

        Integer taskId = taskMap.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        lastAction.remove(uuid);
        editing.remove(uuid);

        RegionAction.getInstance().getWandManager().removeAllWands(player);

        if (player.isOnline()) {
        }
    }
    public static void updateAction(Player player) {
        if (editing.contains(player.getUniqueId())) {
            lastAction.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public static void showOutline(Region region) {
        showOutlineFromCoords(
                region.getWorld(),
                region.getMinX(), region.getMinY(), region.getMinZ(),
                region.getMaxX(), region.getMaxY(), region.getMaxZ()
        );
    }

    public static void showOutlineFromLocations(Location pos1, Location pos2) {

        if (pos1 == null || pos2 == null) return;
        if (!pos1.getWorld().equals(pos2.getWorld())) return;

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        showOutlineFromCoords(pos1.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static void showOutlineFromCoords(World world,
                                              int minX, int minY, int minZ,
                                              int maxX, int maxY, int maxZ) {

        for (int x = minX; x <= maxX; x++) {
            spawn(world, x, minY, minZ);
            spawn(world, x, minY, maxZ);
            spawn(world, x, maxY, minZ);
            spawn(world, x, maxY, maxZ);
        }

        for (int y = minY; y <= maxY; y++) {
            spawn(world, minX, y, minZ);
            spawn(world, minX, y, maxZ);
            spawn(world, maxX, y, minZ);
            spawn(world, maxX, y, maxZ);
        }

        for (int z = minZ; z <= maxZ; z++) {
            spawn(world, minX, minY, z);
            spawn(world, maxX, minY, z);
            spawn(world, minX, maxY, z);
            spawn(world, maxX, maxY, z);
        }
    }
    private static void spawn(World world, double x, double y, double z) {
        world.spawnParticle(
                Particle.END_ROD,
                x + 0.5,
                y + 0.5,
                z + 0.5,
                2,
                0,
                0,
                0,
                0
        );
    }
    public static boolean isEditing(Player player) {
        return editing.contains(player.getUniqueId());
    }
}
