package com.redmancometh.redcore.sliceable;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.*;
import java.util.function.*;

/**
 * @author Redmancometh
 * <p>
 * Temporarily ignoring this class. Don't use.
 * <p>
 * This class provides a one-to-many batch relation.
 */
public class SlicedMap<K, V> extends LinkedMap<K, V> implements SliceableMap<K, V> {
    /**
     *
     */
    private static final long serialVersionUID = -6215265054291628933L;
    private Consumer<V> action;
    private int currentIndex;
    @SuppressWarnings("unused")
    private Map<Integer, Integer> indexMap = new HashMap();
    private BiConsumer<K, V> keyValueAction;
    private boolean tailConsumer;
    private Map<Integer, Boolean> tailConsumerMap = new HashMap();

    @Override
    public Consumer getAction()
    {
        return null;
    }

    @Override
    public void processTasks(int amount)
    {

    }

    @Override
    public BiConsumer<K, V> getKVAction()
    {
        return keyValueAction;
    }

    @Override
    public void processAction(K e, V e2)
    {

    }

    @Override
    public boolean isTailConsumer(int e)
    {
        return tailConsumerMap.get(e);
    }

    public void processForKey(K e)
    {

    }

    public void processItem(int amount)
    {
        if (amount == 1) {
            action.accept(getValue(currentIndex));
            currentIndex++;
            return;
        }
        for (int x = currentIndex; x < amount; x++) {
            action.accept(getValue(currentIndex));
            if (currentIndex + 1 > size()) {
                if (tailConsumer) {
                    processTasks(amount - x);
                    currentIndex = 0;
                    return;
                }
            }
            currentIndex++;
        }
    }

}
