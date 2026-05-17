package com.y2devteam.regionaction.data;

import com.y2devteam.regionaction.region.Region;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class SaveData {

    public static Region loadRegion(String id, ConfigurationSection section) {

        if (section == null) return null;

        String world = section.getString("world");

        int minX = section.getInt("pos1.x");
        int minY = section.getInt("pos1.y");
        int minZ = section.getInt("pos1.z");

        int maxX = section.getInt("pos2.x");
        int maxY = section.getInt("pos2.y");
        int maxZ = section.getInt("pos2.z");

        String enterTitle = section.getString("enter.title", "");
        String enterSubtitle = section.getString("enter.subtitle", "");
        String leaveTitle = section.getString("leave.title", "");
        String leaveSubtitle = section.getString("leave.subtitle", "");

        List<String> enterCommands = section.getStringList("enter.commands");
        List<String> leaveCommands = section.getStringList("leave.commands");

        String sound = section.getString("sound", "");
        int length = section.getInt("sound-length", 30);
        boolean loop = section.getBoolean("sound-loop", false);
        int delay = section.getInt("sound-delay", 0);

        // ✅ 讀取區域權重
        int priority = section.getInt("priority", 0);

        return new Region(
                id, world,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                enterTitle, enterSubtitle,
                leaveTitle, leaveSubtitle,
                enterCommands,
                leaveCommands,
                sound,
                length,
                loop,
                delay,
                priority // ✅ 新增
        );
    }

    public static void saveRegion(ConfigurationSection section, Region region) {
        if (section == null || region == null) return;
        region.saveToConfig(section);
    }
}
