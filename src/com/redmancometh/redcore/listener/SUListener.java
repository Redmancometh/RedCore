package com.redmancometh.redcore.listener;

import com.google.common.collect.Lists;
import com.redmancometh.redcore.animation.AnimationAPI;
import com.redmancometh.redcore.api.VariableAPI;
import com.redmancometh.redcore.commands.CustomCommandMap;
import com.redmancometh.redcore.scoreboard.PlayerBars;
import com.redmancometh.redcore.scoreboard.ScoreboardAPI;
import com.redmancometh.redcore.scoreboard.ScoreboardBar;
import com.redmancometh.redcore.sign.SignGUI;
import com.redmancometh.redcore.spigotutils.SU;
import com.redmancometh.redcore.spigotutils.TPSMeter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.redmancometh.redcore.spigotutils.SU.*;

public class SUListener implements Listener, CommandExecutor
{
    public SUListener()
    {
        PluginCommand cmd = SU.pl().getCommand("sl");
        cmd.setExecutor(this);
    }

    public static void onDisable()
    {
        log(SU.pl(), "§4[§cShutdown§4]§e Collecting plugins depending on RedCore...");
        ArrayList<Plugin> depend = new ArrayList<>();
        for (Plugin p : pm.getPlugins())
        {
            PluginDescriptionFile pdf = p.getDescription();
            if (pdf.getDepend() != null && pdf.getDepend().contains("RedCore") || pdf.getSoftDepend() != null && pdf.getSoftDepend().contains("RedCore"))
                depend.add(p);
        }
        log(SU.pl(), "§4[§cShutdown§4]§e Unloading plugins depending on RedCore...");
        for (Plugin p : depend)
        {
            log(SU.pl(), "§4[§cShutdown§4]§e Unloading plugin §f" + p.getName() + "§e...");
            unloadPlugin(p);
        }
        log(SU.pl(), "§4[§cShutdown§4]§e Stopping TPSMeter...");
        TPSMeter.meter.cancel(true);
        log(SU.pl(), "§4[§cShutdown§4]§e Stopping PacketAPI...");
        try
        {
            tp.close();
        } catch (Throwable e)
        {
            error(cs, e, "RedCore", "com.redmancometh");
        }
        log(SU.pl(), "§4[§cShutdown§4]§e Stopping AnimationAPI...");
        AnimationAPI.stopRunningAnimations(SU.pl());
        log(SU.pl(), "§4[§cShutdown§4]§e Stopping ScoreboardAPI...");
        for (Player p : Bukkit.getOnlinePlayers())
        {
            PlayerBars pbs = ScoreboardAPI.sidebars.remove(p.getName());
            for (ScoreboardBar sb : pbs.loaded)
                sb.unload(p);
            pbs = ScoreboardAPI.nametags.remove(p.getName());
            for (ScoreboardBar sb : pbs.loaded)
                sb.unload(p);
            pbs = ScoreboardAPI.tabbars.remove(p.getName());
            for (ScoreboardBar sb : pbs.loaded)
                sb.unload(p);
        }
        log(SU.pl(), "§4[§cShutdown§4]§e Stopping CommandAPI...");
        CustomCommandMap.unhook();
        log(SU.pl(), "§4[§cShutdown§4]§a The RedCore has shutted down properly.");
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args)
    {
        try
        {
            Player plr = sender instanceof Player ? (Player) sender : null;
            String cmd = args.length == 0 ? "help" : args[0].toLowerCase();
            if (!sender.hasPermission("redcore.command." + cmd))
            {
                sender.sendMessage("§4§lAccess Denied.");
                return true;
            }
            ArrayList<Player> pls = plr == null ? Lists.newArrayList() : newArrayList(plr);
            int stripArg = 1;
            if (args.length > 1)
            {
                if (args[1].equals("*"))
                {
                    stripArg = 2;
                    pls = new ArrayList<>(Bukkit.getOnlinePlayers());
                } else if (args[1].startsWith("p:"))
                {
                    stripArg = 2;
                    pls.clear();
                    for (String s : args[1].substring(2).split(","))
                    {
                        Player p = getPlayer(s);
                        if (p == null)
                        {
                            sender.sendMessage("§cPlayer §e§l" + s + "§c was not found.");
                            continue;
                        }
                        pls.add(p);
                    }
                }
            }
            args = (String[]) ArrayUtils.subarray(args, stripArg, args.length);
            String fullMsg = StringUtils.join(args, ' ');
            fullMsg = VariableAPI.fillVariables(fullMsg, plr);
            switch (cmd)
            {
                case "help":
                    sender.sendMessage("§6§lRedCore - help menu\n" + "§b§lž¯ §e§lCONTACT:§f on Skype - gyurixdev\n" + "§b§lž¯ §e§lSUBCOMMANDS:§f\n" + "§b§lž¯ §6/sl§e [p:players]§6§l cmd§e <commands>\n" + "§b§lž¯ §6/sl§e [p:players]§6§l perm§e [perm]\n" + "§b§lž¯ §6/sl§e [p:players]§6§l vars§e [text]\n" + "§b§lž¯ §6/sl§e [p:players]§6§l velocity§e <x> <y> <z>");
                    return true;
                case "cmd":
                    for (Player p : pls)
                    {
                        for (String s : fullMsg.split(";"))
                            new com.redmancometh.redcore.commands.Command(s).execute(p);
                    }
                    return true;
                case "vars":
                    if (args.length == 0)
                        sender.sendMessage("§e§lAvailable placeholders:§f " + StringUtils.join(new TreeSet<>(VariableAPI.handlers.keySet()), ", "));
                    else sender.sendMessage(fullMsg);
                    return true;
                case "perm":
                    for (Player p : pls)
                        sender.sendMessage("§ePlayer §6§l" + p.getName() + (p.hasPermission(args[0]) ? " §ahas§f " : " §cdoes not have§b ") + args[0] + "§e permission.");
                    return true;
                case "velocity":
                    Vector v = new Vector(Double.valueOf(args[0]), Double.valueOf(args[1]), Double.valueOf(args[2]));
                    for (Player p : pls)
                    {
                        p.setVelocity(v);
                        sender.sendMessage("§eSet velocity of player " + p.getName() + " to §6§l" + v.getX() + "; " + v.getY() + "; " + v.getZ());
                    }
                    return true;
            }
        } catch (Throwable e)
        {
            error(sender, e, "RedCore", "com.redmancometh");

        }
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player plr = e.getPlayer();
        UUID uid = plr.getUniqueId();
        AnimationAPI.stopRunningAnimations(plr);
        ScoreboardAPI.playerLeave(plr);
        SignGUI sg = SignGUI.openSignGUIs.remove(plr.getName());
        if (sg != null) sg.cancel();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent e)
    {
        Player plr = e.getPlayer();
        ScoreboardAPI.playerJoin(plr);
    }

