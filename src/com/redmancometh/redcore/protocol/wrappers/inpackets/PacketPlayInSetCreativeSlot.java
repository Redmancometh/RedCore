package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.ItemStackWrapper;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayInSetCreativeSlot extends WrappedPacket {
    public ItemStackWrapper itemStack;
    public int slot;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] data = PacketInType.SetCreativeSlot.getPacketData(packet);
        slot = (Integer) data[0];
        itemStack = new ItemStackWrapper(data[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.SetCreativeSlot.newPacket(slot, itemStack == null ? null : itemStack.toNMS());
    }
}

