package org.ninenetwork.infinitedungeons.util;

public class GeneralUtils {

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
        return String.valueOf(number);
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

}