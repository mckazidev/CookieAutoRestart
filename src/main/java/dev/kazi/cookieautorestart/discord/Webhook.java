package dev.kazi.cookieautorestart.discord;

import dev.kazi.cookieautorestart.system.LogUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;

public class Webhook {

    private final boolean enable;
    private final String url;
    private final List<Long> channelIds;
    private final boolean embed;
    private final List<String> enableMessages;
    private final List<String> disableMessages;

    public Webhook(FileConfiguration config) {
        this.enable = config.getBoolean("discord.webhook.enable");
        this.url = config.getString("discord.webhook.URL");
        this.channelIds = config.getLongList("discord.settings.channel-id");
        this.embed = config.getBoolean("messages.embed");
        this.enableMessages = config.getStringList("messages.enable");
        this.disableMessages = config.getStringList("messages.disable");
    }

    public void sendMessage(String message) {
        if (!enable) {
            return;
        }

        if (url == null || url.isEmpty()) {
            return;
        }

        String payload = constructPayload(message);
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                LogUtils.error("Не удалось отправить сообщение Webhook | Ошибка: " + responseCode);
            }
        } catch (Exception e) {
            LogUtils.error("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    private String constructPayload(String message) {
        if (embed) {
            return "{ \"embeds\": [{ \"description\": \"" + escapeJson(message) + "\" }] }";
        } else {
            return "{ \"content\": \"" + escapeJson(message) + "\" }";
        }
    }

    private String escapeJson(String message) {
        return message.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String combineMessages(List<String> messages) {
        StringJoiner joiner = new StringJoiner("\n");
        for (String message : messages) {
            if (!message.isEmpty()) {
                joiner.add(message);
            }
        }
        return joiner.toString();
    }

    public void sendEnableMessages() {
        String combinedMessage = combineMessages(enableMessages);
        if (!combinedMessage.isEmpty()) {
            sendMessage(combinedMessage);
        }
    }

    public void sendDisableMessages() {
        String combinedMessage = combineMessages(disableMessages);
        if (!combinedMessage.isEmpty()) {
            sendMessage(combinedMessage);
        }
    }
}
