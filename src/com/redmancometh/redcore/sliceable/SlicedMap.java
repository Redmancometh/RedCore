package com.redmancometh.redcore.sliceable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.collections4.map.LinkedMap;

/**
 * 
 * @author Redmancometh
 *
 *  Temporarily ignoring this class. Don't use.
 *
 *  This class provides a one-to-many batch relation. 
 *  
 */
public class SlicedMap<K, V> extends LinkedMap<K, V> implements SliceableMap<K, V>
{
    private int currentIndex;

    private Consumer<V> action;
    private BiConsumer<K, V> keyValueAction;
    private boolean tailConsumer;
    @SuppressWarnings("unused")
    private Map<Integer, Integer> indexMap = new HashMap();
    private Map<Integer, Boolean> tailConsumerMap = new HashMap();

    /**
     * 
     */
    private static final long serialVersionUID = -6215265054291628933L;

    public void processForKey(K e)
    {

    }

    public void processItem(int amount)
    {
        if (amount == 1)
        {
            action.accept(getValue(currentIndex));
            currentIndex++;
            return;
        }
        for (int x = currentIndex; x < amount; x++)
        {
            action.accept(getValue(currentIndex));
            if (currentIndex + 1 > size())
            {
                if (tailConsumer)
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

}
