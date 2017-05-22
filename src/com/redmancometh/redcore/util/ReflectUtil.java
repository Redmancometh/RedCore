package com.redmancometh.redcore.util;

import java.lang.reflect.Field;

public class ReflectUtil
{
    public static Object getPrivateField(String fieldName, @SuppressWarnings("rawtypes") Class classs, Object object)
    {
	Field field;
	Object o = null;
	try
	{
	    field = classs.getDeclaredField(fieldName);

	    field.setAccessible(true);

	    o = field.get(object);
	}
	catch (NoSuchFieldException e)
	{
	    e.printStackTrace();
	}
	catch (IllegalAccessException e)
	{
	    e.printStackTrace();
	}
	return o;
    }
}
