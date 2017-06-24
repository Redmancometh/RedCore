package com.redmancometh.redcore.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.FieldNamingPolicy;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager<T>
{
    private Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).registerTypeHierarchyAdapter(Material.class, new MaterialAdapter()).create();

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
                this.currentConfig = gson.fromJson(in, confClass);
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
            return Material.valueOf(jsonReader.nextString().toUpperCase().replace(" ", "_"));
        }

    }

}
