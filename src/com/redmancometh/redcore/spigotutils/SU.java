package com.redmancometh.redcore.spigotutils;

import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.animation.AnimationAPI;
import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.api.VariableAPI;
import com.redmancometh.redcore.commands.CustomCommandMap;
import com.redmancometh.redcore.listener.SUListener;
import com.redmancometh.redcore.mojang.MojangAPI;
import com.redmancometh.redcore.nbt.NBTApi;
import com.redmancometh.redcore.protocol.Protocol;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.manager.ProtocolImpl;
import com.redmancometh.redcore.protocol.utils.GameProfile;
import com.redmancometh.redcore.protocol.utils.WrapperFactory;
import com.redmancometh.redcore.scoreboard.ScoreboardAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import static com.redmancometh.redcore.commands.CustomCommandMap.knownCommands;
import static org.bukkit.Bukkit.getServer;

/**
 * RedCore utilities class
 */
public final class SU
{
    public static final Charset utf8 = Charset.forName("UTF-8");
    /**
     * The instance of current Chat provider in Vault
     */
    public static Chat chat;
    /**
     * Instance of the CustomCommandMap used by RedCores CommandAPI
     */
    public static CustomCommandMap cm;
    /**
     * The main instance of the ConsoleCommandSender object.
     */
    public static ConsoleCommandSender cs;
    /**
     * The instance of current Economy provider in Vault
     */
    public static Economy econ;
    /**
     * An instance of the Javascript script engine, used for the eval variable
     */
    public static ScriptEngine js;
    public static HashSet<UUID> loadedPlayers = new HashSet<>();
    /**
     * The main instance of the Messenger object.
     */
    public static Messenger msg;
    /**
     * The instance of current Permission provider in Vault
     */
    public static Permission perm;
    /**
     * The main instance of the PluginManager object.
     */
    public static PluginManager pm;
    /**
     * An instance of the Random number generator
     */
    public static Random rand = new Random();
    /**
     * The main instance of the BukkitScheduler object.
     */
    public static BukkitScheduler sch;
    /**
     * The main instance of the ServicesManager object
     */
    public static ServicesManager sm;
    /**
     * The main instance of the CraftServer object.
     */
    public static Server srv;
    /**
     * PacketAPI instance
     */
    public static Protocol tp;
    /**
     * Name - UUID cache
     */
    public static DualMap<String, UUID> uuidCache = new DualMap<>();
    /**
     * True if Vault is found on the server
     */
    public static boolean vault;
    static Field pluginsF, lookupNamesF;
    private static Field entityF;
    private static Constructor entityPlayerC, playerInterractManagerC;
    private static Method getBukkitEntityM, loadDataM, saveDataM;
    private static Field pingF;
    /**
     * RedCore instance
     */
    private static RedCore pl;
    private static boolean schedulePacketAPI;
    private static Object worldServer, mcServer;

    /**
     * Escape multi line text to a single line one
     *
     * @param text multi line escapeable text input
     * @return The escaped text
     */
    public static String escapeText(String text)
    {
        return text.replace("\\", "\\\\").replace("_", "\\_").replace("|", "\\|").replace(" ", "_").replace("\n", "|");
    }

    /**
     * Fills variables in a String
     *
     * @param s    - The String
     * @param vars - The variables and their values, which should be filled
     * @return The variable filled String
     */
    public static String fillVariables(String s, HashMap<String, Object> vars)
    {
        for (Entry<String, Object> v : vars.entrySet())
            s = s.replace('<' + v.getKey() + '>', String.valueOf(v.getValue()));
        return s;
    }

    /**
     * Fills variables in a String
     *
     * @param s    - The String
     * @param vars - The variables and their values, which should be filled
     * @return The variable filled String
     */
    public static String fillVariables(String s, Object... vars)
    {
        String last = null;
        for (Object v : vars)
        {
            if (last == null) last = (String) v;
            else
            {
                s = s.replace('<' + last + '>', String.valueOf(v));
                last = null;
            }
        }
        return s;
    }

