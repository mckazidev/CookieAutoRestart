package dev.kazi.cookieautorestart;

import org.bukkit.plugin.java.JavaPlugin;

import dev.kazi.cookieautorestart.commands.AutoRestartCMD;
import dev.kazi.cookieautorestart.manager.ConfigManager;
import dev.kazi.cookieautorestart.restart.AutoRestart;
import dev.kazi.cookieautorestart.system.HexUtils;
import dev.kazi.cookieautorestart.system.LogUtils;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        LogUtils.autorestart(HexUtils.translate("&fРазработчик: &9KaziDev"));
        this.configManager = new ConfigManager(this);
        new AutoRestart(this, configManager);

        AutoRestartCMD autoRestartCMD = new AutoRestartCMD(this, configManager);
        this.getCommand("cookieautorestart").setExecutor(autoRestartCMD);
        this.getCommand("cookieautorestart").setTabCompleter(autoRestartCMD);
        configManager.getWebhook().sendEnableMessages();
    }

    @Override
    public void onDisable() {
        configManager.getWebhook().sendDisableMessages();
    }
}
