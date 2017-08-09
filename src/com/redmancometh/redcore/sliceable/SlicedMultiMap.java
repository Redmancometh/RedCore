package com.redmancometh.redcore.sliceable;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.*;
import java.util.function.*;

/**
 * @author Redmancometh
 * This provides a one-to-many time slicing system
 * You can either iterate through values directly, or you can slice a key's valueset up
 */
public class SlicedMultiMap<K, V> extends LinkedMap<K, List<V>> implements SliceableMap<K, List<V>> {

    private static final long serialVersionUID = 1L;
    private int currentIndex = 0;
    private Map<Integer, Integer> indexMap = new HashMap();
    private BiConsumer<K, List<V>> kvConsumer;
    private boolean tailConsumer = false;
    private Map<Integer, Boolean> tailConsumerMap = new HashMap();
    private Consumer<List<V>> valueConsumer;

    public void advanceIndexForKey(int e)
    {
        indexMap.compute(e, (k, v) -> v + 1);
    }

    @Override
    public Consumer<List<V>> getAction()
    {
        return valueConsumer;
    }

    public int getIndexForKey(K e)
    {
        return indexMap.get(e);
    }

    public boolean isTailConsumer(int e)
    {
        return tailConsumerMap.get(e);
    }

    @Override
    public BiConsumer<K, List<V>> getKVAction()
    {
        return kvConsumer;
    }

    @Override
    public void processAction(K e, List<V> e2)
    {
        kvConsumer.accept(e, e2);
    }

    public void processItemsForKey(K e, int amount)
    {

    }

    @Override
    public void processTasks(int amount)
    {
        if (amount == 1) {
            LinkEntry<K, List<V>> entry = getEntry(currentIndex);
            processAction(entry.getKey(), entry.getValue());
            currentIndex++;
            if (!(currentIndex < size())) currentIndex = 0;
            return;
        }
        for (int x = currentIndex; x < amount; x++) {
            LinkEntry<K, List<V>> entry = getEntry(currentIndex);
            processAction(entry.getKey(), entry.getValue());
            if (currentIndex + 1 > size()) {
                if (tailConsumer || isTailConsumer(currentIndex)) {
                    processTasks(amount - x);
                    currentIndex = 0;
                    return;
                }
            }
            currentIndex++;
        }
    }

}
