package com.redmancometh.redcore.mediators;

import java.util.UUID;

import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redcore.databasing.SubDatabase;

public interface BaseObjectManager<T extends Defaultable<?>> {
	public ObjectManager<T> getThis();

	public SubDatabase<UUID, T> getSubDB();
	
	public Class<T> getType();
}
