package com.redmancometh.redcore.config;

import com.redmancometh.redcore.RedPlugin;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.util.StreamUtils;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.com.google.gson.*;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.*;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.*;

@Getter
public class ConfigManager<T> {
    private static Gson originalGson = new Gson();
    private static Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .registerTypeHierarchyAdapter(Material.class, new MaterialAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(ItemWrapper.class, (JsonDeserializer<ItemWrapper>) (el, type, context) -> {
                if (el instanceof JsonPrimitive)
                    return new ItemWrapper(el.getAsString());
                return originalGson.fromJson(el, type);
            })
            .registerTypeHierarchyAdapter(StringSerializable.class, (JsonSerializer)
                    (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString()))
            .registerTypeHierarchyAdapter(StringSerializable.class, (JsonDeserializer<StringSerializable>) (je, type, cont)
                    -> (StringSerializable) Reflection.newInstance((Class) type, new Class[]{String.class}, je.getAsString()))
            .registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter()).create();

    private String configName;
    private T currentConfig;
    private Class<T> confClass;
    private File configFile;

    public ConfigManager(String configName, Class<T> confClass) {
        this.configName = configName;
        this.confClass = confClass;
    }

    /**
     * put your gson config here.
     * total dirty typeless print be careful
     *
     * @param object
     */
    public static void debugPrint(Object object) {
        Class c = object.getClass();
        for (Field f : c.getDeclaredFields()) {
            try {
                Object fieldValue = f.get(object);
                System.out.println(f.getName() + " is null?");
                System.out.println("\n" + fieldValue == null);
                System.out.println("Attempting to retrieve value for field...");
                try {
                    System.out.println("\nValue: " + fieldValue);
                } catch (Throwable t) {
                    System.out.println("Field: " + f.getName() + " was unable to be retrieved!");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConfigManager tryDebugPrint(RedPlugin plugin) {
        try {
            Method m = plugin.getClass().getDeclaredMethod("getCfg");
            m.setAccessible(true);
            Object config = m.invoke(null);
            return (ConfigManager) config;
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println("Need a static or instanced getCfg() method attached to this object to use this!");
            System.out.println("Or you fucked up something else.");
        }
        return null;
    }

    public T getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(T currentConfig) {
        this.currentConfig = currentConfig;
    }

    public void init(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists()) {
            plugin.saveResource(configName, false);
        }
        this.configFile = configFile;
        reload();
    }

    public void reload() {
        try (FileInputStream is = new FileInputStream(configFile)) {
            String s = StreamUtils.streamToString(is).replaceAll("&([0-9a-fk-or])", "§$1");
            this.currentConfig = gson.fromJson(s, confClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class EntityTypeAdapter extends TypeAdapter<EntityType> {
        @Override
        public void write(JsonWriter jsonWriter, EntityType material) throws IOException {
            if (material == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(material.toString());
        }

        @Override
        public EntityType read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return EntityType.valueOf(jsonReader.nextString().toUpperCase().replace(" ", "_"));
        }
    }

    private static class LocationAdapter extends TypeAdapter<Location> {

        @Override
        public void write(JsonWriter jsonWriter, Location location) throws IOException {
            jsonWriter.value(location.toString());
        }

        @Override
        public Location read(JsonReader jsonReader) throws IOException {
            int x = 0, y = 0, z = 0;
            World w = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
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

    private static class MaterialAdapter extends TypeAdapter<Material> {
        @Override
        public void write(JsonWriter jsonWriter, Material material) throws IOException {
            if (material == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(material.toString());
        }

        @Override
        public Material read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            Material m = Material.getMaterial(jsonReader.nextString().toUpperCase().replace(" ", "_"));
            if (m == null) m = Material.getMaterial(Integer.parseInt(jsonReader.nextString()));
            return m;
        }
    }

}
