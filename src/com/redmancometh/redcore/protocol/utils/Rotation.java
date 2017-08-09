package com.redmancometh.redcore.protocol.utils;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.*;

public class Rotation implements WrappedData {
    private static final Class cl = Reflection.getNMSClass("Vector3f");
    private static final Constructor con = Reflection.getConstructor(cl, float.class, float.class, float.class);
    private static final Field xf;
    private static final Field yf;
    private static final Field zf;

    static {
        xf = Reflection.getField(cl, "x");
        yf = Reflection.getField(cl, "y");
        zf = Reflection.getField(cl, "z");
    }

    public float x;
    public float y;
    public float z;

    public Rotation()
    {

    }

    public Rotation(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Rotation(Object vanillaVector)
    {
        try {
            x = (Float) xf.get(vanillaVector);
            y = (Float) yf.get(vanillaVector);
            z = (Float) zf.get(vanillaVector);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object toNMS()
    {
        try {
            return con.newInstance(x, y, z);
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            return null;
        }
    }
}

