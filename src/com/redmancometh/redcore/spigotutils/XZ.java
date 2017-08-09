package com.redmancometh.redcore.spigotutils;

import com.redmancometh.redcore.config.StringSerializable;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.lang.reflect.*;

/**
 * Created by GyuriX on 2016. 08. 13..
 */
public class XZ implements StringSerializable, Comparable<XZ>, WrappedData {
    private static transient Class nmsClass = Reflection.getNMSClass("ChunkCoordIntPair");
    private static transient Constructor con = Reflection.getConstructor(nmsClass, int.class, int.class);
    private static transient Field xField = Reflection.getField(nmsClass, "x"), zField = Reflection.getField(nmsClass, "z");
    public int x, z;

    public XZ(String in)
    {
        String[] d = in.split(" ", 2);
        x = Integer.valueOf(d[0]);
        z = Integer.valueOf(d[1]);
    }

    public XZ(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public XZ(Block bl)
    {
        x = bl.getX();
        z = bl.getZ();
    }

    public XZ(Object nms)
    {
        try {
            x = xField.getInt(nms);
            z = zField.getInt(nms);
        } catch (IllegalAccessException e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    public XZ(Chunk c)
    {
        x = c.getX();
        z = c.getZ();
    }

    @Override
    public int compareTo(XZ o)
    {
        return ((Integer) hashCode()).compareTo(o.hashCode());
    }

    @Override
    public int hashCode()
    {
        return x << 16 + z;
    }

    @Override
    public boolean equals(Object obj)
    {
        XZ xz = (XZ) obj;
        return x == xz.x && z == xz.z;
    }

    @Override
    public String toString()
    {
        return x + " " + z;
    }

    @Override
    public Object toNMS()
    {
        try {
            return con.newInstance(x, z);
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return null;
    }
}
