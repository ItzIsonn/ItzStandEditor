package me.itzisonn_.itzstandeditor;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    private final ItzStandEditor plugin;

    public Command(ItzStandEditor plugin) {
        this.plugin = plugin;

        PluginCommand pluginCommand = plugin.getCommand("itzstandeditor");

        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itzstandeditor.*") && !sender.hasPermission("itzstandeditor.reload")) {
            sender.sendMessage(plugin.getConfigManager().getNoPermMessage());
            return;
        }

        if (args.length != 1 || !args[0].equals("reload")) {
            sender.sendMessage(plugin.getConfigManager().getUsageMessage());
            return;
        }

        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(plugin.getConfigManager().getReloadedMessage());
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        execute(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return filter(List.of("reload"), args);
    }

    private List<String> filter(List<String> list, String[] args) {
        if (list == null) return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();

        for (String arg : list) {
            if (arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }

        return result;
    }
}