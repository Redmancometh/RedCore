package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.ItemStackWrapper;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.02.28..
 */
public class PacketPlayOutSetSlot extends WrappedPacket {
    public ItemStackWrapper item;
    public int slot;
    public int windowId;

    public PacketPlayOutSetSlot() {

    }

    public PacketPlayOutSetSlot(int windowId, int slot, ItemStackWrapper item) {
        this.windowId = windowId;
        this.slot = slot;
        this.item = item;
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.SetSlot.getPacketData(packet);
        windowId = (int) d[0];
        slot = (int) d[1];
        item = new ItemStackWrapper(d[2]);
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.SetSlot.newPacket(windowId, slot, item.toNMS());
    }
}
