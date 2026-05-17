package com.y2devteam.regionaction.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private final String id;
    private final String worldName;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final int priority; // ✅ 區域權重
    private String enterTitle;
    private String enterSubtitle;
    private String leaveTitle;
    private String leaveSubtitle;
    private List<String> enterCommands;
    private List<String> leaveCommands;
    private final String sound;
    private final int soundLength;
    private final boolean loopSound;
    private final int soundDelay;

    // 指令 / 選取建立用（預設權重 = 0）
    public Region(String id, Location pos1, Location pos2) {
        this.id = id;
        this.worldName = pos1.getWorld().getName();

        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        this.enterTitle = "";
        this.enterSubtitle = "";
        this.leaveTitle = "";
        this.leaveSubtitle = "";

        this.enterCommands = new ArrayList<>();
        this.leaveCommands = new ArrayList<>();

        this.sound = "";
        this.soundLength = 30;
        this.loopSound = false;
        this.soundDelay = 0;

        this.priority = 0; // ✅ 預設權重
    }

    // 從 config 讀取用（可自訂權重）
    public Region(String id, String worldName,
                  int minX, int minY, int minZ,
                  int maxX, int maxY, int maxZ,
                  String enterTitle, String enterSubtitle,
                  String leaveTitle, String leaveSubtitle,
                  List<String> enterCommands,
                  List<String> leaveCommands,
                  String sound,
                  int soundLength,
                  boolean loopSound,
                  int soundDelay,
                  int priority) {

        this.id = id;
        this.worldName = worldName;

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.enterTitle = enterTitle;
        this.enterSubtitle = enterSubtitle;
        this.leaveTitle = leaveTitle;
        this.leaveSubtitle = leaveSubtitle;

        this.enterCommands = enterCommands != null ? enterCommands : new ArrayList<>();
        this.leaveCommands = leaveCommands != null ? leaveCommands : new ArrayList<>();

        this.sound = sound != null ? sound : "";
        this.soundLength = soundLength;
        this.loopSound = loopSound;
        this.soundDelay = soundDelay;

        this.priority = priority; // ✅ 設定權重
    }

    public boolean isInside(Location location) {
        if (location == null || location.getWorld() == null) return false;
        if (!location.getWorld().getName().equals(worldName)) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public String getId() {
        return id;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getPriority() { // ✅ 給 RegionChecker 用
        return priority;
    }

    public String getEnterTitle() {
        return enterTitle;
    }

    public String getEnterSubtitle() {
        return enterSubtitle;
    }

    public String getLeaveTitle() {
        return leaveTitle;
    }

    public String getLeaveSubtitle() {
        return leaveSubtitle;
    }

    public List<String> getEnterCommands() {
        return enterCommands;
    }

    public List<String> getLeaveCommands() {
        return leaveCommands;
    }

    public String getSound() {
        return sound;
    }

    public int getSoundLength() {
        return soundLength;
    }

    public boolean isLoopSound() {
        return loopSound;
    }

    public int getSoundDelay() {
        return soundDelay;
    }

    public Location getCenter() {

        World world = getWorld();
        if (world == null) return null;

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double centerZ = (minZ + maxZ) / 2.0;

        return new Location(world, centerX, centerY, centerZ);
    }

    public double getMaxDistanceFromCenter() {

        Location center = getCenter();
        if (center == null) return 1.0;

        Location corner = new Location(getWorld(), minX, minY, minZ);
        return center.distance(corner);
    }

    public void saveToConfig(ConfigurationSection section) {

        section.set("world", worldName);

        section.set("pos1.x", minX);
        section.set("pos1.y", minY);
        section.set("pos1.z", minZ);

        section.set("pos2.x", maxX);
        section.set("pos2.y", maxY);
        section.set("pos2.z", maxZ);

        section.set("enter.title", enterTitle);
        section.set("enter.subtitle", enterSubtitle);
        section.set("enter.commands", enterCommands);

        section.set("leave.title", leaveTitle);
        section.set("leave.subtitle", leaveSubtitle);
        section.set("leave.commands", leaveCommands);

        section.set("sound", sound);

        section.set("sound-length", soundLength);
        section.set("sound-loop", loopSound);
        section.set("sound-delay", soundDelay);

        section.set("priority", priority);
    }
}
