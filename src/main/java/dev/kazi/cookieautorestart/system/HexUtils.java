package dev.kazi.cookieautorestart.system;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class HexUtils {

    public static String translate(String message) {
        if (message == null) {
            return "";
        } else {
            Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
            Matcher matcher = pattern.matcher(message);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String color = matcher.group(1);
                String replacement = ChatColor.of("#" + color).toString();
                matcher.appendReplacement(result, replacement);
            }

            matcher.appendTail(result);
            return ChatColor.translateAlternateColorCodes('&', result.toString());
        }
    }
}