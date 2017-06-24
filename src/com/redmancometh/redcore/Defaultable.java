package com.redmancometh.redcore;

import java.lang.reflect.Modifier;
import java.util.UUID;

import org.bukkit.craftbukkit.libs.com.google.gson.FieldNamingPolicy;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;

public interface Defaultable<K>
{
    Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

    public abstract void setDefaults(K e);

    public default String toJsonString()
    {
        return gson.toJson(this);
    }

    public UUID getUniqueId();
}
