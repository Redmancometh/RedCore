package com.redmancometh.redcore.mediators;

import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redcore.databasing.SubDatabase;

import java.util.UUID;

public interface BaseObjectManager<T extends Defaultable<?>> {
    SubDatabase<UUID, T> getSubDB();

    ObjectManager<T> getThis();

    Class<T> getType();
}
