package com.redmancometh.redcore;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SlicedCollection<T> extends ArrayList<T>
{

    private static final long serialVersionUID = -5221471063452246214L;
    private Consumer<T> action;
    private int currentIndex = 0;
    private boolean tailConsumer = false;

    /**
     * 
     * @param action This is the action to perform on the object of type T when processAction is called
     * @param tailConsumer If this is true when the processTasks method reaches the end of the list it will wrap around
     * and go back to 0 until the amount is fulfilled
     */
    public SlicedCollection(Consumer<T> action, boolean tailConsumer)
    {
        super();
        this.action = action;
        this.tailConsumer = tailConsumer;
    }

    public void processTasks(int amount)
    {
        if (amount == 1)
        {
            action.accept(get(currentIndex));
            currentIndex++;
            return;
        }
        for (int x = currentIndex; x < amount; x++)
        {
            action.accept(get(currentIndex));
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
}
