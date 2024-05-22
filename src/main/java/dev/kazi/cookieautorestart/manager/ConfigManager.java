package dev.kazi.cookieautorestart.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import dev.kazi.cookieautorestart.Main;
import dev.kazi.cookieautorestart.system.HexUtils;
import dev.kazi.cookieautorestart.discord.Webhook;

public class ConfigManager {
    private final Main plugin;
    private List<String> restartTimes;
    private Map<String, String> format;
    private Map<String, List<String>> messages;
    private Map<String, Object> chatSettings;
    private Map<String, Object> titleSettings;
    private Map<String, Object> actionBarSettings;
    private String noPermissionMessage;
    private String reloadMessage;
    private String prefix;

    private FileConfiguration messagesConfig;
    private File messagesFile;

    private FileConfiguration webhookConfig;
    private File webhookFile;

    private Webhook webhook;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
        loadMessagesConfig();
        loadWebhookConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        restartTimes = config.getStringList("restart");
        format = loadFormat(config);

        loadMessagesConfig();
        loadWebhookConfig();

        prefix = HexUtils.translate(getNonNullString(messagesConfig, "messages.prefix"));
        noPermissionMessage = getNonNullString(messagesConfig, "messages.no-permission").replace("%prefix%", prefix);
        reloadMessage = getNonNullString(messagesConfig, "messages.reload").replace("%prefix%", prefix);

        messages = new HashMap<>();
        messages.put("prefix", List.of(prefix));

        chatSettings = loadChatSettings(messagesConfig);
        titleSettings = loadTitleSettings(messagesConfig);
        actionBarSettings = loadActionBarSettings(messagesConfig);

        webhook = new Webhook(webhookConfig);
    }

    private void loadWebhookConfig() {
        webhookFile = new File(plugin.getDataFolder(), "webhook.yml");
        if (!webhookFile.exists()) {
            plugin.saveResource("webhook.yml", false);
        }
        webhookConfig = YamlConfiguration.loadConfiguration(webhookFile);

        InputStream defaultStream = plugin.getResource("webhook.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            webhookConfig.setDefaults(defaultConfig);
        }
    }

    private Map<String, String> loadFormat(FileConfiguration config) {
        Map<String, String> formatMap = new HashMap<>();
        formatMap.put("second", getNonNullString(config, "format.second"));
        formatMap.put("seconds", getNonNullString(config, "format.seconds"));
        formatMap.put("minute", getNonNullString(config, "format.minute"));
        formatMap.put("minutes", getNonNullString(config, "format.minutes"));
        formatMap.put("hour", getNonNullString(config, "format.hour"));
        formatMap.put("hours", getNonNullString(config, "format.hours"));
        formatMap.put("day", getNonNullString(config, "format.day"));
        formatMap.put("days", getNonNullString(config, "format.days"));
        return formatMap;
    }

    private Map<String, Object> loadChatSettings(FileConfiguration config) {
        Map<String, Object> chatSettingsMap = new HashMap<>();
        chatSettingsMap.put("enable", config.getBoolean("messages.chat.enable"));
        chatSettingsMap.put("time", translateList(config.getStringList("messages.chat.time")));
        chatSettingsMap.put("start", translateList(config.getStringList("messages.chat.start")));
        return chatSettingsMap;
    }

    private Map<String, Object> loadTitleSettings(FileConfiguration config) {
        Map<String, Object> titleSettingsMap = new HashMap<>();
        titleSettingsMap.put("enable", config.getBoolean("messages.title.enable"));
        titleSettingsMap.put("time", Map.of(
                "title", HexUtils.translate(getNonNullString(config, "messages.title.time.title")),
                "subtitle", HexUtils.translate(getNonNullString(config, "messages.title.time.subtitle"))
        ));
        titleSettingsMap.put("start", Map.of(
                "title", HexUtils.translate(getNonNullString(config, "messages.title.start.title")),
                "subtitle", HexUtils.translate(getNonNullString(config, "messages.title.start.subtitle"))
        ));
        return titleSettingsMap;
    }

    private Map<String, Object> loadActionBarSettings(FileConfiguration config) {
        Map<String, Object> actionBarSettingsMap = new HashMap<>();
        actionBarSettingsMap.put("enable", config.getBoolean("messages.action-bar.enable"));
        actionBarSettingsMap.put("time", HexUtils.translate(getNonNullString(config, "messages.action-bar.time")));
        actionBarSettingsMap.put("start", HexUtils.translate(getNonNullString(config, "messages.action-bar.start")));
        return actionBarSettingsMap;
    }

    public void loadMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    private String getNonNullString(FileConfiguration config, String path) {
        String value = config.getString(path);
        return value != null ? value : "";
    }

    private List<String> translateList(List<String> list) {
        List<String> translated = new ArrayList<>();
        for (String item : list) {
            translated.add(HexUtils.translate(item));
        }
        return translated;
    }

    public List<String> getRestartTimes() {
        return restartTimes;
    }

    public Map<String, String> getFormat() {
        return format;
    }

    public Map<String, List<String>> getMessages() {
        return messages;
    }

    public Map<String, Object> getChatSettings() {
        return chatSettings;
    }

    public Map<String, Object> getTitleSettings() {
        return titleSettings;
    }

    public Map<String, Object> getActionBarSettings() {
        return actionBarSettings;
    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public String getReloadMessage() {
        return reloadMessage;
    }

    public String getPrefix() {
        return prefix;
    }

    public Webhook getWebhook() {
        return webhook;
    }
}
