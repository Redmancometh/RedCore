package com.redmancometh.redcore.protocol.utils;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.*;

public class Vector implements WrappedData {
    private static final Class cl = Reflection.getNMSClass("Vec3D");
    private static final Constructor con = Reflection.getConstructor(cl, double.class, double.class, double.class);
    private static final Field xf;
    private static final Field yf;
    private static final Field zf;

    static {
        Field[] f = cl.getFields();
        int i = f[0].getType() == double.class ? 0 : 1;
        xf = f[i++];
        yf = f[i++];
        zf = f[i];
    }

    public double x;
    public double y;
    public double z;

    public Vector() {

    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Object vanillaVector) {
        try {
            x = (Double) xf.get(vanillaVector);
            y = (Double) yf.get(vanillaVector);
            z = (Double) zf.get(vanillaVector);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object toNMS() {
        try {
            return con.newInstance(x, y, z);
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            return null;
        }
    }
}

