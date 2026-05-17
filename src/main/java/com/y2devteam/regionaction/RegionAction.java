package com.y2devteam.regionaction;

import com.y2devteam.regionaction.command.RegionActionCommand;
import com.y2devteam.regionaction.config.ConfigManager;
import com.y2devteam.regionaction.config.MessageManager;
import com.y2devteam.regionaction.data.DataManager;
import com.y2devteam.regionaction.listener.PlayerMoveListener;
import com.y2devteam.regionaction.region.RegionChecker;
import com.y2devteam.regionaction.region.RegionManager;
import com.y2devteam.regionaction.util.DebugLogger;
import com.y2devteam.regionaction.wand.WandListener;
import com.y2devteam.regionaction.wand.WandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionAction extends JavaPlugin {

    private static RegionAction instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DataManager dataManager;

    private RegionManager regionManager;
    private RegionChecker regionChecker;

    private WandManager wandManager;
    private WandListener wandListener;

    @Override
    public void onEnable() {

        instance = this;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);

        DebugLogger.setDebug(configManager.isDebugEnabled());
        this.regionManager = new RegionManager(this);
        this.dataManager = new DataManager(this, regionManager);
        this.regionChecker = new RegionChecker(this, regionManager);
        this.wandManager = new WandManager(this);
        this.wandListener = new WandListener(this);

        dataManager.loadRegions();
        Bukkit.getPluginManager().registerEvents(
                new PlayerMoveListener(this, regionChecker), this);

        Bukkit.getPluginManager().registerEvents(
                wandListener, this);
        RegionActionCommand command = new RegionActionCommand(this);
        getCommand("rac").setExecutor(command);
        getCommand("rac").setTabCompleter(command);
        printLogo(true);
    }

    @Override
    public void onDisable() {

        // 儲存所有區域資料
        if (dataManager != null) {
            dataManager.saveRegions();
        }
        printLogo(false);
    }

    // ========================================================
    // Getter 區（讓所有類可以存取）
    // ========================================================
    public static RegionAction getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RegionChecker getRegionChecker() {
        return regionChecker;
    }

    public WandManager getWandManager() {
        return wandManager;
    }

    public WandListener getWandListener() {
        return wandListener;
    }

    public void setWandManager(WandManager wandManager) {
        this.wandManager = wandManager;
    }

    public void setWandListener(WandListener wandListener) {
        this.wandListener = wandListener;
    }

    private void printLogo(boolean startup) {
        String version = getDescription().getVersion();
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("§9  __     __   ____");
        Bukkit.getConsoleSender().sendMessage("§9  \\ \\   / /  |___ \\   §fRegionAction §av" + version);
        Bukkit.getConsoleSender().sendMessage("§9   \\ \\_/ /     __) |    §aY2-Dev");
        Bukkit.getConsoleSender().sendMessage("§9    \\   /     / __/");
        Bukkit.getConsoleSender().sendMessage("§9     |_|     |_____|");
        Bukkit.getConsoleSender().sendMessage(" ");
        if (startup) {
            Bukkit.getConsoleSender().sendMessage("§a✅  RegionAction 該插件已成功正常啟用。");
        } else {
            Bukkit.getConsoleSender().sendMessage("§c⛔  RegionAction 該插件已成功正常關閉。");
        }
        if (DebugLogger.isDebug()) {
            Bukkit.getConsoleSender().sendMessage("§e[RAC-DEBUG] Debug 模式目前為開啟狀態");
        }
        Bukkit.getConsoleSender().sendMessage(" ");
    }
}
