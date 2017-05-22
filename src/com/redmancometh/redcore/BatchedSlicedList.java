package com.redmancometh.redcore;

import java.util.function.Consumer;

public class BatchedSlicedList<T> extends SlicedList<T>
{

    private static final long serialVersionUID = -313112658500611493L;
    private int batchSize;

    public BatchedSlicedList(Consumer<T> action, boolean tailConsumer, int batchSize)
    {
        super(action, tailConsumer);
        this.batchSize = batchSize;
    }

    public void executeBatch()
    {
        this.processTasks(batchSize);
    }
}
