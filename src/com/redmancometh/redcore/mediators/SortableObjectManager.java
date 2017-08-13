package com.redmancometh.redcore.mediators;

import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redcore.databasing.SubDatabase;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface SortableObjectManager<T extends Defaultable<?>> extends BaseObjectManager<T>
{
    Map<Class<? extends SortableObjectManager>, Map<String, List<?>>> cache = new ConcurrentHashMap<>();

    default <U> List<U> getCache(String name)
    {
        Map<String, List<?>> c = getThisCache();
        List<?> lst = c.get(name);
        if (lst == null)
        {
            return new ArrayList<>();
        }
        return (List<U>) c;
    }

    default Map<String, List<?>> getThisCache()
    {
        Map<String, List<?>> c = cache.get(getClass());
        if (c == null)
        {
            c = new ConcurrentHashMap<>();
            cache.put(getClass(), c);
        }
        return c;
    }

    default void putSortableColumn(String name)
    {
        Map<String, List<?>> c = getThisCache();
        c.put(name, new ArrayList<>());
    }

    default void registerUpdate(long time)
    {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (String s : getThisCache().keySet())
            {
                updateCache(s);
            }
        }, time, time, TimeUnit.SECONDS);
    }

    default void updateCache(String name)
    {
        Map<String, List<?>> c = getThisCache();
        List<?> lst = c.get(name);
        if (lst == null)
        {
            return;
        }
        SubDatabase<UUID, T> sub = getSubDB();
        Criteria cr = sub.getFactory().getCurrentSession().createCriteria(getType());
        cr.addOrder(Order.desc(name));
        cr.setMaxResults(20); // Configure this? 20 should  be fine?
        CompletableFuture.runAsync(() -> {
            c.put(name, cr.list());
        });
    }
}
