package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.util.UUID;

public class PacketPlayInSpectate
        extends WrappedPacket {
    private UUID entityUUID;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        entityUUID = (UUID) PacketInType.Spectate.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.Spectate.newPacket(entityUUID);
    }
}

