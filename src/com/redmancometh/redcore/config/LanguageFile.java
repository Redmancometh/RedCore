package com.redmancometh.redcore.config;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.spigotutils.SU;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;

import static com.redmancometh.redcore.api.ChatAPI.ChatMessageType.ACTION_BAR;
import static com.redmancometh.redcore.api.ChatAPI.ChatMessageType.SYSTEM;

public class LanguageFile extends ConfigManager<LanguageFile>
{
    private HashMap<String, String> data = new HashMap<>();

    public LanguageFile(JavaPlugin pl, String fn)
    {
        super(pl, fn, LanguageFile.class);
        data = super.getCurrentConfig().data;
    }

    private LanguageFile()
    {
        super();
    }

    public static String getServerLanguage()
    {
        return "en";
    }

    public void abmsg(CommandSender plr, String key, Object... vars)
    {
        if (plr == null || key == null) return;
        ChatAPI.sendJsonMsg(ACTION_BAR, get(plr, key, vars));
    }

    public String get(CommandSender plr, String key, Object... vars)
    {
        if (plr == null || key == null) return null;
        String lang = getLanguage(plr);
        String slang = getLanguage(plr);
        String str = data.get(lang + "." + key);
        if (str == null && !lang.equals(slang)) str = data.get(slang + "." + key);
        return str == null ? ("§cMissing:§e " + lang + "." + key) : SU.fillVariables(str, vars);
    }

    public static String getLanguage(CommandSender plr)
    {
        return "en";
    }

    public void msg(String prefix, CommandSender plr, String key, Object... vars)
    {
        if (prefix == null) prefix = "";
        if (plr == null || key == null) return;
        if (plr instanceof Player) ChatAPI.sendJsonMsg(SYSTEM, prefix + get(plr, key, vars), (Player) plr);
        else plr.sendMessage(ChatTag.stripExtras(prefix + get(plr, key, vars)));
    }

    public void msg(CommandSender plr, String key, Object... vars)
    {
        if (plr == null || key == null) return;
        if (plr instanceof Player) ChatAPI.sendJsonMsg(SYSTEM, get(plr, key, vars), (Player) plr);
        else plr.sendMessage(ChatTag.stripExtras(get(plr, "prefix") + get(plr, key, vars)));

    }

    protected static class LanguageFileAdapter extends TypeAdapter<LanguageFile>
    {

        @Override
        public void write(JsonWriter jsonWriter, LanguageFile lf) throws IOException
        {
            throw new IOException("Language files are currently not saveable.");
            //TODO Make language files saveable
        }

        @Override
        public LanguageFile read(JsonReader jsonReader) throws IOException
        {
            LanguageFile lf = new LanguageFile();
            String rootKey = "";
            String key = "";
            while (true)
            {
                JsonToken token = jsonReader.peek();
                switch (token)
                {
                    case NULL:
                    case NUMBER:
                    case BOOLEAN:
                        throw new IOException("Null, boolean and number tokens are not allowed in language file.");
                    case NAME:
                        key = rootKey.isEmpty() ? jsonReader.nextName() : rootKey + "." + jsonReader.nextName();
                        break;
                    case BEGIN_ARRAY:
                    case STRING:
                        lf.data.put(key, ConfigManager.stringAdapter.read(jsonReader));
                        break;
                    case BEGIN_OBJECT:
                        jsonReader.beginObject();
                        rootKey = key;
                        break;
                    case END_OBJECT:
                        jsonReader.endObject();
                        if (rootKey.equals("")) return lf;
                        int id = rootKey.lastIndexOf('.');
                        if (id == -1) rootKey = "";
                        else rootKey = rootKey.substring(0, id);
                        break;
                    case END_DOCUMENT:
                        return lf;
                }
            }
        }
    }
}
