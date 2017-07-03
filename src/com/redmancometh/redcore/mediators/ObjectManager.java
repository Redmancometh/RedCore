package com.redmancometh.redcore.mediators;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.databasing.SubDatabase;
import com.redmancometh.redcore.exceptions.ObjectNotPresentException;

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

<<<<<<< HEAD
    /**
     * Don't use this if you can avoid.
     * @return
     */
=======
>>>>>>> origin/master
    public SubDatabase<UUID, T> getSubDB()
    {
        return RedCore.getInstance().getMasterDB().getSubDBForType(type);
    }

    public CompletableFuture<T> getRecord(UUID uuid)
    {
        return getSubDB().getObject(uuid);
    }

    public CompletableFuture<Void> save(T e)
    {
        return RedCore.getInstance().getMasterDB().getSubDBForType(type).saveObject(e);
    }

    public CompletableFuture<Void> save(Player p)
    {
        return save(p.getUniqueId());
    }

    public CompletableFuture<Void> save(UUID uuid)
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

    public CompletableFuture<Void> saveAndPurge(Player p)
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
