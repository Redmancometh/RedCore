package com.redmancometh.redcore;

import com.redmancometh.redcore.mediators.*;
import org.hibernate.SessionFactory;

public interface MenuPlugin extends RedPlugin {
    NulledObjectManager nullMan = new NulledObjectManager();

    @Override
    default ObjectManager getManager()
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
