package com.y2devteam.regionaction.config;

import com.y2devteam.regionaction.RegionAction;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final RegionAction plugin;
    private FileConfiguration config;

    public ConfigManager(RegionAction plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // =============================
    // Wand
    // =============================

    public Material getWandMaterial() {
        String material = config.getString("wand.material", "BLAZE_ROD");
        Material mat = Material.matchMaterial(material.toUpperCase());

        return mat == null ? Material.BLAZE_ROD : mat;
    }

    // =============================
    // Title 時間
    // =============================

    public int getTitleFadeIn() {
        return config.getInt("title.fade-in", 10);
    }

    public int getTitleStay() {
        return config.getInt("title.stay", 40);
    }

    public int getTitleFadeOut() {
        return config.getInt("title.fade-out", 10);
    }

    // =============================
    // Debug
    // =============================

    public boolean isDebugEnabled() {
        return config.getBoolean("debug", false);
    }
}