    /**
     * Fills variables in an iterable
     *
     * @param iterable - The iterable
     * @param vars     - The variables and their values, which should be filled
     * @return The variable filled iterable converted to an ArrayList
     */
    public static ArrayList<String> fillVariables(Iterable<String> iterable, Object... vars)
    {
        ArrayList<String> out = new ArrayList<>();
        iterable.forEach((s) -> {
            String last = null;
            for (Object v : vars)
            {
                if (last == null) last = (String) v;
                else
                {
                    s = s.replace('<' + last + '>', String.valueOf(v));
                    last = null;
                }
            }
            out.add(s);
        });
        return out;
    }

    /**
     * Filters the startings of the given data
     *
     * @param data  - The data to be filtered
     * @param start - Filter every string which starts with this one
     * @return The filtered Strings
     */
    public static ArrayList<String> filterStart(String[] data, String start)
    {
        start = start.toLowerCase();
        ArrayList<String> ld = new ArrayList<>();
        for (String s : data)
        {
            if (s.toLowerCase().startsWith(start)) ld.add(s);
        }
        Collections.sort(ld);
        return ld;
    }

    /**
     * Filters the startings of the given data
     *
     * @param data  - The data to be filtered
     * @param start - Filter every string which starts with this one
     * @return The filtered Strings
     */
    public static ArrayList<String> filterStart(Iterable<String> data, String start)
    {
        start = start.toLowerCase();
        ArrayList<String> ld = new ArrayList<>();
        for (String s : data)
        {
            if (s.toLowerCase().startsWith(start)) ld.add(s);
        }
        Collections.sort(ld);
        return ld;
    }

