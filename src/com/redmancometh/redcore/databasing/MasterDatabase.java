package com.redmancometh.redcore.databasing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.SessionFactory;

import lombok.Getter;

public class MasterDatabase implements Iterable<SubDatabase>
{
    @Getter
    private Map<Class, SubDatabase> subDBMap = new HashMap();

    public SubDatabase getSubDBForType(Class clazz)
    {
        return subDBMap.get(clazz);
    }

    public void registerDatabase(Class ofType, SessionFactory factory)
    {
        System.out.println("REGISTERED FOR: " + ofType);
        System.out.println("WITH FACTORY: " + factory);
        this.subDBMap.put(ofType, new SubDatabase(ofType, factory));
    }

    @Override
    public void forEach(Consumer<? super SubDatabase> action)
    {
        subDBMap.values().forEach(action);
    }

    @Override
    public Iterator<SubDatabase> iterator()
    {
        return subDBMap.values().iterator();
    }

}
