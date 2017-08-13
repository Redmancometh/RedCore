package com.redmancometh.redcore.nbt;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTList extends NBTTag
{
    public static Class nmsClass;
    static Field listField;
    static Field listType;
    public ArrayList<NBTTag> list = new ArrayList();

    public NBTList()
    {
    }

    public NBTList(Object tag)
    {
        loadFromNMS(tag);
    }

    public NBTList addAll(Collection col)
    {
        for (Object o : col)
        {
            if (o == null) continue;
            list.add(NBTTag.make(o));
        }
        return this;
    }

    public NBTList addAll(Object... col)
    {
        for (Object o : col)
        {
            if (o == null) continue;
            list.add(NBTTag.make(o));
        }
        return this;
    }

    @Override
    public void loadFromNMS(Object tag)
    {
        try
        {
            for (Object o : (List) listField.get(tag))
            {
                String cln = o.getClass().getSimpleName();
                if (cln.equals("NBTTagCompound"))
                {
                    list.add(new NBTCompound(o));
                    continue;
                }
                if (cln.equals("NBTTagList"))
                {
                    list.add(new NBTList(o));
                    continue;
                }
                list.add(new NBTPrimitive(o));
            }
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void write(ByteBuf buf)
    {
        if (list.isEmpty())
        {
            buf.writeByte(0);
            buf.writeInt(0);
            return;
        }
        buf.writeByte(NBTApi.getType(list.get(0)));
        buf.writeInt(list.size());
        for (NBTTag nbtTag : list)
            nbtTag.write(buf);
    }

    @Override
    public Object toNMS()
    {
        try
        {
            Object o = nmsClass.newInstance();
            ArrayList<Object> l = new ArrayList<Object>();
            for (NBTTag t : list)
            {
                l.add(t.toNMS());
            }
            listField.set(o, l);
            if (!l.isEmpty())
            {
                listType.set(o, Byte.valueOf((byte) ArrayUtils.indexOf(NBTApi.types, l.get(0).getClass())));
            }
            return o;
        } catch (Throwable e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String toString()
    {
        return "[\u00a7b" + StringUtils.join(list, ", \u00a7b") + "\u00a7b]";
    }
}

