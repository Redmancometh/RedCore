package com.redmancometh.redcore.databasing;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.exceptions.ObjectNotPresentException;

/**
 * 
 * @author Redmancometh
 *
 * @param <V> This is the type of object persisted 
 * @param <K> This is the type for the key which is used to lookup the object in both the cache and database.
 */
public class SubDatabase<K extends Serializable, V extends Defaultable>
{
    private SessionFactory factory;
    private final Class<V> type;
    public Function<K, V> defaultObjectBuilder;
    private boolean criteriaClass = false;
    private List<Criterion> criteria;
    LoadingCache<K, CompletableFuture<V>> cache = CacheBuilder.newBuilder().build(new CacheLoader<K, CompletableFuture<V>>()
    {
        @Override
        public CompletableFuture<V> load(K key)
        {
            return (CompletableFuture<V>) CompletableFuture.supplyAsync(() ->
            {
                try (Session session = factory.openSession())
                {
                    if (criteriaClass)
                    {
                        Criteria c = session.createCriteria(type);
                        criteria.forEach((criterion) -> c.add(criterion));
                    }
                    V result = session.get(type, key);
                    if (result == null) return defaultObjectBuilder.apply(key);
                    return result;
                }
                catch (SecurityException | IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                return null;
            }, RedCore.getInstance().getPool());
        }
    });

    public void insertObject(K key, V value)
    {
        try
        {
            System.out.println("Inserting: " + value + " AT: " + key);
            cache.asMap().put(key, CompletableFuture.supplyAsync(() -> value));
            System.out.println(cache.asMap().get(key).get() + " GET");
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    public SubDatabase(Class<V> type, SessionFactory factory)
    {
        super();
        this.factory = factory;
        this.type = type;
        this.defaultObjectBuilder = (key) ->
        {
            try
            {
                V v = type.newInstance();
                v.setDefaults(key);
                return v;
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
            return null;
        };
    }

    public Class<V> getMyType()
    {
        return this.type;
    }

    public CompletableFuture<V> getWithCriteria(K e, Criteria... criteria)
    {
        try
        {
            return cache.get(e);
        }
        catch (ExecutionException e1)
        {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * Get an object from the db async using CompletableFuture.
     * Call this with .thenAccept or something.
     * @param e
     * @return
     */
    public CompletableFuture<V> getObject(K e)
    {
        try
        {
            return cache.get(e);
        }
        catch (ExecutionException e1)
        {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * This is ONLY saved if the key is found in the cache!
     * @param e
     * @return
     * @throws ObjectNotPresentException 
     */
    public CompletableFuture<Void> saveFromKey(K e) throws ObjectNotPresentException
    {
        if (!cache.asMap().containsKey(e)) throw new ObjectNotPresentException(e.toString(), type);
        return CompletableFuture.runAsync(() ->
        {
            try (Session session = factory.openSession())
            {
                try
                {
                    session.beginTransaction();
                    cache.get(e).thenAccept((v) -> session.saveOrUpdate(v));
                    session.getTransaction().commit();
                    session.flush();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }

            }
        }, RedCore.getInstance().getPool());

    }

    /**
     * Save an object without purging it
     * @param e
     * @return
     */
    public CompletableFuture<Void> saveObject(V e)
    {
        return CompletableFuture.runAsync(() ->
        {
            try (Session session = factory.openSession())
            {
                session.beginTransaction();
                session.saveOrUpdate(e);
                session.getTransaction().commit();

            }
        }, RedCore.getInstance().getPool());
    }

    /**
     * Save a value and purge it from the cache.
     * @param e
     * @return
     * @throws ObjectNotPresentException
     */
    public CompletableFuture<Void> saveAndPurge(V e, UUID uuid) throws ObjectNotPresentException
    {
        return saveObject(e).thenRun(() -> cache.asMap().remove(uuid)).exceptionally((throwable) ->
        {
            throwable.printStackTrace();
            return null;
        });
    }

    /**
     * This method is an insta-return which assumes the value
     * is already loaded into the cache.
     * @param e
     * @return
     */
    public V get(K e)
    {
        try
        {
            CompletableFuture<V> future = cache.asMap().get(e);
            if (future == null) System.out.println("DAFUQ");
            return future.get(10, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e1)
        {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * Purge an object by using it's KEY object.
     * Warning: This is unchecked, and the object will not be saved.
     * @param e
     */
    public void purgeObject(K e)
    {
        cache.asMap().remove(e);
    }

    public SessionFactory getFactory()
    {
        return factory;
    }

    public List<Criterion> getCriteria()
    {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria)
    {
        this.criteria = criteria;
    }

    public boolean isCriteriaClass()
    {
        return criteriaClass;
    }

    public void setCriteriaClass(boolean criteriaClass)
    {
        this.criteriaClass = criteriaClass;
    }

}
