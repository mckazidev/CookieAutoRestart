package dev.kazi.cookieautorestart.system;

public class LogUtils {

    public static void autorestart(String text) {
        System.out.println(HexUtils.translate("&8[&#FF8400Cookie&fAutoRestart&8] " + text));
    }

    public static void warning(String text) {
        System.out.println(HexUtils.translate("&8[&#FFD472ВНИМАНИЕ&8] " + text));
    }

    public static void error(String text) {
        System.out.println(HexUtils.translate("&8[&#FF0000ОШИБКА&8] " + text));
    }
}
