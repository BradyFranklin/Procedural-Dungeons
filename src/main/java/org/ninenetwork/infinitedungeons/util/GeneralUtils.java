package org.ninenetwork.infinitedungeons.util;

import org.bukkit.Location;
import org.ninenetwork.infinitedungeons.dungeon.DungeonType;
import org.ninenetwork.infinitedungeons.listener.armorevent.ArmorType;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class GeneralUtils {

    public static String formatStringWithGradient(String input, String colorStart, String colorEnd, boolean bold, boolean withAnd) {
        StringBuilder formattedString = new StringBuilder();
        Color start = Color.decode(colorStart);
        Color end = Color.decode(colorEnd);
        double stepSizeRed = (double) (end.getRed() - start.getRed()) / (input.length() - 1);
        double stepSizeGreen = (double) (end.getGreen() - start.getGreen()) / (input.length() - 1);
        double stepSizeBlue = (double) (end.getBlue() - start.getBlue()) / (input.length() - 1);
        for (int i = 0; i < input.length(); i++) {
            int red = (int) (start.getRed() + stepSizeRed * i);
            int green = (int) (start.getGreen() + stepSizeGreen * i);
            int blue = (int) (start.getBlue() + stepSizeBlue * i);
            String hexColor = String.format("#%02X%02X%02X", red, green, blue);
            if (withAnd) {
                formattedString.append("&#").append(hexColor.substring(1));
            } else {
                formattedString.append("#").append(hexColor.substring(1));
            }
            if (bold) {
                formattedString.append("&l");
            }
            formattedString.append(input.charAt(i));
        }
        return formattedString.toString();
    }

    public static String formatStringWithRainbowGradient(String input, boolean bold) {
        StringBuilder formattedString = new StringBuilder();
        int numColors = input.length();
        for (int i = 0; i < input.length(); i++) {
            float hue = (float) i / numColors;
            float saturation = 1.0f;
            float brightness = 1.0f;
            Color color = Color.getHSBColor(hue, saturation, brightness);
            String hexColor = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            formattedString.append("&#").append(hexColor.substring(1));
            if (bold) {
                formattedString.append("&l");
            }
            formattedString.append(input.charAt(i));
        }
        return formattedString.toString();
    }

    public static PlayerStatSource armorToStatSource(ArmorType armorType) {
        if (armorType == ArmorType.BOOTS) {
            return PlayerStatSource.ARMOR_BOOTS;
        } else if (armorType == ArmorType.LEGGINGS) {
            return PlayerStatSource.ARMOR_LEGGINGS;
        } else if (armorType == ArmorType.CHESTPLATE) {
            return PlayerStatSource.ARMOR_CHESTPLATE;
        } else {
            return PlayerStatSource.ARMOR_HELMET;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String formatStringWithGradientTriColor(String input, String colorStart, String colorMid, String colorEnd, boolean bold) {
        StringBuilder formattedString = new StringBuilder();
        Color start = Color.decode(colorStart);
        Color mid = Color.decode(colorMid);
        Color end = Color.decode(colorEnd);

        int segmentLength = input.length() / 2; // Approximate length of each color segment

        formattedString.append(generateGradientSegment(input.substring(0, segmentLength), start, mid, bold));
        formattedString.append(generateGradientSegment(input.substring(segmentLength), mid, end, bold));

        return formattedString.toString();
    }

    private static String generateGradientSegment(String text, Color start, Color end, boolean bold) {
        StringBuilder segment = new StringBuilder();
        double stepSizeRed = (double) (end.getRed() - start.getRed()) / (text.length() - 1);
        double stepSizeGreen = (double) (end.getGreen() - start.getGreen()) / (text.length() - 1);
        double stepSizeBlue = (double) (end.getBlue() - start.getBlue()) / (text.length() - 1);

        for (int i = 0; i < text.length(); i++) {
            int red = (int) (start.getRed() + stepSizeRed * i);
            int green = (int) (start.getGreen() + stepSizeGreen * i);
            int blue = (int) (start.getBlue() + stepSizeBlue * i);
            String hexColor = String.format("#%02X%02X%02X", red, green, blue);
            segment.append("&#").append(hexColor.substring(1));
            if (bold) {
                segment.append("&l");
            }
            segment.append(text.charAt(i));
        }
        return segment.toString();
    }

    public static int getRequiredLevel(int floor, String viewType) {
        int requiredLevel = 0;
        if (floor == 1) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 1;
            } else {
                requiredLevel = 24;
            }
        } else if (floor == 2) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 3;
            } else {
                requiredLevel = 26;
            }
        } else if (floor == 3) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 5;
            } else {
                requiredLevel = 28;
            }
        } else if (floor == 4) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 9;
            } else {
                requiredLevel = 30;
            }
        } else if (floor == 5) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 14;
            } else {
                requiredLevel = 32;
            }
        } else if (floor == 6) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 19;
            } else {
                requiredLevel = 34;
            }
        } else if (floor == 7) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 24;
            } else {
                requiredLevel = 36;
            }
        }
        return requiredLevel;
    }

    public static double scatterNumber(double inputNumber) {
        double range = inputNumber * 0.04;
        Random random = new Random();
        return inputNumber - range + (random.nextDouble() * 2 * range);
    }

    public static Location findMidPoint(Location location1, Location location2) {
        double finalX;
        double finalY;
        double finalZ;
        finalX = (((location1.getX()) + (location2.getX())) / 2);
        finalY = (((location1.getY()) + (location2.getY())) / 2);
        finalZ = (((location1.getZ()) + (location2.getZ())) / 2);
        return new Location(location1.getWorld(), finalX, finalY, finalZ);
    }

    public static String getBossName(int floor) {
        if (floor == 1) {
            return "Bonzo";
        } else {
            return "temp";
        }
    }

    public static String getScoreLetterValue(int score) {
        if (score >= 300) {
            return "S+";
        } else if (score >= 270) {
            return "S";
        } else if (score >= 230) {
            return "A";
        } else if (score >= 160) {
            return "B";
        } else if (score >= 100) {
            return "C";
        } else {
            return "D";
        }
    }

    public static String getStatSymbol(PlayerStat stat) {
        if (stat == PlayerStat.DAMAGE) {
            return "❁";
        } else if (stat == PlayerStat.HEALTH) {
            return "❤";
        } else if (stat == PlayerStat.DEFENSE) {
            return "❈";
        } else if (stat == PlayerStat.TRUE_DEFENSE) {
            return "❂";
        } else if (stat == PlayerStat.SPEED) {
            return "✦";
        } else if (stat == PlayerStat.INTELLIGENCE) {
            return "✎";
        } else if (stat == PlayerStat.CRIT_CHANCE) {
            return "☣";
        } else if (stat == PlayerStat.CRIT_DAMAGE) {
            return "☠";
        } else if (stat == PlayerStat.BONUS_ATTACK) {
            return "⚔";
        } else if (stat == PlayerStat.FEROCITY) {
            return "⫽";
        } else if (stat == PlayerStat.MAGIC_FIND) {
            return "✯";
        } else if (stat == PlayerStat.VITALITY) {
            return "♨";
        } else if (stat == PlayerStat.ABILITY_DAMAGE) {
            return "๑";
        }
        return "";
    }

    public static String getCustomSymbol(String type) {
        if (type.equalsIgnoreCase("star")) {
            return "✪";
        } else if (type.equalsIgnoreCase("check")) {
            return "✔";
        } else if (type.equalsIgnoreCase("x")) {
            return "✖:";
        } else if (type.equalsIgnoreCase("arrow")) {
            return "➜";
        }
        return "";
    }

    public static String formatNumber(double number){
        if (number >= 1000000000){
            return String.format("%.2fB", number/ 1000000000.0);
        }
        if (number >= 1000000){
            return String.format("%.2fM", number/ 1000000.0);
        }
        if (number >= 100000){
            return String.format("%.2fK", number/ 1000.0);
        }
        if (number >= 1000){
            return String.format("%.2fK", number/ 1000.0);
        }
        if (number < 0) {
            return String.valueOf(0);
        } else if (number < 1) {
            return String.valueOf(1);
        }
        return String.valueOf((int) number);
    }

    public static String toRomanNumerals(int number) {
        if (number == 1) {
            return "I";
        } else if (number == 2) {
            return "II";
        } else if (number == 3) {
            return "III";
        } else if (number == 4) {
            return "IV";
        } else if (number == 5) {
            return "V";
        } else if (number == 6) {
            return "VI";
        } else if (number == 7) {
            return "VII";
        }
        return " ";
    }

    public static String dungeonTimeFormatter(int time) {
        if (time < 60) {
            return time + "s";
        }
        int minutes = time / 60;
        int seconds = time % 60;
        return minutes + "m " + seconds + "s";
    }

    public static String getDungeonTypeName(DungeonType type) {
        if (type == DungeonType.CATACOMBS) {
            return "Catacombs";
        } else if (type == DungeonType.MASTER) {
            return "Master Mode";
        }
        return "Placeholder Error";
    }

    public static String scoreboardClassSymbol(String pickedClass) {
        if (pickedClass.equalsIgnoreCase("mage")) {
            return "[M]";
        } else if (pickedClass.equalsIgnoreCase("archer")) {
            return "[A]";
        } else if (pickedClass.equalsIgnoreCase("berserk")) {
            return "[B]";
        } else if (pickedClass.equalsIgnoreCase("tank")) {
            return "[T]";
        } else if (pickedClass.equalsIgnoreCase("healer")) {
            return "[H]";
        }
        return "[!]";
    }


    public static String rainbow(String original) {
        char[] chars = new char[]{'f', 'f', 'e', '6', 'c', 'c', 'd'};
        int index = 0;
        String returnValue = "";
        for (char c : original.toCharArray()){
            returnValue += "&" + chars[index] + c;
            index++;
            if (index == chars.length){
                index = 0;
            }
        }
        return returnValue;
    }

    public static int findRowsForFloor(int floor, String hOrV) {
        if (floor == 7) {
            return 6;
        } else if (floor == 6 || floor == 5) {
            if (hOrV.equalsIgnoreCase("h")) {
                return 5;
            } else {
                return 6;
            }
        } else if (floor == 4 || floor == 3) {
            return 5;
        } else {
            return 4;
        }
    }

}