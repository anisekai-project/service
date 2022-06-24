package me.anisekai.toshiko.utils;

public final class FailSafeUtils {

    private FailSafeUtils() {}

    public static Integer parseInt(String str) {

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double parseDouble(String str) {

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }



}
