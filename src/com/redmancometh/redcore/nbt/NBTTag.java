package com.redmancometh.redcore.nbt;

import com.redmancometh.redcore.config.StringSerializable;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import io.netty.buffer.ByteBuf;

import java.util.*;

public abstract class NBTTag implements WrappedData, StringSerializable {

    public static NBTTag make(Object o) {
        if (o instanceof NBTTag) {
            return (NBTTag) o;
        }
        if (o instanceof Collection) {
            return new NBTList().addAll((Collection) o);
        }
        if (o.getClass().isArray()) {
            return new NBTList().addAll((Object[]) o);
        }
        if (o instanceof Map) {
            return new NBTCompound().addAll((Map) o);
        }
        return new NBTPrimitive().setData(o);
    }

    public abstract void loadFromNMS(Object var1);

    public abstract void write(ByteBuf buf);
}

