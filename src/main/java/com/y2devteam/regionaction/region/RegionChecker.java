package com.y2devteam.regionaction.region;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.util.DebugLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionChecker {

    private final RegionAction plugin;
    private final RegionManager regionManager;
    private final Map<UUID, String> playerCurrentRegion = new HashMap<>();
    private final Map<UUID, Float> playerVolume = new HashMap<>();
    private final Map<UUID, Integer> loopTasks = new HashMap<>();

    public RegionChecker(RegionAction plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    public void checkPlayer(Player player, Location to) {
        if (player == null || to == null) return;

        UUID uuid = player.getUniqueId();

        Region nowRegion = null;
        int highestPriority = Integer.MIN_VALUE;

        // ✅ 找出權重最高的區域 (如果同權重 → 範圍小者優先)
        for (Region region : regionManager.getAllRegions()) {
            if (region.isInside(to)) {
                if (nowRegion == null
                        || region.getPriority() > highestPriority
                        || (region.getPriority() == highestPriority
                        && region.getMaxDistanceFromCenter() < nowRegion.getMaxDistanceFromCenter())
                ) {
                    nowRegion = region;
                    highestPriority = region.getPriority();
                }
            }
        }

        if (DebugLogger.isDebug() && nowRegion != null) {
            DebugLogger.log("目前最高權重區域: "
                    + nowRegion.getId()
                    + " (priority=" + nowRegion.getPriority() + ")");
        }

        String lastRegionId = playerCurrentRegion.get(uuid);

        if (nowRegion != null) {
            String newRegionId = nowRegion.getId();

            if (lastRegionId == null) {
                handleEnter(player, nowRegion);
                playerCurrentRegion.put(uuid, newRegionId);
                return;
            }

            if (!lastRegionId.equalsIgnoreCase(newRegionId)) {
                Region lastRegion = regionManager.getRegion(lastRegionId);
                if (lastRegion != null) {
                    handleLeave(player, lastRegion);
                }
                handleEnter(player, nowRegion);
                playerCurrentRegion.put(uuid, newRegionId);
            }
            return;
        }

        if (lastRegionId != null) {
            Region lastRegion = regionManager.getRegion(lastRegionId);
            if (lastRegion != null) {
                handleLeave(player, lastRegion);
            }
            playerCurrentRegion.remove(uuid);
            playerVolume.remove(uuid);
        }
    }

    private void handleEnter(Player player, Region region) {
        if (DebugLogger.isDebug()) {
            DebugLogger.log(player.getName() + " 進入區域: " + region.getId());
        }

        if (!region.getEnterTitle().isEmpty() || !region.getEnterSubtitle().isEmpty()) {
            int fadeIn = plugin.getConfig().getInt("title.fade-in", 10);
            int stay = plugin.getConfig().getInt("title.stay", 40);
            int fadeOut = plugin.getConfig().getInt("title.fade-out", 10);

            player.sendTitle(
                    color(region.getEnterTitle()),
                    color(region.getEnterSubtitle()),
                    fadeIn,
                    stay,
                    fadeOut
            );
        }

        String sound = region.getSound();
        if (sound != null && !sound.isEmpty()) {
            int delaySeconds = region.getSoundDelay();
            long delayTicks = delaySeconds * 20L;

            if (DebugLogger.isDebug()) {
                DebugLogger.log("區域 " + region.getId() + " 設定音效延遲: " + delaySeconds + " 秒");
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                if (!player.isOnline()) return;

                String currentRegion = playerCurrentRegion.get(player.getUniqueId());
                if (currentRegion == null || !currentRegion.equalsIgnoreCase(region.getId())) return;

                if (!region.isInside(player.getLocation())) return;

                player.stopSound(sound, SoundCategory.MASTER);

                float volume = calculateVolume(player, region);
                playerVolume.put(player.getUniqueId(), volume);

                player.playSound(
                        player.getLocation(),
                        sound,
                        SoundCategory.MASTER,
                        volume,
                        1.0f
                );

                if (DebugLogger.isDebug()) {
                    DebugLogger.log("✅ 延遲播放音效: " + sound + " | 區域: " + region.getId() + " | 音量: " + volume);
                }

                if (region.isLoopSound() && region.getSoundLength() > 0) {
                    startLoopTask(player, region);
                }

            }, delayTicks);
        }

        executeCommands(player, region.getEnterCommands());
    }

    private void handleLeave(Player player, Region region) {

        if (DebugLogger.isDebug()) {
            DebugLogger.log(player.getName() + " 離開區域: " + region.getId());
        }

        if (!region.getLeaveTitle().isEmpty() || !region.getLeaveSubtitle().isEmpty()) {

            int fadeIn = plugin.getConfig().getInt("title.fade-in", 10);
            int stay = plugin.getConfig().getInt("title.stay", 40);
            int fadeOut = plugin.getConfig().getInt("title.fade-out", 10);

            player.sendTitle(
                    color(region.getLeaveTitle()),
                    color(region.getLeaveSubtitle()),
                    fadeIn,
                    stay,
                    fadeOut
            );
        }

        String sound = region.getSound();

        if (sound != null && !sound.isEmpty()) {
            stopLoopTask(player.getUniqueId());
            player.stopSound(sound, SoundCategory.MASTER);

            if (DebugLogger.isDebug()) {
                DebugLogger.log("離開區域停止音效: " + sound);
            }
        }

        executeCommands(player, region.getLeaveCommands());
    }

    private void startLoopTask(Player player, Region region) {
        UUID uuid = player.getUniqueId();
        stopLoopTask(uuid);

        int lengthSeconds = region.getSoundLength();
        if (lengthSeconds <= 0) return;

        long period = lengthSeconds * 20L;
        String regionId = region.getId();
        String sound = region.getSound();

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            if (!player.isOnline()) {
                stopLoopTask(uuid);
                return;
            }

            String currentRegionId = playerCurrentRegion.get(uuid);
            if (currentRegionId == null || !currentRegionId.equalsIgnoreCase(regionId)) {
                stopLoopTask(uuid);
                return;
            }

            float volume = calculateVolume(player, region);
            playerVolume.put(uuid, volume);

            player.stopSound(sound, SoundCategory.MASTER);
            player.playSound(
                    player.getLocation(),
                    sound,
                    SoundCategory.MASTER,
                    volume,
                    1.0f
            );

            if (DebugLogger.isDebug()) {
                DebugLogger.log("循環播放音效: " + sound + " 音量: " + volume);
            }

        }, period, period);

        loopTasks.put(uuid, taskId);
    }

    private void stopLoopTask(UUID uuid) {
        Integer taskId = loopTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private float calculateVolume(Player player, Region region) {
        float volume = (float) plugin.getConfig().getDouble("sound.fade.max-volume", 1.0);

        if (volume < 0) volume = 0;
        if (volume > 1) volume = 1;

        return volume;
    }

    private void executeCommands(Player player, Iterable<String> commands) {
        if (commands == null) return;

        for (String cmd : commands) {

            String finalCommand = cmd;

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                finalCommand = PlaceholderAPI.setPlaceholders(player, finalCommand);
            }

            finalCommand = finalCommand.replace("%player%", player.getName());

            boolean runAsPlayer = plugin.getConfig().getBoolean("commands.as-player", false);

            if (runAsPlayer) {
                boolean wasOp = player.isOp();
                player.setOp(true);
                player.performCommand(finalCommand);
                if (!wasOp) player.setOp(false);
            } else {
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "execute as " + player.getName() + " run " + finalCommand
                );
            }
        }
    }

    private String color(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }

    public void clearPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        stopLoopTask(uuid);

        if (playerCurrentRegion.containsKey(uuid)) {
            Region region = regionManager.getRegion(playerCurrentRegion.get(uuid));
            if (region != null && region.getSound() != null && !region.getSound().isEmpty()) {
                player.stopSound(region.getSound(), SoundCategory.MASTER);
            }
        }

        playerCurrentRegion.remove(uuid);
        playerVolume.remove(uuid);
    }

    public void clearAll() {

        for (Integer taskId : loopTasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        loopTasks.clear();
        playerCurrentRegion.clear();
        playerVolume.clear();
    }
}
