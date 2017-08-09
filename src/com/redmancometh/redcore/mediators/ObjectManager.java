package com.redmancometh.redcore.mediators;

import com.redmancometh.redcore.*;
import com.redmancometh.redcore.databasing.SubDatabase;
import com.redmancometh.redcore.exceptions.ObjectNotPresentException;
import com.redmancometh.redcore.util.SpecialFuture;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ObjectManager<T extends Defaultable<?>> implements BaseObjectManager<T> {
    private final Class<T> type;

    public ObjectManager(Class<T> type)
    {
        this.type = type;
    }

    public void delete(T e)
    {
        getSubDB().deleteObject(e);
    }

    public T getBlocking(UUID key)
    {
        return getSubDB().get(key);
    }

    /**
     * Don't use this if you can avoid.
     *
     * @return
     */
    public SubDatabase<UUID, T> getSubDB()
    {
        return RedCore.getInstance().getMasterDB().getSubDBForType(type);
    }

    @Override
    public ObjectManager<T> getThis()
    {
        return this;
    }

    public Class<T> getType()
    {
        return type;
    }

    public SpecialFuture<T> getRecord(UUID uuid)
    {
        return getSubDB().getObject(uuid);
    }

    public void insertObject(UUID key, T value)
    {
        getSubDB().insertObject(key, value);
    }

    public SpecialFuture<Void> save(T e)
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

    public SpecialFuture<T> saveAndPurge(Player p)
    {
        UUID uuid = p.getUniqueId();
        return getSubDB().getObject(uuid).thenAccept((record) ->
        {
            try {
                getSubDB().saveAndPurge(record, uuid);
            } catch (ObjectNotPresentException e) {
                e.printStackTrace();
            }
        });
    }

    public SpecialFuture<?> saveAndPurge(T e, UUID uuid)
    {
        try {
            return getSubDB().saveAndPurge(e, uuid);
        } catch (ObjectNotPresentException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
