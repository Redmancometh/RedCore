package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutEntityHeadRotation extends WrappedPacket {
    public int entityId;
    public byte rotation;

    public PacketPlayOutEntityHeadRotation()
    {

    }

    public PacketPlayOutEntityHeadRotation(int entityId, float rotation)
    {
        this.entityId = entityId;
        this.rotation = (byte) (rotation * 360.0 / 256.0);
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.EntityHeadRotation.getPacketData(packet);
        entityId = (int) d[0];
        rotation = (byte) d[1];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.EntityHeadRotation.newPacket(entityId, rotation);
    }
}
