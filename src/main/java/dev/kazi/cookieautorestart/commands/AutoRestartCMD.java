package dev.kazi.cookieautorestart.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.kazi.cookieautorestart.Main;
import dev.kazi.cookieautorestart.manager.ConfigManager;
import dev.kazi.cookieautorestart.restart.AutoRestart;
import dev.kazi.cookieautorestart.system.HexUtils;

public class AutoRestartCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private ConfigManager configManager;

    public AutoRestartCMD(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("cookieautorestart.reload")) {
                    player.sendMessage(HexUtils.translate(configManager.getNoPermissionMessage()));
                    return true;
                }
            }

            configManager.loadConfig();
            plugin.getServer().getScheduler().cancelTasks(plugin);
            new AutoRestart(plugin, configManager);
            sender.sendMessage(HexUtils.translate(configManager.getReloadMessage()));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