    /**
     * Get the ping of a player in milliseconds
     *
     * @param plr target player
     * @return The ping of the given player in milliseconds.
     */
    public static int getPing(Player plr)
    {
        try
        {
            return pingF.getInt(entityF.get(plr));
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get an online player or optionally load an offline player based on its name
     *
     * @param name name of the player, which should be got / active.
     * @return The online player / active offline player who has the given name, or null if no such player have found.
     */
    public static Player getPlayer(String name)
    {
        if (name.length() > 16)
        {
            UUID uuid = UUID.fromString(name);
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) p = loadPlayer(uuid);
            return p;
        }
        Player p = Bukkit.getPlayerExact(name);
        if (p == null) p = loadPlayer(getUUID(name));
        return p;
    }

    /**
     * Load an offline player to be handleable like an online one.
     *
     * @param uuid uuid of the loadable offline player
     * @return the active Player object, or null if the player was not found.
     */
    public static Player loadPlayer(UUID uuid)
    {
        try
        {
            if (uuid == null)
            {
                return null;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player == null || !player.hasPlayedBefore())
            {
                return null;
            }

            Player plr = (Player) getBukkitEntityM.invoke(entityPlayerC.newInstance(mcServer, worldServer, new GameProfile(player.getName(), uuid).toNMS(), playerInterractManagerC.newInstance(worldServer)));
            if (plr != null)
            {
                loadDataM.invoke(plr);
                return plr;
            }
        } catch (Throwable e)
        {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return null;
    }

    /**
     * Get the UUID of an offline player based on his name.
     *
     * @param name name of the target player
     * @return The UUID of the requested player, or null if it was not found.
     */
    public static UUID getUUID(String name)
    {
        Player plr = Bukkit.getPlayer(name);
        if (plr != null) return plr.getUniqueId();
        OfflinePlayer[] offlinePls = Bukkit.getOfflinePlayers();
        for (OfflinePlayer p : offlinePls)
        {
            if (p.getName() != null && p.getName().equals(name)) return p.getUniqueId();
        }
        name = name.toLowerCase();
        for (OfflinePlayer p : offlinePls)
        {
            if (p.getName() != null && p.getName().toLowerCase().equals(name)) return p.getUniqueId();
        }
        for (OfflinePlayer p : offlinePls)
        {
            if (p.getName() != null && p.getName().toLowerCase().contains(name)) return p.getUniqueId();
        }
        return getOnlineUUID(name);
    }

    /**
     * Sends an error report to the given sender and to console. The report only includes the stack trace parts, which
     * contains the authors name
     *
     * @param sender - The CommandSender who should receive the error report
     * @param err    - The error
     * @param plugin - The plugin where the error appeared
     * @param author - The author name, which will be searched in the error report
     */
    public static void error(CommandSender sender, Throwable err, String plugin, String author)
    {
        StringBuilder report = new StringBuilder();
        report.append("§4§l").append(plugin).append(" - ERROR REPORT - ").append(err.getClass().getSimpleName());
        if (err.getMessage() != null) report.append('\n').append(err.getMessage());
        int i = 0;
        boolean startrep = true;
        for (StackTraceElement el : err.getStackTrace())
        {
            boolean force = el.getClassName() != null && el.getClassName().contains(author);
            if (force) startrep = false;
            if (startrep || force)
                report.append("\n§c #").append(++i).append(": §eLINE §a").append(el.getLineNumber()).append("§e in FILE §6").append(el.getFileName()).append("§e (§7").append(el.getClassName()).append("§e.§b").append(el.getMethodName()).append("§e)");
        }
        String rep = report.toString();
        cs.sendMessage(rep);
        if (sender != null && sender != cs) sender.sendMessage(rep);
    }

    public static UUID getOnlineUUID(String name)
    {
        name = name.toLowerCase();
        UUID uid = uuidCache.get(name);
        if (uid == null)
        {
            GameProfile prof = MojangAPI.getProfile(name);
            if (prof == null)
            {
                cs.sendMessage("§cInvalid online player name: §e" + name + "§c. Using offline UUID.");
                return getOfflineUUID(name);
            }
            uid = prof.id;
            uuidCache.put(name, uid);
        }
        return uid;
    }

    public static UUID getOfflineUUID(String name)
    {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(utf8));
    }

    public static Player getPlayer(UUID uid)
    {
        Player plr = Bukkit.getPlayer(uid);
        if (plr != null) return plr;
        OfflinePlayer op = Bukkit.getOfflinePlayer(uid);
        if (op != null) return loadPlayer(uid);
        return null;
    }

    /**
     * Get GameProfile of the given player. The GameProfile contains the players name, UUID and skin.
     *
     * @param plr target player
     * @return the GameProfile of the target player
     */
    public static GameProfile getProfile(Player plr)
    {
        try
        {
            return new GameProfile(plr.getClass().getMethod("getProfile").invoke(plr));
        } catch (Throwable e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the UUID of an offline player based on his name.
     *
     * @param name name of the target player
     * @return The UUID of the requested player, or null if it was not found.
     */
    public static UUID getUUIDExact(String name)
    {
        Player plr = Bukkit.getPlayer(name);
        if (plr != null) return plr.getUniqueId();
        OfflinePlayer[] offlinePls = Bukkit.getOfflinePlayers();
        for (OfflinePlayer p : offlinePls)
        {
            if (p.getName() != null && p.getName().equals(name)) return p.getUniqueId();
        }
        return MojangAPI.getProfile(name).id;
    }

    public static void init(RedCore redCore)
    {
        pl = redCore;
        srv = getServer();
        pm = srv.getPluginManager();
        cs = srv.getConsoleSender();
        msg = srv.getMessenger();
        sm = srv.getServicesManager();
        sch = srv.getScheduler();
        js = new ScriptEngineManager().getEngineByName("JavaScript");
        pluginsF = Reflection.getField(pm.getClass(), "plugins");
        lookupNamesF = Reflection.getField(pm.getClass(), "lookupNames");
        registerVariables();
        Reflection.init();
        tp = new ProtocolImpl();
        try
        {
            tp.init();
        } catch (Throwable ignored)
        {
            schedulePacketAPI = true;
        }
        AnimationAPI.init();
        WrapperFactory.init();
        PacketInType.init();
        PacketOutType.init();
        ChatAPI.init();
        NBTApi.init();
        for (Player p : Bukkit.getOnlinePlayers())
            ScoreboardAPI.playerJoin(p);
        cm = new CustomCommandMap();
    }

    public static void registerVariables()
    {
        VariableAPI.handlers.put("eval", (plr, inside, oArgs) -> {
            String s = StringUtils.join(inside, "");
            try
            {
                return SU.js.eval(s);
            } catch (ScriptException e)
            {
                return "<eval:" + s + '>';
            }
        });
        VariableAPI.handlers.put("tobool", (plr, inside, oArgs) -> Boolean.valueOf(StringUtils.join(inside, "")));
        VariableAPI.handlers.put("tobyte", (plr, inside, oArgs) -> (byte) Double.valueOf(StringUtils.join(inside, "")).doubleValue());
        VariableAPI.handlers.put("toshort", (plr, inside, oArgs) -> (short) Double.valueOf(StringUtils.join(inside, "")).doubleValue());
        VariableAPI.handlers.put("toint", (plr, inside, oArgs) -> (int) Double.valueOf(StringUtils.join(inside, "")).doubleValue());
        VariableAPI.handlers.put("tolong", (plr, inside, oArgs) -> (long) Double.valueOf(StringUtils.join(inside, "")).doubleValue());
        VariableAPI.handlers.put("tofloat", (plr, inside, oArgs) -> Float.valueOf(StringUtils.join(inside, "")));
        VariableAPI.handlers.put("todouble", (plr, inside, oArgs) -> Double.valueOf(StringUtils.join(inside, "")));
        VariableAPI.handlers.put("tostr", (plr, inside, oArgs) -> StringUtils.join(inside, ""));
        VariableAPI.handlers.put("toarray", (plr, inside, oArgs) -> inside.toArray());
        VariableAPI.handlers.put("substr", (plr, inside, oArgs) -> {
            String[] s = StringUtils.join(inside, "").split(" ", 3);
            int from = Integer.valueOf(s[0]);
            int to = Integer.valueOf(s[1]);
            return s[2].substring(from < 0 ? s[2].length() + from : from, to < 0 ? s[2].length() + to : to);
        });
        VariableAPI.handlers.put("splits", (plr, inside, oArgs) -> StringUtils.join(inside, "").split(" "));
        VariableAPI.handlers.put("splitlen", (plr, inside, oArgs) -> {
            String[] s = StringUtils.join(inside, "").split(" ", 3);
            Integer max = Integer.valueOf(s[0]);
            String pref = SU.unescapeText(s[1]);
            String text = s[2];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i += max)
            {
                sb.append('\n').append(pref).append(text.substring(i, Math.min(text.length(), i + max)));
            }
            return sb.length() == 0 ? "" : sb.substring(1);
        });
        VariableAPI.handlers.put("noout", (plr, inside, oArgs) -> "");
        VariableAPI.handlers.put("booltest", (plr, inside, oArgs) -> {
            String[] s = StringUtils.join(inside, "").split(";");
            return Boolean.valueOf(s[0]) ? s[1] : s[2];
        });
        VariableAPI.handlers.put("args", (plr, inside, oArgs) -> {
            int id = Integer.valueOf(StringUtils.join(inside, ""));
            return oArgs[id];
        });
        VariableAPI.handlers.put("len", (plr, inside, oArgs) -> {
            Object o = inside.get(0);
            return o.getClass().isArray() ? Array.getLength(o) : ((Collection) o).size();
        });
        VariableAPI.handlers.put("iarg", (plr, inside, oArgs) -> {
            int id = Integer.valueOf(inside.get(0).toString());
            return inside.get(id);
        });
        VariableAPI.handlers.put("plr", (plr, inside, oArgs) -> Reflection.getData(plr, inside));
        VariableAPI.handlers.put("obj", (plr, inside, oArgs) -> Reflection.getData(oArgs[0], inside));
        VariableAPI.handlers.put("iobj", (plr, inside, oArgs) -> {
            Object obj = inside.remove(0);
            return Reflection.getData(obj, inside);
        });
        VariableAPI.handlers.put("tps", (plr, inside, oArgs) -> TPSMeter.tps);
        VariableAPI.handlers.put("real", (plr, inside, oArgs) -> System.currentTimeMillis());
        VariableAPI.handlers.put("formattime", (plr, inside, oArgs) -> {
            String str = StringUtils.join(inside, "");
            int id = str.indexOf(' ');
            long time = Long.valueOf(str.substring(0, id));
            String format = str.substring(id + 1);
            return new SimpleDateFormat(format).format(time);
        });
        VariableAPI.handlers.put("balf", (plr, inside, oArgs) -> econ.getBalance(plr));
    }

    /**
     * Unescape multi line to single line escaped text
     *
     * @param text multi line escaped text input
     * @return The unescaped text
     */

    public static String unescapeText(String text)
    {
        return (' ' + text).replaceAll("([^\\\\])_", "$1 ").replaceAll("([^\\\\])\\|", "$1\n").replaceAll("([^\\\\])\\\\([_\\|])", "$1$2").replace("\\\\", "\\").substring(1);
    }

    /**
     * Logs messages from the given plugin. You can use color codes in the msg.
     *
     * @param pl  - The plugin who wants to log the message
     * @param msg - The message which should be logged
     */
    public static void log(Plugin pl, Iterable<Object>... msg)
    {
        cs.sendMessage('[' + pl.getName() + "] " + StringUtils.join(msg, ", "));
    }

    /**
     * Convertion of a collection of player UUIDs to the Arraylist containing the player names matching with the UUIDs.
     *
     * @param uuids collection of player uuids which will be converted to names
     * @return the convertion result, which is an ArrayList of player names
     */
    public static ArrayList<String> namesFromUUIDs(Collection<UUID> uuids)
    {
        ArrayList<String> out = new ArrayList<>();
        for (UUID id : uuids)
        {
            out.add(getName(id));
        }
        return out;
    }

    /**
     * Get the name of an offline player based on it's UUID.
     *
     * @param id UUID of the target player
     * @return The name of the requested player or null if the name was not found.
     */
    public static String getName(UUID id)
    {
        Player plr = Bukkit.getPlayer(id);
        if (plr != null) return plr.getName();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        if (op == null) return MojangAPI.getProfile(id.toString()).name;
        return op.getName();
    }

    /**
     * Convertion of a collection of player names to the Arraylist containing the player UUIDs matching with the names.
     *
     * @param names collection of player names which will be converted to UUIDs
     * @return the convertion result, which is an ArrayList of player UUIDs
     */
    public static ArrayList<UUID> namesToUUIDs(Collection<String> names)
    {
        ArrayList<UUID> out = new ArrayList<>();
        for (String s : names)
        {
            out.add(getUUID(s));
        }
        return out;
    }

    /**
     * Optimizes color and formatting code usage in a string by removing redundant color/formatting codes
     *
     * @param in input message containing color and formatting codes
     * @return The color and formatting code optimized string
     */
    public static String optimizeColorCodes(String in)
    {
        StringBuilder out = new StringBuilder();
        StringBuilder oldFormat = new StringBuilder("§r");
        StringBuilder newFormat = new StringBuilder("§r");
        StringBuilder formatChange = new StringBuilder();
        String formatArchive = "";
        boolean color = false;
        for (char c : in.toCharArray())
        {
            if (color)
            {
                color = false;
                if (c >= 'k' && c <= 'o')
                {
                    int max = newFormat.length();
                    boolean add = true;
                    for (int i = 1; i < max; i += 2)
                    {
                        if (newFormat.charAt(i) == c)
                        {
                            add = false;
                            break;
                        }
                    }
                    if (add)
                    {
                        newFormat.append('§').append(c);
                        formatChange.append('§').append(c);
                    }
                    continue;
                }
                if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) c = 'f';
                newFormat.setLength(0);
                newFormat.append('§').append(c);
                formatChange.setLength(0);
                formatChange.append('§').append(c);
            } else if (c == '§') color = true;
            else if (c == '\u7777')
            {
                formatArchive = newFormat.toString();
            } else if (c == '\u7778')
            {
                newFormat.setLength(0);
                newFormat.append(formatArchive);
                formatChange.setLength(0);
                formatChange.append(formatArchive);
            } else
            {
                if (!newFormat.toString().equals(oldFormat.toString()))
                {
                    out.append(formatChange);
                    formatChange.setLength(0);
                    oldFormat.setLength(0);
                    oldFormat.append(newFormat);
                }
                out.append(c);
                if (c == '\n')
                {
                    formatChange.insert(0, oldFormat);
                    oldFormat.setLength(0);
                    newFormat.append(formatChange.toString());
                }
            }
        }
        return out.toString();
    }

    public static RedCore pl()
    {
        return pl;
    }

    public static void postInit()
    {
        pm.registerEvents(new SUListener(), pl);
        SU.pm.registerEvents(SU.tp, pl);
        initOfflinePlayerManager();
        vault = pm.getPlugin("Vault") != null;
        if (schedulePacketAPI) try
        {
            tp.init();
        } catch (Throwable e)
        {
            SU.error(cs, e, "RedCore", "com.redmancometh");
        }
        if (vault)
        {
            RegisteredServiceProvider<Permission> rspPerm = srv.getServicesManager().getRegistration(Permission.class);
            if (rspPerm != null) perm = rspPerm.getProvider();
            RegisteredServiceProvider<Chat> rspChat = srv.getServicesManager().getRegistration(Chat.class);
            if (rspChat != null) chat = rspChat.getProvider();
            RegisteredServiceProvider<Economy> rspEcon = srv.getServicesManager().getRegistration(Economy.class);
            if (rspEcon != null) econ = rspEcon.getProvider();
        }
        new TPSMeter().start();
        cs.sendMessage("§2[§aStartup§2]§a Started RedCore properly.");
    }

    public static void initOfflinePlayerManager()
    {
        try
        {
            Class mcServerClass = Reflection.getNMSClass("MinecraftServer");
            Class entityPlayerClass = Reflection.getNMSClass("EntityPlayer");
            Class craftPlayerClass = Reflection.getOBCClass("entity.CraftPlayer");
            Class pIMClass = Reflection.getNMSClass("PlayerInteractManager");
            Class worldServerClass = Reflection.getNMSClass("WorldServer");

            entityF = Reflection.getField(Reflection.getOBCClass("entity.CraftEntity"), "entity");
            pingF = Reflection.getNMSClass("EntityPlayer").getField("ping");
            mcServer = mcServerClass.getMethod("getServer", new Class[0]).invoke(null);
            playerInterractManagerC = pIMClass.getConstructor(Reflection.getNMSClass("World"));
            worldServer = mcServerClass.getMethod("getWorldServer", Integer.TYPE).invoke(mcServer, 0);
            entityPlayerC = entityPlayerClass.getConstructor(mcServerClass, worldServerClass, Reflection.getUtilClass("com.mojang.authlib.GameProfile"), pIMClass);
            getBukkitEntityM = entityPlayerClass.getMethod("getBukkitEntity");
            loadDataM = craftPlayerClass.getMethod("loadData");
            saveDataM = craftPlayerClass.getMethod("saveData");
        } catch (Throwable e)
        {
            log(pl, "§cError in initializing offline player manager.");
            error(cs, e, "RedCore", "com.redmancometh");
        }
    }

    /**
     * Logs messages from the given plugin. You can use color codes in the msg.
     *
     * @param pl  - The plugin who wants to log the message
     * @param msg - The message which should be logged
     */
    public static void log(Plugin pl, Object... msg)
    {
        cs.sendMessage('[' + pl.getName() + "] " + StringUtils.join(msg, ", "));
    }

    /**
     * Generates a random number between min (inclusive) and max (exclusive)
     *
     * @param min - Minimal value of the random number
     * @param max - Maximal value of the random number
     * @return A random double between min and max
     */
    public static double rand(double min, double max)
    {
        return rand.nextDouble() * Math.abs(max - min) + min;
    }

    /**
     * Generate a configurable random color
     *
     * @param minSaturation - Minimal saturation (0-1)
     * @param maxSaturation - Maximal saturation (0-1)
     * @param minLuminance  - Minimal luminance (0-1)
     * @param maxLuminance  - Maximal luminance (0-1)
     * @return The generated random color
     */
    public static Color randColor(double minSaturation, double maxSaturation, double minLuminance, double maxLuminance)
    {
        float hue = rand.nextFloat();
        double saturation = SU.rand.nextDouble() * (maxSaturation - minSaturation) + minSaturation;
        double luminance = SU.rand.nextDouble() * (maxLuminance - minLuminance) + minLuminance;
        java.awt.Color color = java.awt.Color.getHSBColor(hue, (float) saturation, (float) luminance);
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Save a active offline player. You should use this method when you have active an offline player
     * and you have changed some of it's data
     *
     * @param plr Loaded offline players Player object
     */
    public static void savePlayer(Player plr)
    {
        try
        {
            saveDataM.invoke(plr);
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Save files from the given plugins jar file to its subfolder in the plugins folder. The files will only be saved
     * if they doesn't exists in the plugins subfolder.
     *
     * @param pl        instane of the plugin
     * @param fileNames names of the saveable files
     */
    public static void saveResources(Plugin pl, String... fileNames)
    {
        Logger log = pl.getLogger();
        File df = pl.getDataFolder();
        ClassLoader cl = pl.getClass().getClassLoader();
        df.mkdir();
        for (String fn : fileNames)
        {
            try
            {
                File f = new File(df + File.separator + fn);
                if (!f.exists())
                {
                    if (fn.contains(File.separator))
                    {
                        new File(fn.substring(0, fn.lastIndexOf(File.separatorChar))).mkdirs();
                    }
                    InputStream is = cl.getResourceAsStream(fn);
                    if (is == null)
                    {
                        log.severe("Error, the requested file (" + fn + ") is missing from the plugins jar file.");
                    } else Files.copy(is, f.toPath());
                }
            } catch (Throwable e)
            {
                log.severe("Error, on copying file (" + fn + "): ");
                e.printStackTrace();
            }
        }
    }

    /**
     * Set maximum length of a String by cutting the redundant characters off from it
     *
     * @param in  input String
     * @param len maximum length
     * @return The cutted String, which will maximally len characters.
     */
    public static String setLength(String in, int len)
    {
        return in.length() > len ? in.substring(0, len) : in;
    }

    public static String[] splitPage(String text, int lines)
    {
        String[] d = text.split("\n");
        String[] out = new String[(d.length + (lines - 1)) / lines];
        for (int i = 0; i < d.length; i++)
        {
            if (i % lines == 0) out[i / lines] = d[i];
            else out[i / lines] += "\n" + d[i];
        }
        return out;
    }

    /**
     * Unloads a plugin
     *
     * @param p - The unloadable plugin
     */
    public static void unloadPlugin(Plugin p)
    {
        try
        {
            if (!p.isEnabled()) return;
            String pn = p.getName();
            for (Plugin p2 : pm.getPlugins())
            {
                PluginDescriptionFile pdf = p2.getDescription();
                if (pdf.getDepend() != null && pdf.getDepend().contains(pn) || pdf.getSoftDepend() != null && pdf.getSoftDepend().contains(pn))
                    unloadPlugin(p2);
            }
            pm.disablePlugin(p);
            ((List) pluginsF.get(pm)).remove(p);
            ((Map) lookupNamesF.get(pm)).remove(pn);
            for (Iterator it = knownCommands.entrySet().iterator(); it.hasNext(); )
            {
                Entry entry = (Entry) it.next();
                if ((entry.getValue() instanceof PluginCommand))
                {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == p) it.remove();
                }
            }
            ((URLClassLoader) p.getClass().getClassLoader()).close();
            System.gc();
        } catch (Throwable e)
        {
            error(cs, e, "RedCore", "com.redmancometh");
        }
    }
}