package com.redmancometh.redcore.sliceable;

import java.util.function.BiConsumer;

public interface SliceableMap<K, V> extends Sliceable<V>
{
    public abstract BiConsumer<K, V> getKVAction();

    public abstract void processAction(K e, V e2);

    public abstract boolean isTailConsumer(int e);

}
