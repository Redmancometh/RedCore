package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayInChat
        extends WrappedPacket {
    public String message;

    @Override
    public void loadVanillaPacket(Object packet) {
        message = (String) PacketInType.Chat.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketInType.Chat.newPacket(message);
    }
}

