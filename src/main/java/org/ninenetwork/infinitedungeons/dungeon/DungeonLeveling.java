package org.ninenetwork.infinitedungeons.dungeon;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class DungeonLeveling {

    public static void applyExp(Player player, int floor) {
        /*
        PlayerCache cache = PlayerCache.from(player);
        double expToApply = getRespectiveExp(dungeonRunReport);
        if ((cache.getDungeonLevelingExp() + expToApply) >= getRequiredExpToLevel(cache.getDungeonLevel() + 1)) {
            double remainder = (cache.getDungeonLevelingExp() + expToApply) % getRequiredExpToLevel(cache.getDungeonLevel() + 1);
            cache.setDungeonLevelingExp(remainder);
            cache.setTotalDungeonLevelExp(cache.getTotalDungeonLevelExp() + expToApply);
            cache.setDungeonLevel(cache.getDungeonLevel() + 1);
        }
        */
    }

    public static int getPlayerLevelBaseBoost(Player player) {
        int level = PlayerCache.from(player).getDungeonLevel();
        switch (level) {
            case 1:
                return 4;
            case 2:
                return 8;
            case 3:
                return 12;
            case 4:
                return 16;
            case 5:
                return 20;
            case 6:
                return 25;
            case 7:
                return 30;
            case 8:
                return 35;
            case 9:
                return 40;
            case 10:
                return 45;
            case 11:
                return 51;
            case 12:
                return 57;
            case 13:
                return 63;
            case 14:
                return 69;
            case 15:
                return 75;
            case 16:
                return 82;
            case 17:
                return 89;
            case 18:
                return 96;
            case 19:
                return 103;
            case 20:
                return 110;
            case 21:
                return 118;
            case 22:
                return 126;
            case 23:
                return 134;
            case 24:
                return 142;
            case 25:
                return 150;
            case 26:
                return 159;
            case 27:
                return 168;
            case 28:
                return 177;
            case 29:
                return 186;
            case 30:
                return 195;
            case 31:
                return 205;
            case 32:
                return 215;
            case 33:
                return 225;
            case 34:
                return 235;
            case 35:
                return 245;
            case 36:
                return 257;
            case 37:
                return 269;
            case 38:
                return 281;
            case 39:
                return 293;
            case 40:
                return 305;
            case 41:
                return 319;
            case 42:
                return 333;
            case 43:
                return 347;
            case 44:
                return 361;
            case 45:
                return 375;
            case 46:
                return 391;
            case 47:
                return 408;
            case 48:
                return 426;
            case 49:
                return 445;
            case 50:
                return 465;
            default:
                return 0;
        }
    }

    public static int getRequiredExpToLevel(int level) {
        switch (level) {
            case 1:
                return 50;
            case 2:
                return 75;
            case 3:
                return 110;
            case 4:
                return 160;
            case 5:
                return 230;
            case 6:
                return 330;
            case 7:
                return 470;
            case 8:
                return 670;
            case 9:
                return 950;
            case 10:
                return 1340;
            case 11:
                return 1890;
            case 12:
                return 2665;
            case 13:
                return 3760;
            case 14:
                return 5260;
            case 15:
                return 7380;
            case 16:
                return 10300;
            case 17:
                return 14400;
            case 18:
                return 20000;
            case 19:
                return 27600;
            case 20:
                return 38000;
            case 21:
                return 52500;
            case 22:
                return 71500;
            case 23:
                return 97000;
            case 24:
                return 132000;
            case 25:
                return 180000;
            case 26:
                return 243000;
            case 27:
                return 328000;
            case 28:
                return 445000;
            case 29:
                return 600000;
            case 30:
                return 800000;
            case 31:
                return 1065000;
            case 32:
                return 1410000;
            case 33:
                return 1900000;
            case 34:
                return 2500000;
            case 35:
                return 3300000;
            case 36:
                return 4300000;
            case 37:
                return 5600000;
            case 38:
                return 7200000;
            case 39:
                return 9200000;
            case 40:
                return 12000000;
            case 41:
                return 15000000;
            case 42:
                return 19000000;
            case 43:
                return 24000000;
            case 44:
                return 30000000;
            case 45:
                return 38000000;
            case 46:
                return 48000000;
            case 47:
                return 60000000;
            case 48:
                return 75000000;
            case 49:
                return 93000000;
            case 50:
                return 116250000;
            default:
                return 0;
        }
    }

}