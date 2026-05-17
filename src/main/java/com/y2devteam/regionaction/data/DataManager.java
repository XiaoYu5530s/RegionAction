package com.y2devteam.regionaction.data;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.region.Region;
import com.y2devteam.regionaction.region.RegionManager;
import com.y2devteam.regionaction.util.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

public class DataManager {

    private final RegionAction plugin;
    private final RegionManager regionManager;

    private File dataFolder;
    private File saveFile;
    private YamlConfiguration saveConfig;

    public DataManager(RegionAction plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
        setup();
    }

    private void setup() {

        dataFolder = new File(plugin.getDataFolder(), "data");

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        saveFile = new File(dataFolder, "save.yml");

        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        saveConfig = YamlConfiguration.loadConfiguration(saveFile);
    }

    // ============================================================
    // 加載區域
    // ============================================================
    public void loadRegions() {

        regionManager.clear();

        for (String key : saveConfig.getKeys(false)) {

            ConfigurationSection section = saveConfig.getConfigurationSection(key);
            if (section == null) continue;

            Region region = SaveData.loadRegion(key, section);
            if (region != null) {
                regionManager.loadRegion(region);
            }
        }

        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§a[RAC] 已載入區域數量: " + regionManager.getAllRegions().size());
        }
    }

    // ============================================================
    // ⭐ 儲存區域 — 使用 CommentedRegionWriter（你要的格式）
    // ============================================================
    public void saveRegions() {

        try {

            // 重新讀取原 save.yml（保留手動區域）
            YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(saveFile);

            // 收集 key（手動 + plugin）
            Set<String> keys = new HashSet<>(oldConfig.getKeys(false));
            for (Region region : regionManager.getAllRegions()) {
                keys.add(region.getId());
            }

            // ⭐ 自然排序（a1, a2, a10）
            List<String> sortedKeys = new ArrayList<>(keys);
            sortedKeys.sort((a, b) -> {
                int na = extractNumber(a);
                int nb = extractNumber(b);
                return (na == nb) ? a.compareTo(b) : Integer.compare(na, nb);
            });

            // ⭐ 清空原本檔案 & 寫入自訂格式（包含中文註解）
            CommentedRegionWriter.writeHeader(saveFile);

            // 一個一個 key 寫回去
            for (String id : sortedKeys) {

                Region region = regionManager.getRegion(id);

                if (region != null) {
                    // Plugin 管理的區域 → 用註解格式輸出
                    CommentedRegionWriter.writeRegionWithComments(saveFile, region);
                    continue;
                }

                // 手動區域 → 保留舊設定（也輸出註解格式）
                ConfigurationSection old = oldConfig.getConfigurationSection(id);
                if (old != null) {
                    Region manual = SaveData.loadRegion(id, old);
                    if (manual != null) {
                        CommentedRegionWriter.writeRegionWithComments(saveFile, manual);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§b[RAC] save.yml（含註解）已保存（手動區域永久保留）");
        }
    }

    // 數字抽取器（a1 → 1）
    private int extractNumber(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public void reload() {
        saveConfig = YamlConfiguration.loadConfiguration(saveFile);
        loadRegions();
    }

    public File getSaveFile() {
        return saveFile;
    }

    public YamlConfiguration getSaveConfig() {
        return saveConfig;
    }
}