    @EventHandler
    public void onPluginUnload(PluginDisableEvent e)
    {
        Plugin pl = e.getPlugin();
        AnimationAPI.stopRunningAnimations(pl);
        tp.unregisterIncomingListener(pl);
        tp.unregisterOutgoingListener(pl);
    }

    @EventHandler
    public void registerServiceEvent(ServiceRegisterEvent e)
    {
        RegisteredServiceProvider p = e.getProvider();
        String sn = p.getService().getName();
        log(SU.pl(), "Register service - " + sn);
        switch (sn)
        {
            case "net.milkbowl.vault.chat.Chat":
                chat = (Chat) p.getProvider();
                break;
            case "net.milkbowl.vault.economy.Economy":
                econ = (Economy) p.getProvider();
                break;
            case "net.milkbowl.vault.permission.Permission":
                perm = (Permission) p.getProvider();
                break;
        }
    }

    @EventHandler
    public void unregisterServiceEvent(ServiceUnregisterEvent e)
    {
        RegisteredServiceProvider p = e.getProvider();
        String sn = p.getService().getName();
        log(SU.pl(), "Unregister service - " + sn);
        switch (sn)
        {
            case "net.milkbowl.vault.chat.Chat":
                chat = null;
                break;
            case "net.milkbowl.vault.economy.Economy":
                econ = null;
                break;
            case "net.milkbowl.vault.permission.Permission":
                perm = null;
                break;
        }
    }
}

