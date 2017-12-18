package com.redmancometh.redcore.commands;

import java.util.function.BiConsumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Data;

@Data
public class ServerCommand implements CommandExecutor
{
    private BiConsumer<CommandSender, String[]> action;
    private String command;
    private boolean opCommand = false;

    public ServerCommand(String command, JavaPlugin registering)
    {
        registering.getCommand(command).setExecutor(this);
        this.setCommand(command);
    }

    public ServerCommand(String command, BiConsumer<CommandSender, String[]> action, JavaPlugin registering)
    {
        registering.getCommand(command).setExecutor(this);
        this.action = action;
        this.setCommand(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (opCommand && !sender.isOp()) return true;
        action.accept(sender, args);
        return true;
    }

}
