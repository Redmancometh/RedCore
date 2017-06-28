package com.redmancometh.redcore.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.hazelcast.util.function.BiConsumer;
import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.config.ConfigManager;

public class DebugConfig extends ServerCommand
{

    public DebugConfig(String command, BiConsumer<CommandSender, String[]> action, JavaPlugin registering)
    {
        super("cfgdebug", (sender, args) -> RedCore.getInstance().getPluginManager().forEach((plugin) -> ConfigManager.debugPrint(plugin)), RedCore.getInstance());
    }

}
