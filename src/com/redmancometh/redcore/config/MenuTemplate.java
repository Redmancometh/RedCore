
package com.redmancometh.redcore.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MenuTemplate implements Iterable<Pane>
{
    private List<Pane> panes = new ArrayList();

    public List<Pane> getPanes()
    {
        return panes;
    }

    public void setPanes(List<Pane> panes)
    {
        this.panes = panes;
    }

    @Override
    public void forEach(Consumer<? super Pane> action)
    {
        panes.forEach(action);
    }

    @Override
    public Iterator<Pane> iterator()
    {
        return panes.iterator();
    }

}
