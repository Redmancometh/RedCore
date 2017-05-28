package com.redmancometh.redcore.sliceable;

import java.util.function.Consumer;

public interface Sliceable<V>
{
    public abstract Consumer<V> getAction();
    public abstract void processTasks(int amount);
}
