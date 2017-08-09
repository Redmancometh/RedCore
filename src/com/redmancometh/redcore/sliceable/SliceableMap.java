package com.redmancometh.redcore.sliceable;

import java.util.function.BiConsumer;

public interface SliceableMap<K, V> extends Sliceable<V> {
    BiConsumer<K, V> getKVAction();

    boolean isTailConsumer(int e);

    void processAction(K e, V e2);

}
