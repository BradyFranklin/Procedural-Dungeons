package org.ninenetwork.infinitedungeons.util;

import org.mineacademy.fo.model.BoxedMessage;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {

    public static void dungeonEndMessage(Dungeon dungeon) {
        for (PlayerCache cache : dungeon.getPlayerCaches()) {

            BoxedMessage.tell(cache.toPlayer(), "&CDungeons &8- &EFloor " + GeneralUtils.toRomanNumerals(dungeon.getFloor()),
                    "&FTeam Score: &a" + dungeon.getDungeonScore().getDungeonScore() + "&f(&b" + GeneralUtils.getScoreLetterValue((int) dungeon.getDungeonScore().getDungeonScore()),
                    "&eDefeated &c" + GeneralUtils.getBossName(dungeon.getFloor()) + " &ein &a" + GeneralUtils.dungeonTimeFormatter(dungeon.getDungeonTimeTracker().getElapsedTime()),
                    "&6> &e&lEXTRA STATS &6<",
                    " ",
                    "&3+(exp) Catacombs Experience",
                    "&3+(exp) Class Experience",
                    "&3+(exp) Other Experience &b(Team Bonus)");
        }
    }

    public static List<String> applyRainbowAnimation(List<String> loreLines) {
        List<String> animatedLore = new ArrayList<>();

        for (String line : loreLines) {
            animatedLore.add(applyRainbowToLine(line));
        }

        return animatedLore;
    }

    private static String applyRainbowToLine(String line) {
        StringBuilder animatedLine = new StringBuilder();
        String[] words = line.split(" "); // Split into words
        int colorIndex = 0;

        for (String word : words) {
            animatedLine.append(applyColorToWord(word, colorIndex));
            animatedLine.append(" "); // Add space between words
            colorIndex = (colorIndex + 1) % 6; // Cycle through 6 colors
        }

        return animatedLine.toString().trim(); // Trim trailing space
    }

    private static String applyColorToWord(String word, int colorIndex) {
        String[] rainbowColors = {"&#FF0000", "&#FFA500", "&#FFFF00",
                "�FF00", "�FF", "&#FF00FF"};
        return rainbowColors[colorIndex] + word;
    }

}