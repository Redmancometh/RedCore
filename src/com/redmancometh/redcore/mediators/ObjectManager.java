package com.redmancometh.redcore.mediators;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.databasing.SubDatabase;
import com.redmancometh.redcore.exceptions.ObjectNotPresentException;
import com.redmancometh.redcore.util.SpecialFuture;

public class ObjectManager<T extends Defaultable<?>> implements BaseObjectManager<T>
{
    private final Class<T> type;

    public Class<T> getType()
    {
        return type;
    }

    public ObjectManager(Class<T> type)
    {
        this.type = type;
    }

    public void insertObject(UUID key, T value)
    {
        getSubDB().insertObject(key, value);
    }

    public T getBlocking(UUID key)
    {
        return getSubDB().get(key);
    }

    /**
     * Don't use this if you can avoid.
     * @return
     */
    public SubDatabase<UUID, T> getSubDB()
    {
        return RedCore.getInstance().getMasterDB().getSubDBForType(type);
    }

    public SpecialFuture<T> getRecord(UUID uuid)
    {
        return getSubDB().getObject(uuid);
    }

    public CompletableFuture<Void> save(T e)
    {
        return RedCore.getInstance().getMasterDB().getSubDBForType(type).saveObject(e);
    }

    public SpecialFuture<T> save(Player p)
    {
        return save(p.getUniqueId());
    }

    public SpecialFuture<T> save(UUID uuid)
    {
        return getSubDB().getObject(uuid).thenAccept((record) -> getSubDB().saveObject(record));
    }

    public CompletableFuture<Void> saveAndPurge(T e, UUID uuid)
    {
        try
        {
            return getSubDB().saveAndPurge(e, uuid);
        }
        catch (ObjectNotPresentException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public void delete(T e)
    {
        getSubDB().deleteObject(e);
    }

    public SpecialFuture<T> saveAndPurge(Player p)
    {
        UUID uuid = p.getUniqueId();
        return getSubDB().getObject(uuid).thenAccept((record) ->
        {
            try
            {
                getSubDB().saveAndPurge(record, uuid);
            }
            catch (ObjectNotPresentException e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public ObjectManager<T> getThis()
    {
        return this;
    }
}
