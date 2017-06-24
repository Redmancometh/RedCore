package com.redmancometh.redcore;

import org.hibernate.SessionFactory;

import com.redmancometh.redcore.mediators.NulledObjectManager;
import com.redmancometh.redcore.mediators.ObjectManager;

public interface MenuPlugin extends RedPlugin
{
    NulledObjectManager nullMan = new NulledObjectManager();

    @Override
    public default ObjectManager getManager()
    {
        return nullMan;
    }

    @Override
    default SessionFactory getInternalFactory()
    {
        return null;
    }

    @Override
    default void setInternalFactory(SessionFactory factory)
    {

    }
}
