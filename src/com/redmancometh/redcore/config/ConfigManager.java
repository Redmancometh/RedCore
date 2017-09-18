package com.redmancometh.redcore.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.redmancometh.redcore.RedPlugin;

import lombok.Getter;

public class ConfigManager<T>
{
    @Getter
    private Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).registerTypeHierarchyAdapter(Material.class, new MaterialAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter()).create();

    private String configName;
    private T currentConfig;
    private Class<T> confClass;
    private File configFile;

    public ConfigManager(String configName, Class<T> confClass)
    {
        this.configName = configName;
        this.confClass = confClass;
    }

    public void init(JavaPlugin plugin)
    {
        File configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists())
        {
            plugin.saveResource(configName, false);
        }
        this.configFile = configFile;
        reload();
    }

    public void reload()
    {
        try (FileInputStream fileIn = new FileInputStream(configFile))
        {
            try (InputStreamReader in = new InputStreamReader(fileIn))
            {
                this.currentConfig = getGson().fromJson(in, confClass);
            }
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

    public static ConfigManager tryDebugPrint(RedPlugin plugin)
    {
        try
        {
            Method m = plugin.getClass().getDeclaredMethod("getCfg");
            m.setAccessible(true);
            Object config = m.invoke(null, new Object[0]);
            return (ConfigManager) config;
        }
        catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            System.out.println("Need a static or instanced getCfg() method attached to this object to use this!");
            System.out.println("Or you fucked up something else.");
        }
        return null;
    }

    /**
     * put your gson config here.
     * total dirty typeless print be careful
     * @param object
     */
    public static void debugPrint(Object object)
    {
        Class c = object.getClass();
        for (Field f : c.getDeclaredFields())
        {
            try
            {
                Object fieldValue = f.get(object);
                System.out.println(f.getName() + " is null?");
                System.out.println("\n" + fieldValue == null);
                System.out.println("Attempting to retrieve value for field...");
                try
                {
                    System.out.println("\nValue: " + fieldValue);
                }
                catch (Throwable t)
                {
                    System.out.println("Field: " + f.getName() + " was unable to be retrieved!");
                    continue;
                }
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
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

}
