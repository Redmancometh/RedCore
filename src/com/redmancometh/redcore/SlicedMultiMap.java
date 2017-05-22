package com.redmancometh.redcore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.collections4.map.LinkedMap;

/**
 * 
 * @author Redmancometh
 * This provides a one-to-many time slicing system
 * You can either iterate through values directly, or you can slice a key's valueset up
 */
public class SlicedMultiMap<K, V> extends LinkedMap<K, List<V>> implements SliceableMap<K, List<V>>
{

    private static final long serialVersionUID = 1L;
    private Consumer<List<V>> valueConsumer;
    private BiConsumer<K, List<V>> kvConsumer;
    private Map<Integer, Integer> indexMap = new HashMap();
    private Map<Integer, Boolean> tailConsumerMap = new HashMap();
    private int currentIndex = 0;
    private boolean tailConsumer = false;

    public boolean isTailConsumer(int e)
    {
        return tailConsumerMap.get(e);
    }

    public void advanceIndexForKey(int e)
    {
        indexMap.compute(e, (k, v) -> v + 1);
    }

    public int getIndexForKey(K e)
    {
        return indexMap.get(e);
    }

    public void processItemsForKey(K e, int amount)
    {
        
    }

    @Override
    public void processAction(K e, List<V> e2)
    {
        kvConsumer.accept(e, e2);
    }

    @Override
    public Consumer<List<V>> getAction()
    {
        return valueConsumer;
    }

    @Override
    public void processTasks(int amount)
    {
        if (amount == 1)
        {
            LinkEntry<K, List<V>> entry = getEntry(currentIndex);
            processAction(entry.getKey(), entry.getValue());
            currentIndex++;
            if (!(currentIndex < size())) currentIndex = 0;
            return;
        }
        for (int x = currentIndex; x < amount; x++)
        {
            LinkEntry<K, List<V>> entry = getEntry(currentIndex);
            processAction(entry.getKey(), entry.getValue());
            if (currentIndex + 1 > size())
            {
                if (tailConsumer || isTailConsumer(currentIndex))
                {
                    processTasks(amount - x);
                    currentIndex = 0;
                    return;
                }
            }
            currentIndex++;
        }
    }

    @Override
    public BiConsumer<K, List<V>> getKVAction()
    {
        return kvConsumer;
    }

}
