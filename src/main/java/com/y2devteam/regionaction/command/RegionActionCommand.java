package com.y2devteam.regionaction.command;

import com.y2devteam.regionaction.RegionAction;
import com.y2devteam.regionaction.region.Region;
import com.y2devteam.regionaction.util.DebugLogger;
import com.y2devteam.regionaction.wand.WandManager;
import com.y2devteam.regionaction.wand.WandListener;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionActionCommand implements CommandExecutor, TabCompleter {

    private final RegionAction plugin;

    public RegionActionCommand(RegionAction plugin) {
        this.plugin = plugin;
    }

    // 品牌顯示
    private void brand(CommandSender sender, String label) {
        Player player = sender instanceof Player ? (Player) sender : null;
        sender.sendMessage("§8§m============================================================");
        sender.sendMessage("§x§4§b§c§f§f§f(Customized small and medium plugins) §5Plugins§7: §aRegionAction v1.0.0");
        sender.sendMessage("§x§f§c§d§e§f§f<§x§e§b§c§e§f§fY§x§d§a§b§e§f§f2§x§c§9§a§d§f§f-§x§b§7§9§d§f§fD§x§a§6§8§d§f§fe§x§9§5§7§d§f§fv §x§8§4§6§d§f§fT§x§7§3§5§d§f§fe§x§6§1§4§d§f§fa§x§5§0§3§d§f§fm §x§5§0§3§d§f§f-§x§6§1§4§d§f§f L§x§7§3§5§d§f§fo§x§8§4§6§d§f§fw §x§9§5§7§d§f§c c§x§a§6§8§d§f§8o§x§b§7§9§d§f§6s§x§c§9§a§d§f§5t §x§d§a§b§e§f§3& §x§e§b§c§e§f§2S§x§f§c§d§e§f§1t§x§f§c§e§3§f§1a§x§f§b§e§8§f§2b§x§f§a§e§d§f§2i§x§f§9§e§f§f§3l§x§f§8§d§f§f§3i§x§f§7§c§f§f§3t§x§f§6§b§f§f§3y§x§f§5§a§f§f§2>");
        sender.sendMessage("§fVersion: §av" + plugin.getDescription().getVersion() + " §8(Code: §f#F-01§8)");
        sender.sendMessage("§fServer: §aPurpur/Paper 1.21+");
        sender.sendMessage("§x§5§0§0§0§f§fR§x§5§8§0§8§f§fe§x§6§0§0§f§f§fg§x§6§8§0§f§f§fi§x§7§0§1§6§f§fo§x§7§8§1§d§f§fn§x§8§0§2§5§f§fA§x§8§8§2§c§f§fc§x§9§0§3§4§f§ft§x§9§8§3§b§f§fi§x§a§0§4§3§f§fo§x§a§8§4§a§f§fn §7— Advanced Area & TriggerSystem");
        sender.sendMessage("§8§m============================================================");

        if (player != null) {
            TextComponent author = new TextComponent("§9Author: §f#雞槌勒 ");
            TextComponent discord = new TextComponent("§7[§8Discord§7]");
            discord.setClickEvent(new ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    "https://discord.gg/AKG8UNzXwM"
            ));
            discord.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§7[§f點擊並加入我的 Discord§7]").create()
            ));
            player.spigot().sendMessage(author, discord);
        } else {
            sender.sendMessage("§9Author: §f#雞槌勒 §8Discord§7: https://discord.gg/AKG8UNzXwM");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            brand(sender, label);
            return true;
        }

        if (!sender.hasPermission("regionaction.admin")) {
            if (sender instanceof Player) {
                plugin.getMessageManager().send((Player) sender, "no-permission");
            }
            return true;
        }

        // /rac reload
        if (args[0].equalsIgnoreCase("reload")) {

            plugin.getConfigManager().reload();
            plugin.getMessageManager().reload();
            plugin.getDataManager().reload();
            plugin.getRegionManager().clear();
            plugin.getDataManager().loadRegions();

            plugin.getRegionChecker().clearAll();

            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getRegionChecker().checkPlayer(p, p.getLocation());
            }

            DebugLogger.setDebug(plugin.getConfigManager().isDebugEnabled());

            if (sender instanceof Player) {
                plugin.getMessageManager().send((Player) sender, "reloaded");
            } else {
                Bukkit.getConsoleSender().sendMessage("§a[RAC] 插件已重新載入");
            }

            return true;
        }

        // /rac create <id>
        if (args[0].equalsIgnoreCase("create")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只有玩家可以使用此指令");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§c用法: /rac create <id>");
                return true;
            }

            Player player = (Player) sender;
            String id = args[1].toLowerCase();

            if (!plugin.getWandListener().hasBothPositions(player)) {
                plugin.getMessageManager().send(player, "need-select");
                return true;
            }

            Location pos1 = plugin.getWandListener().getPos1(player);
            Location pos2 = plugin.getWandListener().getPos2(player);

            if (plugin.getRegionManager().exists(id)) {
                plugin.getMessageManager().send(player, "region-exists", "%id%", id);
                return true;
            }

            boolean result = plugin.getRegionManager().createRegion(id, pos1, pos2);

            if (result) {
                if (DebugLogger.isDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§a[RAC-DEBUG] 區域建立成功: " + id);
                }
                plugin.getDataManager().saveRegions();
                plugin.getWandListener().clearPositions(player);

                plugin.getMessageManager()
                        .send(player, "region-created",
                                "%id%", id);
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {

            if (!(sender instanceof Player)) {
                plugin.getRegionManager().printRegions();
                return true;
            }

            sender.sendMessage("§f§l§m━━━━━§7 [§6所有已設定的區域§7] §f§l§m━━━━━");

            if (plugin.getRegionManager().getAllRegions().isEmpty()) {
                sender.sendMessage("§c目前沒有任何已設置好的區域");
                return true;
            }

            for (Region region : plugin.getRegionManager().getAllRegions()) {
                sender.sendMessage("§7區域ID: §f" + region.getId());
                sender.sendMessage("§7所在世界: §f" + region.getWorldName());
                sender.sendMessage("§7點位1: §f"
                        + region.getMinX() + ", "
                        + region.getMinY() + ", "
                        + region.getMinZ());
                sender.sendMessage("§7點位2: §f"
                        + region.getMaxX() + ", "
                        + region.getMaxY() + ", "
                        + region.getMaxZ());
            }

            return true;
        }

        // /rac delete <id>
        if (args[0].equalsIgnoreCase("delete")) {

            if (args.length < 2) {
                sender.sendMessage("§c用法: /rac delete <id>");
                return true;
            }

            String id = args[1].toLowerCase();

            if (!plugin.getRegionManager().exists(id)) {

                if (sender instanceof Player) {
                    plugin.getMessageManager()
                            .send((Player) sender, "region-not-found", "%id%", id);
                }

                return true;
            }

            plugin.getRegionManager().deleteRegion(id);
            plugin.getDataManager().saveRegions();

            if (sender instanceof Player) {
                plugin.getMessageManager()
                        .send((Player) sender, "region-deleted",
                                "%id%", id);
            }

            return true;
        }

        // /rac give <player>
        if (args[0].equalsIgnoreCase("give")) {

            if (args.length < 2) {
                sender.sendMessage("§c用法: /rac give <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage("§c找不到該玩家");
                return true;
            }

            plugin.getWandManager().giveWand(target);

            if (sender instanceof Player) {
                plugin.getMessageManager()
                        .send((Player) sender,
                                "wand-given",
                                "%player%", target.getName());
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("debug")) {

            DebugLogger.toggle();

            if (sender instanceof Player) {

                if (DebugLogger.isDebug()) {
                    plugin.getMessageManager().send((Player) sender, "debug-on");
                } else {
                    plugin.getMessageManager().send((Player) sender, "debug-off");
                }
            }

            return true;
        }
        sender.sendMessage("§c未知指令，使用 /rac 查看幫助");
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {

        List<String> list = new ArrayList<>();

        if (!sender.hasPermission("regionaction.admin"))
            return list;
        if (args.length == 1) {
            list.add("create");
            list.add("delete");
            list.add("give");
            list.add("list");
            list.add("reload");
            list.add("debug");
        }
        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("delete")) {
                return plugin.getRegionManager().getRegionIds();
            }

            if (args[0].equalsIgnoreCase("give")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }
        }
        return list;
    }
}
