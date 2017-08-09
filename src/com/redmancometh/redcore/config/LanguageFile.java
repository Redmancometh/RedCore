package com.redmancometh.redcore.config;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.spigotutils.SU;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

import static com.redmancometh.redcore.api.ChatAPI.ChatMessageType.ACTION_BAR;
import static com.redmancometh.redcore.api.ChatAPI.ChatMessageType.SYSTEM;

public class LanguageFile extends ConfigManager<HashMap<String, String>> {
    private HashMap<String, String> data;

    public LanguageFile(JavaPlugin pl, String fn) {
        super(pl, fn, Reflection.getField(LanguageFile.class, "data").getType());
        data = super.getCurrentConfig();
    }

    public static String getServerLanguage() {
        return "en";
    }

    public void abmsg(CommandSender plr, String key, Object... vars) {
        ChatAPI.sendJsonMsg(ACTION_BAR, get(plr, key, vars));
    }

    public String get(CommandSender plr, String key, Object... vars) {
        String lang = getLanguage(plr);
        String slang = getLanguage(plr);
        String str = data.get(lang + "." + key);
        if (str == null && !lang.equals(slang))
            str = data.get(slang + "." + key);
        return str == null ? ("§cMissing:§e " + lang + "." + key) : SU.fillVariables(str, vars);
    }

    public static String getLanguage(CommandSender plr) {
        return "en";
    }

    public void msg(String prefix, CommandSender plr, String key, Object... vars) {
        ChatAPI.sendJsonMsg(SYSTEM, prefix + get(plr, key, vars));
    }

    public void msg(CommandSender plr, String key, Object... vars) {
        ChatAPI.sendJsonMsg(SYSTEM, get(plr, "prefix") + get(plr, key, vars));
    }
}
