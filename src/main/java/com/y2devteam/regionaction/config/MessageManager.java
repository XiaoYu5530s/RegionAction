package com.y2devteam.regionaction.config;

import com.y2devteam.regionaction.RegionAction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MessageManager {

    private final RegionAction plugin;
    private File file;
    private YamlConfiguration config;

    public MessageManager(RegionAction plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {

        file = new File(plugin.getDataFolder(), "message.yml");

        if (!file.exists()) {
            plugin.saveResource("message.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getPrefix() {
        return color(config.getString("prefix", "&6[RegionAction] "));
    }

    public String getMessage(String path) {
        return color(config.getString(path, "&c訊息不存在: " + path));
    }

    public String getMessage(String path, String key, String value) {
        return color(getMessage(path).replace(key, value));
    }

    public void send(Player player, String path) {
        player.sendMessage(getPrefix() + getMessage(path));
    }

    public void send(Player player, String path, String key, String value) {
        player.sendMessage(getPrefix() + getMessage(path, key, value));
    }

    private String color(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }
}
