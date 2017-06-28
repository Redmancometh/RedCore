package com.redmancometh.redcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.hazelcast.util.function.BiConsumer;

public class ServerCommand implements CommandExecutor
{
    private BiConsumer<CommandSender, String[]> action;
    private boolean opCommand = false;
    private String command;

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

    public BiConsumer<CommandSender, String[]> getAction()
    {
        return action;
    }

    public void setAction(BiConsumer<CommandSender, String[]> action)
    {
        this.action = action;
    }

    public boolean isOpCommand()
    {
        return opCommand;
    }

    public void setOpCommand(boolean opCommand)
    {
        this.opCommand = opCommand;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

}
