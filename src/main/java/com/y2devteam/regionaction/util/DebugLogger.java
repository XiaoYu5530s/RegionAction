package com.y2devteam.regionaction.util;

import org.bukkit.Bukkit;

public class DebugLogger {

    private static boolean debug = false;
    public static void toggle() {
        debug = !debug;
        Bukkit.getConsoleSender().sendMessage(
                "§6[RAC] Debug 模式: " + (debug ? "§a開啟" : "§c關閉")
        );
    }
    public static void setDebug(boolean value) {
        debug = value;
    }
    public static boolean isDebug() {
        return debug;
    }
    public static void log(String message) {
        if (!debug) return;

        Bukkit.getConsoleSender().sendMessage("§7[RAC-DEBUG] §f" + message);
    }
}
