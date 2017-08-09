package com.redmancometh.redcore;

import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.lang.reflect.Modifier;
import java.util.UUID;

public interface Defaultable<K> {
    Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

    UUID getUniqueId();

    void setDefaults(K e);

    default String toJsonString()
    {
        return gson.toJson(this);
    }
}
