package com.redmancometh.redcore.api;

import com.google.common.io.*;
import com.redmancometh.redcore.commands.Command;
import com.redmancometh.redcore.json.JsonAPI;
import com.redmancometh.redcore.spigotutils.SU;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;

/**
 * Created by com.redmancometh on 20/12/2015.
 */
public class BungeeAPI implements PluginMessageListener {
    public static boolean enabled;

    public static boolean executeBungeeCommands(String[] commands, String... players) {
        if (!enabled)
            return false;
        String json = JsonAPI.serialize(commands);
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("BungeeCommand");
        out.writeUTF(StringUtils.join(players, ","));
        out.writeUTF(json);
        p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        return true;
    }

    public static boolean executePlayerCommands(Command[] commands, String... players) {
        if (!enabled)
            return false;
        String json = JsonAPI.serialize(commands);
        return forwardToPlayer("CommandExecution", json.getBytes(), players);
    }

    public static boolean forwardToPlayer(String channel, byte[] message, String... players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ForwardToPlayer");
            out.writeUTF(s);
            out.writeUTF(channel);
            out.writeShort(message.length);
            out.write(message);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean executeServerCommands(String[] commands, String... servers) {
        if (!enabled)
            return false;
        String json = JsonAPI.serialize(commands);
        return forwardToServer("CommandExecution", json.getBytes(), servers);
    }

    public static boolean forwardToServer(String channel, byte[] message, String... servers) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF(s);
            out.writeUTF(channel);
            out.writeShort(message.length);
            out.write(message);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean executeServerCommands(Command[] commands, String... servers) {
        if (!enabled)
            return false;
        String json = JsonAPI.serialize(commands);
        return forwardToServer("CommandExecution", json.getBytes(), servers);
    }

    public static boolean forwardToAllServer(String channel, byte[] message) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(channel);
        out.writeShort(message.length);
        out.write(message);
        p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        return true;
    }

    public static boolean forwardToPlayer(String channel, byte[] message, Iterable<String> players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ForwardToPlayer");
            out.writeUTF(s);
            out.writeUTF(channel);
            out.writeShort(message.length);
            out.write(message);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean kick(String message, String... players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("KickPlayer");
            out.writeUTF(s);
            out.writeUTF(message);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean kick(String message, Iterable<String> players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("KickPlayer");
            out.writeUTF(s);
            out.writeUTF(message);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestCurrentServerName() {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());

        return true;
    }

    public static void requestIP(Player... players) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("IP");
        byte[] data = out.toByteArray();
        for (Player p : players) {
            p.sendPluginMessage(SU.pl, "BungeeCord", data);
        }
    }

    public static void requestIP(Iterable<Player> players) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("IP");
        byte[] data = out.toByteArray();
        for (Player p : players) {
            p.sendPluginMessage(SU.pl, "BungeeCord", data);
        }
    }

    public static boolean requestPlayerCount(Iterable<String> servers) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestPlayerCount(String... servers) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestPlayerList(Iterable<String> servers) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestPlayerList(String... servers) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestServerIP(Iterable<String> servers) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ServerIP");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestServerIP(String... servers) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ServerIP");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestServerNames() {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());

        return true;
    }

    public static boolean requestUUID(Iterable<String> players) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("UUIDOther");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean requestUUID(String... players) {
        if (!enabled)
            return false;
        Collection<Player> pls = (Collection<Player>) SU.srv.getOnlinePlayers();
        if (pls.size() == 0)
            return false;
        Player p = pls.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("UUIDOther");
            out.writeUTF(s);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static void send(String server, Player... players) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        for (Player p : players) {
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
    }

    public static void send(String server, Collection<Player> players) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        for (Player p : players) {
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
    }

    public static boolean send(String server, String... players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ConnectOther");
            out.writeUTF(s);
            out.writeUTF(server);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean send(String server, Iterable<String> players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ConnectOther");
            out.writeUTF(s);
            out.writeUTF(server);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean sendMessage(String msg, String... players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(s);
            out.writeUTF(msg);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    public static boolean sendMessage(String msg, Iterable<String> players) {
        if (!enabled)
            return false;
        Collection<Player> pc = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (pc.isEmpty())
            return false;
        Player p = pc.iterator().next();
        for (String s : players) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(s);
            out.writeUTF(msg);
            p.sendPluginMessage(SU.pl, "BungeeCord", out.toByteArray());
        }
        return true;
    }

    @Override
    public void onPluginMessageReceived(String channel, final Player player, byte[] bytes) {
        try {
            if (!channel.equals("BungeeCord"))
                return;
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String sub = in.readUTF();
            UUID uid = player.getUniqueId();
            switch (sub) {
                case "CommandExecution":
                    final Command[] commands = JsonAPI.deserialize(in.readUTF(), Command[].class);
                    SU.sch.scheduleSyncDelayedTask(SU.pl, () -> {
                        for (Command c : commands) {
                            c.execute(player);
                        }
                    });
            }
        } catch (Throwable ignored) {
        }
    }
}
