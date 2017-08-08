package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayInHeldItemSlot
        extends WrappedPacket {
    public int itemInHandIndex;

    @Override
    public void loadVanillaPacket(Object packet) {
        itemInHandIndex = (Integer) PacketInType.HeldItemSlot.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketInType.HeldItemSlot.newPacket(itemInHandIndex);
    }
}

