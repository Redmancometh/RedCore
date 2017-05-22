package com.redmancometh.redcore.databasing;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    LoadingCache<K, CompletableFuture<V>> cache = CacheBuilder.newBuilder().build(new CacheLoader<K, CompletableFuture<V>>()
    {
        @Override
        public CompletableFuture<V> load(K key)
        {
            return (CompletableFuture<V>) CompletableFuture.supplyAsync(() ->
            {
                try (Session session = factory.openSession())
                {
                    V result = session.get(type, key);
                    if (result == null)
                    {
                        System.out.println("RESULT IS NULL!");
                        System.out.println(defaultObjectBuilder == null);
                        System.out.println(defaultObjectBuilder.apply(key));
                        return defaultObjectBuilder.apply(key);
                    }
                    System.out.println("GOT OUT?!");
                    return result;
                }
            });
        }

    });

    public SubDatabase(Class<V> type, SessionFactory factory)
    {
        super();
        this.factory = factory;
        this.type = type;
        this.defaultObjectBuilder = (key) ->
        {
            System.out.println("KEY: " + key);
            try
            {
                System.out.println("TYPE: " + type);
                V v = type.newInstance();
                System.out.println("V: " + v);
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
        System.out.println("SAVING FROM KEY");
        if (!cache.asMap().containsKey(e)) throw new ObjectNotPresentException(e.toString(), type);
        return CompletableFuture.runAsync(() ->
        {
            System.out.println("SAVING FROM KEY INSIDE ASYNC");
            try (Session session = factory.openSession())
            {
                System.out.println("SESSION START");
                session.beginTransaction();
                System.out.println("TRANSACTION BEGAN");
                try
                {
                    System.out.println("SAVE CALL");
                    session.save(e);
                    System.out.println("END SAVE CALL");
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                    System.out.println("FAILED TO SAVE!");
                }
                System.out.println("SAVED ABOUT TO COMMIT!");
                session.getTransaction().commit();
                System.out.println("DONE COMMITTING");
            }
            System.out.println("AT END OF TRY");
        }, RedCore.getInstance().getPool());

    }

    public CompletableFuture<Void> saveObject(V e)
    {
        System.out.println("SAVING: " + e);
        return CompletableFuture.runAsync(() ->
        {
            System.out.println("INSIDE COMPLETABLEFUTURE");
            try (Session session = factory.openSession())
            {
                System.out.println("SAVE THE SHIT?!");
                session.beginTransaction();
                session.saveOrUpdate(e);
                session.persist(e);
                session.getTransaction().commit();
            }
        }, RedCore.getInstance().getPool());
    }

    public CompletableFuture<Void> saveAndPurge(K e) throws ObjectNotPresentException
    {
        return saveFromKey(e).thenRun(() -> cache.asMap().remove(e));
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

}
