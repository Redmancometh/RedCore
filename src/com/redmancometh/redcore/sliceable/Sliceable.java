package com.redmancometh.redcore.sliceable;

import java.util.function.Consumer;

public interface Sliceable<V>
{
    Consumer<V> getAction();

    void processTasks(int amount);
}
