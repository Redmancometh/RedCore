package com.redmancometh.redcore.commands;

import com.hazelcast.util.function.BiConsumer;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerCommand implements CommandExecutor {
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

    public BiConsumer<CommandSender, String[]> getAction()
    {
        return action;
    }

    public void setAction(BiConsumer<CommandSender, String[]> action)
    {
        this.action = action;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public boolean isOpCommand()
    {
        return opCommand;
    }

    public void setOpCommand(boolean opCommand)
    {
        this.opCommand = opCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (opCommand && !sender.isOp()) return true;
        action.accept(sender, args);
        return true;
    }

}
