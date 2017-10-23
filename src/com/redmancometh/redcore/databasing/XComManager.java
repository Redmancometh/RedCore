package com.redmancometh.redcore.databasing;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmancometh.redcore.mediators.ObjectManager;
import com.redmancometh.redcore.util.SpecialFuture;
import org.bukkit.entity.Player;

import java.lang.reflect.Modifier;
import java.util.UUID;

public abstract class XComManager<T extends Syncable> extends ObjectManager<T>
{

    protected String key;
    public Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

    public abstract void publish(T e);

    public abstract T getFromCache(UUID uuid);

    public XComManager(Class<T> type)
    {
        super(type);
    }

    protected String json(T e)
    {
        return gson.toJson(e);
    }

    @Override
    public SpecialFuture<?> saveAndPurge(T e, UUID uuid)
    {
        SpecialFuture.runAsync(() -> publish(e));
        return super.saveAndPurge(e, uuid);
    }

    @Override
    public SpecialFuture<T> saveAndPurge(Player p)
    {
        return getRecord(p.getUniqueId()).thenAccept((record) -> publish(record)).thenRun(() -> super.saveAndPurge(p));
    }

    @Override
    public SpecialFuture<T> save(Player p)
    {
        return getRecord(p.getUniqueId()).thenAccept((record) -> publish(record)).thenRun(() -> super.save(p));
    }

    @Override
    public SpecialFuture<Void> save(T e)
    {
        publish(e);
        return super.save(e);
    }

    @Override
    public SpecialFuture<T> save(UUID uuid)
    {
        return getRecord(uuid).thenAccept((record) -> publish(record)).thenRun(() -> super.save(uuid));
    }

    @Override
    public SpecialFuture<T> getRecord(UUID uuid)
    {

        T e = getFromCache(uuid);
        if (e != null)
        {
            super.insertObject(e.getUniqueId(), e);
            return SpecialFuture.supplyAsync(() -> e);
        }
        return super.getRecord(uuid);
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

}
