package com.redmancometh.redcore.config;

import com.redmancometh.redcore.RedPlugin;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.spigotutils.SU;
import com.redmancometh.redcore.util.StreamUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.FieldNamingPolicy;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

@Getter
public class ConfigManager<T>
{
    public static final Gson originalGson = new Gson();
    protected static StringAdapter stringAdapter;
    public static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).registerTypeHierarchyAdapter(Material.class, new MaterialAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(String.class, stringAdapter = new StringAdapter()).registerTypeHierarchyAdapter(ItemWrapper.class, (JsonDeserializer<ItemWrapper>) (
            el, type, context) ->
    {
        if (el instanceof JsonPrimitive) return new ItemWrapper(el.getAsString());
        return originalGson.fromJson(el, type);
    }).registerTypeHierarchyAdapter(StringSerializable.class, (JsonSerializer) (o, type,
            jsonSerializationContext) -> o == null ? null : new JsonPrimitive(o.toString())).registerTypeHierarchyAdapter(StringSerializable.class, (JsonDeserializer<StringSerializable>) (je, type,
                    cont) -> je.isJsonNull() ? null : (StringSerializable) Reflection.newInstance((Class) type, new Class[]
    { String.class }, je.getAsString())).registerTypeHierarchyAdapter(LanguageFile.class, new LanguageFile.LanguageFileAdapter()).registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter()).create();
    private Type confClass;
    private File configFile;
    private String configName;
    private T currentConfig;

    /**
     * w
     * Constructs a new ConfigFile from the given file and class type. After this method, the init method should be called.
     *
     * @param configName - Config files names
     * @param confClass  - Class used for storing the config in the memory
     */
    public ConfigManager(String configName, Class<T> confClass)
    {
        this.configName = configName;
        this.confClass = confClass;
    }

    /**
     * Constructs a new ConfigFile from the given file and class type and automatically initializes the config.
     *
     * @param pl         - Plugin instance used for auto saving the file
     * @param configName - Config files name
     * @param confClass  - Class used for storing the config in the memory
     */
    public ConfigManager(JavaPlugin pl, String configName, Type confClass)
    {
        this.configName = configName;
        this.confClass = confClass;
        init(pl);
    }

    protected ConfigManager()
    {

    }

    /**
     * put your gson config here.
     * total dirty typeless print be careful
     *
     * @param object
     */
    public static void debugPrint(Object object)
    {
        Class c = object.getClass();
        for (Field f : Reflection.getAllFields(object.getClass()))
        {
            try
            {
                try
                {
                    Object fieldValue = f.get(object);
                    SU.cs.sendMessage("�e" + f.getName() + "�b = �f" + gson.toJson(fieldValue, f.getGenericType()) + "\n");
                }
                catch (Throwable e)
                {
                    SU.cs.sendMessage("�cField �e" + f.getName() + "�c was unable to be retrieved!\n");
                }
            }
            catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }
    }

    public static ConfigManager tryDebugPrint(RedPlugin plugin)
    {
        try
        {
            Object config = Reflection.getMethod(plugin.getClass(), "getCfg").invoke(null);
            return (ConfigManager) config;
        }
        catch (Throwable e)
        {
            System.out.println("Need a static or instanced getCfg() method attached to this object to use this!");
            System.out.println("Or you fucked up something else.");
        }
        return null;
    }

    public void init(JavaPlugin plugin)
    {
        File configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists()) plugin.saveResource(configName, false);
        this.configFile = configFile;
        reload();
    }

    public void reload()
    {
        try (FileInputStream is = new FileInputStream(configFile))
        {
            String s = StreamUtils.streamToString(is).replaceAll("&([0-9a-fk-or])", "�$1");
            this.currentConfig = gson.fromJson(s, confClass);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public T getCurrentConfig()
    {
        return currentConfig;
    }

    public void setCurrentConfig(T currentConfig)
    {
        this.currentConfig = currentConfig;
    }

    public void save()
    {
        try (FileOutputStream os = new FileOutputStream(configFile))
        {
            StreamUtils.stringToStream(gson.toJson(currentConfig).replaceAll("�([0-9a-fk-or])", "&$1"), os);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static class EntityTypeAdapter extends TypeAdapter<EntityType>
    {
        @Override
        public void write(JsonWriter jsonWriter, EntityType material) throws IOException
        {
            if (material == null)
            {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(material.toString());
        }

        @Override
        public EntityType read(JsonReader jsonReader) throws IOException
        {
            if (jsonReader.peek() == JsonToken.NULL)
            {
                jsonReader.nextNull();
                return null;
            }
            return EntityType.valueOf(jsonReader.nextString().toUpperCase().replace(" ", "_"));
        }
    }

    private static class LocationAdapter extends TypeAdapter<Location>
    {

        @Override
        public void write(JsonWriter jsonWriter, Location location) throws IOException
        {
            jsonWriter.value(location.toString());
        }

        @Override
        public Location read(JsonReader jsonReader) throws IOException
        {
            int x = 0, y = 0, z = 0;
            World w = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext())
            {
                switch (jsonReader.nextName())
                {
                    case "world":
                        w = Bukkit.getWorld(jsonReader.nextString());
                    case "x":
                        x = Integer.parseInt(jsonReader.nextString());
                        break;
                    case "y":
                        y = Integer.parseInt(jsonReader.nextString());
                        break;
                    case "z":
                        z = Integer.parseInt(jsonReader.nextString());
                        break;
                }
            }
            jsonReader.endObject();
            return new Location(w, x, y, z);
        }
    }

    private static class MaterialAdapter extends TypeAdapter<Material>
    {
        @Override
        public void write(JsonWriter jsonWriter, Material material) throws IOException
        {
            if (material == null)
            {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(material.toString());
        }

        @Override
        public Material read(JsonReader jsonReader) throws IOException
        {
            if (jsonReader.peek() == JsonToken.NULL)
            {
                jsonReader.nextNull();
                return null;
            }
            Material m = Material.getMaterial(jsonReader.nextString().toUpperCase().replace(" ", "_"));
            if (m == null) m = Material.getMaterial(Integer.parseInt(jsonReader.nextString()));
            return m;
        }
    }

    private static class PotionEffectAdapter extends TypeAdapter<PotionEffect>
    {
        @Override
        public void write(JsonWriter jsonWriter, PotionEffect o) throws IOException
        {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getType().getName()).append(":").append(o.getAmplifier()).append(":").append(o.getDuration());
            if (o.hasParticles()) sb.append(":P");
            if (o.isAmbient()) sb.append(":A");
            jsonWriter.value(sb.toString());
        }

        @Override
        public PotionEffect read(JsonReader jsonReader) throws IOException
        {
            if (jsonReader.peek() == JsonToken.NULL)
            {
                jsonReader.nextNull();
                return null;
            }
            String[] d = jsonReader.nextString().split("[ :]", 4);
            PotionEffectType pet = PotionEffectType.getByName(d[0]);
            return new PotionEffect(pet, Integer.valueOf(d[1]), Integer.valueOf(d[2]), d.length > 3 && d[3].contains("A"), d.length > 3 && d[3].contains("P"));
        }
    }

    protected static class StringAdapter extends TypeAdapter<String>
    {
        @Override
        public void write(JsonWriter jsonWriter, String s) throws IOException
        {
            if (s == null)
            {
                jsonWriter.nullValue();
                return;
            }
            String[] d = s.split("\n");
            if (d.length == 1)
            {
                jsonWriter.value(s);
            }
            else
            {
                jsonWriter.beginArray();
                for (String s2 : d)
                    jsonWriter.value(s2);
                jsonWriter.endArray();
            }
        }

        @Override
        public String read(JsonReader jsonReader) throws IOException
        {
            JsonToken jt = jsonReader.peek();
            if (jt == JsonToken.NULL)
            {
                jsonReader.nextNull();
                return null;
            }
            else if (jt == JsonToken.BEGIN_ARRAY)
            {
                jsonReader.beginArray();
                StringBuilder sb = new StringBuilder();
                while (jsonReader.peek() != JsonToken.END_ARRAY)
                    sb.append('\n').append(jsonReader.nextString());
                jsonReader.endArray();
                return sb.length() == 0 ? "" : sb.substring(1);
            }
            return jsonReader.nextString();
        }
    }
}
