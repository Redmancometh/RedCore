package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutUpdateAttributes extends WrappedPacket {
    public ArrayList<Attribute> attributes;
    public int entityId;

    public PacketPlayOutUpdateAttributes()
    {
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        Object[] d = PacketOutType.UpdateAttributes.getPacketData(obj);
        entityId = (int) d[0];
        loadNMSAttributes((Iterable<Object>) d[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.UpdateAttributes.newPacket(entityId, getNMSAttributes());
    }

    public ArrayList<Object> getNMSAttributes()
    {
        ArrayList<Object> out = new ArrayList<>();
        for (Attribute a : attributes)
            out.add(a.toNMS());
        return out;
    }

    public void loadNMSAttributes(Iterable<Object> nms)
    {
        attributes = new ArrayList<>();
        for (Object o : nms)
            attributes.add(new Attribute(o));
    }

    public static class Attribute implements WrappedData {
        private static final Class nmsClass = Reflection.getNMSClass("PacketPlayOutUpdateAttributes$AttributeSnapshot");
        private static final Field nameField = Reflection.getFirstFieldOfType(nmsClass, String.class);
        private static final Field modifierField = Reflection.getFirstFieldOfType(nmsClass, Collection.class);
        private static final Field valueField = Reflection.getFirstFieldOfType(nmsClass, double.class);
        public ArrayList<AttributeModifier> modifiers = new ArrayList<>();
        public String name;
        public double value;

        public Attribute(String name, double value, ArrayList<AttributeModifier> modifiers)
        {
            this.name = name;
            this.value = value;
            this.modifiers.addAll(modifiers);
        }

        public Attribute(Object nms)
        {
            try {
                name = (String) nameField.get(nms);
                value = (double) valueField.get(nms);
                Collection<Object> nmsModifiers = (Collection<Object>) modifierField.get(nms);
                for (Object o : nmsModifiers)
                    modifiers.add(new AttributeModifier(o));
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        @Override
        public Object toNMS()
        {
            try {
                Object nms = Reflection.newInstance(nmsClass);
                nameField.set(nms, name);
                valueField.set(nms, value);
                ArrayList<Object> nmsModifiers = new ArrayList<>();
                for (AttributeModifier m : modifiers)
                    nmsModifiers.add(m.toNMS());
                modifierField.set(nms, nmsModifiers);
                return nms;
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
                return null;
            }
        }
    }

    public static class AttributeModifier implements WrappedData {
        private static final Class nmsClass = Reflection.getNMSClass("AttributeModifier");
        private static final Field nameField = Reflection.getFirstFieldOfType(nmsClass, String.class);
        private static final Field amountField = Reflection.getFirstFieldOfType(nmsClass, double.class);
        private static final Field idField = Reflection.getFirstFieldOfType(nmsClass, UUID.class);
        private static final Field operationField = Reflection.getFirstFieldOfType(nmsClass, int.class);
        public double amount;
        public UUID id;
        public String name;
        public int operation;

        public AttributeModifier(Object nms)
        {
            try {
                amount = amountField.getDouble(nms);
                id = (UUID) idField.get(nms);
                name = (String) nameField.get(nms);
                operation = operationField.getInt(nms);
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        @Override
        public Object toNMS()
        {
            try {
                Object nms = Reflection.newInstance(nmsClass);
                amountField.set(nms, amount);
                idField.set(nms, id);
                nameField.set(nms, name);
                operationField.set(nms, operation);
                return nms;
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
                return null;
            }
        }
    }
}
