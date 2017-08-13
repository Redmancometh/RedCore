package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.ItemStackWrapper;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutWindowItems extends WrappedPacket
{
    private static final Class itemClass = Reflection.getNMSClass("ItemStack");
    public int inventoryId;
    public ArrayList<ItemStackWrapper> items = new ArrayList<>();

    public PacketPlayOutWindowItems()
    {
    }

    public PacketPlayOutWindowItems(int inventoryId, ArrayList<ItemStackWrapper> items)
    {
        this.inventoryId = inventoryId;
        this.items = items;
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        Object[] data = PacketOutType.WindowItems.getPacketData(obj);
        inventoryId = (int) data[0];
        items = wrapItemList((Object[]) data[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.WindowItems.newPacket(inventoryId, makeNMSItemList());
    }

    private Object[] makeNMSItemList()
    {
        Object[] d = (Object[]) Array.newInstance(itemClass, items.size());
        for (int i = 0; i < items.size(); i++)
            d[i] = items.get(i).toNMS();
        return d;
    }

    private ArrayList<ItemStackWrapper> wrapItemList(Object[] nms)
    {
        ArrayList<ItemStackWrapper> items = new ArrayList<>();
        for (Object o : nms)
            items.add(new ItemStackWrapper(o));
        return items;
    }
}
