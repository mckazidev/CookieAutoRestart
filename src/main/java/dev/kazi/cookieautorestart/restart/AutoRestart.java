package dev.kazi.cookieautorestart.restart;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.kazi.cookieautorestart.Main;
import dev.kazi.cookieautorestart.manager.ConfigManager;
import dev.kazi.cookieautorestart.system.HexUtils;

public class AutoRestart {

    private final Main plugin;
    private final ConfigManager configManager;
    private List<LocalDateTime> restartTimes;

    public AutoRestart(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        scheduleRestarts();
    }

    private void scheduleRestarts() {
        restartTimes = parseRestartTimes(configManager.getRestartTimes());

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                for (LocalDateTime restartTime : restartTimes) {
                    long secondsUntilRestart = java.time.Duration.between(now, restartTime).getSeconds();

                    if (shouldBroadcastWarning(secondsUntilRestart)) {
                        broadcastRestartWarning(secondsUntilRestart);
                    } else if (secondsUntilRestart == 0) {
                        restartServer();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean shouldBroadcastWarning(long secondsUntilRestart) {
        return secondsUntilRestart == 3600 || secondsUntilRestart == 1800 ||
                secondsUntilRestart == 1200 || secondsUntilRestart == 600 ||
                secondsUntilRestart == 300 || secondsUntilRestart == 60 ||
                secondsUntilRestart == 30 || secondsUntilRestart == 10 ||
                (secondsUntilRestart <= 5 && secondsUntilRestart > 0);
    }

    private List<LocalDateTime> parseRestartTimes(List<String> restartStrings) {
        List<LocalDateTime> times = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E;HH;mm");

        for (String restart : restartStrings) {
            String[] parts = restart.split(";");
            LocalDateTime dateTime = LocalDateTime.now()
                    .withHour(Integer.parseInt(parts[1]))
                    .withMinute(Integer.parseInt(parts[2]))
                    .withSecond(0);
            while (!dateTime.getDayOfWeek().toString().equalsIgnoreCase(parts[0])) {
                dateTime = dateTime.plusDays(1);
            }
            times.add(dateTime);
        }
        return times;
    }

    private void broadcastRestartWarning(long secondsUntilRestart) {
        String formattedTime = formatTime(secondsUntilRestart);

        if ((boolean) configManager.getChatSettings().get("enable")) {
            List<String> messages = (List<String>) configManager.getChatSettings().get("time");
            if (messages != null) {
                for (String message : messages) {
                    Bukkit.broadcastMessage(HexUtils.translate(message
                            .replace("%time%", formattedTime)
                            .replace("%prefix%", configManager.getPrefix())));
                }
            }
        }

        if ((boolean) configManager.getTitleSettings().get("enable")) {
            String title = (String) ((Map<?, ?>) configManager.getTitleSettings().get("time")).get("title");
            String subtitle = (String) ((Map<?, ?>) configManager.getTitleSettings().get("time")).get("subtitle");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(
                        HexUtils.translate(title.replace("%time%", formattedTime).replace("%prefix%", configManager.getPrefix())),
                        HexUtils.translate(subtitle.replace("%time%", formattedTime).replace("%prefix%", configManager.getPrefix())),
                        10, 70, 20);
            }
        }

        if ((boolean) configManager.getActionBarSettings().get("enable")) {
            String actionBarMessage = (String) configManager.getActionBarSettings().get("time");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(HexUtils.translate(actionBarMessage
                                .replace("%time%", formattedTime)
                                .replace("%prefix%", configManager.getPrefix()))));
            }
        }
    }

    private void restartServer() {
        if ((boolean) configManager.getChatSettings().get("enable")) {
            List<String> messages = (List<String>) configManager.getChatSettings().get("start");
            if (messages != null) {
                for (String message : messages) {
                    Bukkit.broadcastMessage(HexUtils.translate(message.replace("%prefix%", configManager.getPrefix())));
                }
            }
        }

        if ((boolean) configManager.getTitleSettings().get("enable")) {
            String title = (String) ((Map<?, ?>) configManager.getTitleSettings().get("start")).get("title");
            String subtitle = (String) ((Map<?, ?>) configManager.getTitleSettings().get("start")).get("subtitle");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(
                        HexUtils.translate(title.replace("%prefix%", configManager.getPrefix())),
                        HexUtils.translate(subtitle.replace("%prefix%", configManager.getPrefix())),
                        10, 70, 20);
            }
        }

        if ((boolean) configManager.getActionBarSettings().get("enable")) {
            String actionBarMessage = (String) configManager.getActionBarSettings().get("start");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(HexUtils.translate(actionBarMessage
                                .replace("%prefix%", configManager.getPrefix()))));
            }
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;

        StringBuilder timeString = new StringBuilder();
        if (hours > 0) {
            timeString.append(hours).append(" ").append(getCorrectFormat(hours, "hour")).append(" ");
        }
        if (minutes > 0 || hours > 0) {
            timeString.append(minutes).append(" ").append(getCorrectFormat(minutes, "minute")).append(" ");
        }
        timeString.append(seconds).append(" ").append(getCorrectFormat(seconds, "second"));

        return timeString.toString().trim();
    }

    private String getCorrectFormat(long value, String unit) {
        return value == 1 ? configManager.getFormat().get(unit) : configManager.getFormat().get(unit + "s");
    }
}
