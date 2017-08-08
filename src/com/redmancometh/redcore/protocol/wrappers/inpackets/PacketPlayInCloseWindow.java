package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayInCloseWindow
        extends WrappedPacket {
    public int id;

    @Override
    public Object getVanillaPacket() {
        return PacketInType.CloseWindow.newPacket(id);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        id = (Integer) PacketInType.CloseWindow.getPacketData(packet)[0];
    }
}

