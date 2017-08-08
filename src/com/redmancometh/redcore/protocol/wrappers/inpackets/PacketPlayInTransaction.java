package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayInTransaction
        extends WrappedPacket {
    public boolean accepted;
    public short actionId;
    public int windowId;

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] data = PacketInType.Transaction.getPacketData(packet);
        windowId = (Integer) data[0];
        actionId = (Short) data[1];
        accepted = (Boolean) data[2];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketInType.Transaction.newPacket(windowId, actionId, accepted);
    }
}

