package com.redmancometh.redcore.protocol.utils;


import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.config.StringSerializable;
import com.redmancometh.redcore.json.JsonAPI;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.spigotutils.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

public class DataWatcher implements WrappedData, StringSerializable {
    private static Constructor con;
    private static Field dwField, itemField, idField;
    private static Constructor itc;
    private static Class nmsDW, nmsItem;
    private static Constructor objcon;
    private static Map<Class, Object> serializers;

    static {
        try {
            nmsDW = Reflection.getNMSClass("DataWatcher");
            con = Reflection.getConstructor(nmsDW, Reflection.getNMSClass("Entity"));
            dwField = Reflection.getLastFieldOfType(nmsDW, Map.class);
            if (Reflection.ver.isAbove(ServerVersion.v1_9)) {
                Class dwr = Reflection.getNMSClass("DataWatcherRegistry");
                nmsItem = Reflection.getInnerClass(nmsDW, "Item");
                itc = nmsItem.getConstructors()[0];
                itemField = Reflection.getFirstFieldOfType(nmsItem, Object.class);
                objcon = Reflection.getConstructor(Reflection.getNMSClass("DataWatcherObject"), int.class, Reflection.getNMSClass("DataWatcherSerializer"));
                serializers = new HashMap<>();
                serializers.put(Byte.class, dwr.getField("a").get(null));
                serializers.put(Integer.class, dwr.getField("b").get(null));
                serializers.put(Float.class, dwr.getField("c").get(null));
                serializers.put(String.class, dwr.getField("d").get(null));
                serializers.put(ChatAPI.icbcClass, dwr.getField("e").get(null));
                serializers.put(Reflection.getNMSClass("ItemStack"), dwr.getField("f").get(null));
                serializers.put(Reflection.getNMSClass("IBlockData"), dwr.getField("g").get(null));
                serializers.put(Boolean.class, dwr.getField("h").get(null));
                serializers.put(Reflection.getNMSClass("Vector3f"), dwr.getField("i").get(null));
                serializers.put(Reflection.getNMSClass("BlockPosition"), dwr.getField("k").get(null));
                serializers.put(Reflection.getNMSClass("EnumDirection"), dwr.getField("l").get(null));
                serializers.put(UUID.class, dwr.getField("m").get(null));
            } else {
                nmsItem = Reflection.getInnerClass(nmsDW, "WatchableObject");
                itc = nmsItem.getConstructors()[0];
                itemField = Reflection.getFirstFieldOfType(nmsItem, Object.class);
                idField = Reflection.getField(nmsItem, "b");
                serializers = (Map<Class, Object>) Reflection.getFirstFieldOfType(nmsDW, Map.class).get(null);
            }
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    public TreeMap<Integer, Object> map = new TreeMap<>();

    public DataWatcher()
    {
    }

    public DataWatcher(Iterable<WrappedItem> list)
    {
        try {
            for (WrappedItem wi : list)
                map.put(wi.id, wi.data);
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    public DataWatcher(Object nmsData)
    {
        try {
            if (nmsData == null)
                return;
            if (nmsData instanceof Iterable) {
                for (WrappedItem wi : wrapNMSItems((Iterable<Object>) nmsData))
                    map.put(wi.id, wi.data);
                return;
            }
            Map<Integer, Object> m = (Map<Integer, Object>) dwField.get(nmsData);
            for (Entry<Integer, Object> e : m.entrySet())
                map.put(e.getKey(), itemField.get(e.getValue()));
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    public static ArrayList<WrappedItem> wrapNMSItems(Iterable<Object> in)
    {
        ArrayList<WrappedItem> out = new ArrayList<>();
        for (Object o : in) {
            try {
                out.add(new WrappedItem(o));
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "gyuriX");
            }
        }
        return out;
    }

    public static ArrayList<Object> convertToNmsItems(Iterable<WrappedItem> in)
    {
        ArrayList<Object> out = new ArrayList<>();
        for (WrappedItem wi : in) {
            try {
                Object o = WrapperFactory.unwrap(wi.data);
                if (o != null) {
                    if (Reflection.ver.isAbove(ServerVersion.v1_9))
                        out.add(itc.newInstance(objcon.newInstance(wi.id, serializers.get(o.getClass())), o));
                    else {
                        out.add(itc.newInstance(serializers.get(o.getClass()), wi.id, o));
                    }
                }
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "gyuriX");
            }
        }
        return out;
    }

    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        for (Entry<Integer, Object> e : map.entrySet()) {
            out.append("§e, §b").append(e.getKey()).append("§e: §f").append(JsonAPI.serialize(e.getValue()));
        }
        return out.length() == 0 ? "§e{}" : "§e{§b" + out.substring(6) + "§e}";
    }

    public static class WrappedItem implements WrappedData {
        public Object data;
        public int id;

        public WrappedItem(int id, Object data)
        {
            this.id = id;
            this.data = data;
        }

        public WrappedItem(Object o)
        {
            try {
                data = WrapperFactory.wrap(itemField.get(o));
                id = idField.getInt(o);
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        @Override
        public Object toNMS()
        {
            try {
                if (Reflection.ver.isAbove(ServerVersion.v1_9))
                    return itc.newInstance(objcon.newInstance(id, serializers.get(data.getClass())), data);
                else
                    return itc.newInstance(serializers.get(data.getClass()), id, data);
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }
    }

    @Override
    public Object toNMS()
    {
        Object dw = null;
        try {
            dw = con.newInstance((Object) null);
            Map<Integer, Object> m = (Map<Integer, Object>) dwField.get(dw);
            for (Entry<Integer, Object> e : map.entrySet()) {
                Object o = WrapperFactory.unwrap(e.getValue());
                if (o == null)
                    continue;
                try {
                    if (Reflection.ver.isAbove(ServerVersion.v1_9)) {
                        m.put(e.getKey(), itc.newInstance(objcon.newInstance(e.getKey(), serializers.get(o.getClass())), o));
                    } else {
                        int type = (int) serializers.get(o.getClass());
                        m.put(e.getKey(), itc.newInstance(type, e.getKey(), o));
                    }
                } catch (Throwable err) {
                    SU.cs.sendMessage("§e[DataWatcher] §cError on getting serializer for object #" + e.getKey() + " - §f" + o + "§c having class §f" + o.getClass().getSimpleName());
                    SU.error(SU.cs, err, "RedCore", "com.redmancometh");
                }
            }
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return dw;
    }


}
