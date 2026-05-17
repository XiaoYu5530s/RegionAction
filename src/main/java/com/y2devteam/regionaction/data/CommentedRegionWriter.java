package com.y2devteam.regionaction.data;

import com.y2devteam.regionaction.region.Region;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommentedRegionWriter {

    public static void writeHeader(File file) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {

            writer.write("# ===========================================");
            writer.newLine();
            writer.write("#             區域顯示設定資料檔");
            writer.newLine();
            writer.write("#       請勿刪除此檔案，但可安全手動修改");
            writer.newLine();
            writer.write("# ===========================================");
            writer.newLine();
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRegionWithComments(File file, Region region) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            writer.write(region.getId() + ":");
            writer.newLine();

            writer.write("  world: " + region.getWorldName() + "     # 所在世界名稱");
            writer.newLine();
            writer.write("  priority: " + region.getPriority() +
                    "    # 區域權重 (劃分進入區域優先不重疊效果)");
            writer.newLine();

            writer.write("  pos1:            # 區域第一個角落點");
            writer.newLine();
            writer.write("    x: " + region.getMinX());
            writer.newLine();
            writer.write("    y: " + region.getMinY());
            writer.newLine();
            writer.write("    z: " + region.getMinZ());
            writer.newLine();

            writer.write("  pos2:            # 區域第二個角落點");
            writer.newLine();
            writer.write("    x: " + region.getMaxX());
            writer.newLine();
            writer.write("    y: " + region.getMaxY());
            writer.newLine();
            writer.write("    z: " + region.getMaxZ());
            writer.newLine();

            writer.write("  enter:           # 玩家「進入」區域時顯示的Title");
            writer.newLine();
            writer.write("    title: '" + safe(region.getEnterTitle()) + "'      # 可填進入時的主標題");
            writer.newLine();
            writer.write("    subtitle: '" + safe(region.getEnterSubtitle()) + "'   # 可填進入時的副標題");
            writer.newLine();
            writer.write("    commands: []   # 可填進入時的使用什麼指令");
            writer.newLine();

            writer.write("  leave:           # 玩家「離開」區域時顯示的Title");
            writer.newLine();
            writer.write("    title: '" + safe(region.getLeaveTitle()) + "'");
            writer.newLine();
            writer.write("    subtitle: '" + safe(region.getLeaveSubtitle()) + "'");
            writer.newLine();
            writer.write("    commands: []");
            writer.newLine();

            writer.write("  sound: '" + safe(region.getSound())
                    + "'        # 進入區域時播放音效設定ID（空白=不播放，可填寫自訂音樂，例: minecraft:music.sound）");
            writer.newLine();

            writer.write("  sound-length: " + region.getSoundLength()
                    + " # 你的自訂音樂長度（以秒為單位)");
            writer.newLine();

            writer.write("  sound-loop: " + region.isLoopSound()
                    + " # 是否自動循環播放");
            writer.newLine();

            writer.write("  sound-delay: " + region.getSoundDelay()
                    + " # 進入區域後幾秒才開始播放音樂（以秒為單位）");
            writer.newLine();

            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String safe(String s) {
        return s == null ? "" : s.replace("'", "''");
    }
}
