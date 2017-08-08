package com.redmancometh.redcore.listener;

import com.google.common.collect.Lists;
import com.redmancometh.redcore.animation.AnimationAPI;
import com.redmancometh.redcore.api.VariableAPI;
import com.redmancometh.redcore.commands.CustomCommandMap;
import com.redmancometh.redcore.scoreboard.*;
import com.redmancometh.redcore.sign.SignGUI;
import com.redmancometh.redcore.spigotutils.TPSMeter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.redmancometh.redcore.spigotutils.SU.*;

public class SUListener extends JavaPlugin implements Listener, CommandExecutor {
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        try {
            Player plr = sender instanceof Player ? (Player) sender : null;
            String cmd = args.length == 0 ? "help" : args[0].toLowerCase();
            if (!sender.hasPermission("RedCore.command." + cmd)) {
                sender.sendMessage("§4§lAccess Denied.");
                return true;
            }
            ArrayList<Player> pls = plr == null ? Lists.newArrayList() : newArrayList(plr);
            int stripArg = 1;
            if (args.length > 1) {
                if (args[1].equals("*")) {
                    stripArg = 2;
                    pls = new ArrayList<>(Bukkit.getOnlinePlayers());
                } else if (args[1].startsWith("p:")) {
                    stripArg = 2;
                    pls.clear();
                    for (String s : args[1].substring(2).split(",")) {
                        Player p = getPlayer(s);
                        if (p == null) {
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
            switch (cmd) {
                case "help":
                    sender.sendMessage("§6§lRedCore - help menu\n" +
                            "§b§l➯ §e§lCONTACT:§f on Skype - com.redmancomethdev\n" +
                            "§b§l➯ §e§lSUBCOMMANDS:§f\n" +
                            "§b§l➯ §e/sl§f [p:players] cmd <commands>\n" +
                            "§b§l➯ §e/sl§f [p:players] perm [perm]\n" +
                            "§b§l➯ §e/sl§f [p:players] vars [text]\n" +
                            "§b§l➯ §e/sl§f [p:players] velocity <x> <y> <z>");
                    return true;
                case "cmd":
                    for (Player p : pls) {
                        for (String s : fullMsg.split(";"))
                            new com.redmancometh.redcore.commands.Command(s).execute(p);
                    }
                    return true;
                case "vars":
                    if (args.length == 0)
                        sender.sendMessage("§e§lAvailable placeholders:§f " + StringUtils.join(new TreeSet<>(VariableAPI.handlers.keySet()), ", "));
                    else
                        sender.sendMessage(fullMsg);
                    return true;
                case "perm":
                    for (Player p : pls)
                        sender.sendMessage("§bPlayer " + p.getName() + (p.hasPermission(args[0]) ? "§ahas§f " : "§cdoes not have§f ") + args[0] + "§b permission.");
                    return true;
                case "velocity":
                    Vector v = new Vector(Double.valueOf(args[0]), Double.valueOf(args[1]), Double.valueOf(args[2]));
                    for (Player p : pls) {
                        p.setVelocity(v);
                        sender.sendMessage("§bSet velocity of player " + p.getName() + " to " + v.getX() + "; " + v.getY() + "; " + v.getZ());
                    }
                    return true;
            }
        } catch (Throwable e) {
            error(sender, e, "RedCore", "com.redmancometh");

        }
        return true;
    }

    public void onDisable() {
        log(this, "§4[§cShutdown§4]§e Collecting plugins depending on RedCore...");
        ArrayList<Plugin> depend = new ArrayList<>();
        for (Plugin p : pm.getPlugins()) {
            PluginDescriptionFile pdf = p.getDescription();
            if (pdf.getDepend() != null && pdf.getDepend().contains("RedCore") || pdf.getSoftDepend() != null && pdf.getSoftDepend().contains("RedCore"))
                depend.add(p);
        }
        log(this, "§4[§cShutdown§4]§e Unloading plugins depending on RedCore...");
        for (Plugin p : depend) {
            log(this, "§4[§cShutdown§4]§e Unloading plugin §f" + p.getName() + "§e...");
            unloadPlugin(p);
        }
        log(this, "§4[§cShutdown§4]§e Stopping TPSMeter...");
        TPSMeter.meter.cancel(true);
        log(this, "§4[§cShutdown§4]§e Stopping PacketAPI...");
        try {
            tp.close();
        } catch (Throwable e) {
            error(cs, e, "RedCore", "com.redmancometh");
        }
        log(this, "§4[§cShutdown§4]§e Stopping AnimationAPI...");
        AnimationAPI.stopRunningAnimations(this);
        log(this, "§4[§cShutdown§4]§e Stopping ScoreboardAPI...");
        for (Player p : Bukkit.getOnlinePlayers()) {
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
        log(this, "§4[§cShutdown§4]§e Stopping CommandAPI...");
        CustomCommandMap.unhook();
        log(this, "§4[§cShutdown§4]§a The RedCore has shutted down properly.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player plr = e.getPlayer();
        UUID uid = plr.getUniqueId();
        AnimationAPI.stopRunningAnimations(plr);
        ScoreboardAPI.playerLeave(plr);
        SignGUI sg = SignGUI.openSignGUIs.remove(plr.getName());
        if (sg != null)
            sg.cancel();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player plr = e.getPlayer();
        ScoreboardAPI.playerJoin(plr);
    }

    @EventHandler
    public void onPluginUnload(PluginDisableEvent e) {
        Plugin pl = e.getPlugin();
        AnimationAPI.stopRunningAnimations(pl);
        tp.unregisterIncomingListener(pl);
        tp.unregisterOutgoingListener(pl);
    }

    @EventHandler
    public void registerServiceEvent(ServiceRegisterEvent e) {
        RegisteredServiceProvider p = e.getProvider();
        String sn = p.getService().getName();
        log(this, "Register service - " + sn);
        switch (sn) {
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
    public void unregisterServiceEvent(ServiceUnregisterEvent e) {
        RegisteredServiceProvider p = e.getProvider();
        String sn = p.getService().getName();
        log(this, "Unregister service - " + sn);
        switch (sn) {
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
