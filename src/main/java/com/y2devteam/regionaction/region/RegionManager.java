package com.y2devteam.regionaction.region;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.util.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class RegionManager {
    private final RegionAction plugin;
    private final Map<String, Region> regionMap = new HashMap<>();

    public RegionManager(RegionAction plugin) {
        this.plugin = plugin;
    }

    public boolean createRegion(String id, Location pos1, Location pos2) {
        if (id == null || id.isEmpty()) return false;
        if (pos1 == null || pos2 == null) return false;
        if (!pos1.getWorld().equals(pos2.getWorld())) return false;
        id = id.toLowerCase();
        if (regionMap.containsKey(id)) {
            return false;
        }

        Region region = new Region(id, pos1, pos2);
        regionMap.put(id, region);
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§a[RAC] 已建立區域: " + id);
        }
        return true;
    }

    public boolean deleteRegion(String id) {
        if (id == null) return false;
        id = id.toLowerCase();
        if (!regionMap.containsKey(id)) {
            return false;
        }

        regionMap.remove(id);
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§c[RAC] 已刪除區域: " + id);
        }
        return true;
    }

    public Region getRegion(String id) {
        if (id == null) return null;
        return regionMap.get(id.toLowerCase());
    }

    public boolean exists(String id) {
        if (id == null) return false;
        return regionMap.containsKey(id.toLowerCase());
    }

    public Collection<Region> getAllRegions() {
        return Collections.unmodifiableCollection(regionMap.values());
    }

    public void clear() {
        regionMap.clear();
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§e[RAC] 所有區域已清空");
        }
    }

    public void loadRegion(Region region) {
        if (region == null) return;
        regionMap.put(region.getId().toLowerCase(), region);
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§b[RAC] 載入區域: " + region.getId()
            );
        }
    }

    public List<String> getRegionIds() {
        return new ArrayList<>(regionMap.keySet());
    }

    public void printRegions() {
        Bukkit.getConsoleSender().sendMessage("§f§l§m━━━━━§7 [§6所有已設定的區域§7] §f§l§m━━━━━");
        if (regionMap.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§c目前沒有任何已設置好的區域");
            return;
        }
        for (Region region : regionMap.values()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§f" + region.getId() +
                            " §7(所在世界: §f" + region.getWorldName() +
                            " §7| A點位: §f" +
                            region.getMinX() + "," + region.getMinY() + "," + region.getMinZ() +
                            " §7| B點位: §f" +
                            region.getMaxX() + "," + region.getMaxY() + "," + region.getMaxZ() +
                            "§7)"
            );
        }
    }
}

