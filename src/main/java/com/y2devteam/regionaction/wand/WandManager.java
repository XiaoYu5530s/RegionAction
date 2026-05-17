package com.y2devteam.regionaction.wand;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.util.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WandManager {

    private final RegionAction plugin;
    private final NamespacedKey wandKey;

    public WandManager(RegionAction plugin) {
        this.plugin = plugin;
        this.wandKey = new NamespacedKey(plugin, "region_wand");
    }

    public ItemStack createWand() {

        Material material = plugin.getConfigManager().getWandMaterial();

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        String name = plugin.getConfig().getString("wand.name", "&6&l區域劃線工具");
        meta.setDisplayName(color(name));

        List<String> lore = new ArrayList<>();

        if (plugin.getConfig().contains("wand.lore")) {
            for (String line : plugin.getConfig().getStringList("wand.lore")) {
                lore.add(color(line));
            }
        }

        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        meta.getPersistentDataContainer().set(wandKey, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    public void giveWand(Player player) {

        if (player == null) return;

        ItemStack wand = createWand();
        player.getInventory().addItem(wand);

        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§a[RAC] 已給予 " + player.getName() + " 劃線工具"
            );
        }
    }

    public boolean isWand(ItemStack item) {

        if (item == null) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();

        return meta.getPersistentDataContainer().has(wandKey, PersistentDataType.INTEGER);
    }

    public void removeAllWands(Player player) {
        if (player == null) return;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;
            if (isWand(item)) {
                player.getInventory().setItem(i, null);
            }
        }
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§cDEBUG 已掃描並移除" + player.getName() + "的wand"
            );
        }
    }
    private String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
