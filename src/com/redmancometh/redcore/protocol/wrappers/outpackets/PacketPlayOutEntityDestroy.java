package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutEntityDestroy extends WrappedPacket {
    public int[] entityIds;

    public PacketPlayOutEntityDestroy()
    {

    }

    public PacketPlayOutEntityDestroy(int... eids)
    {
        entityIds = eids;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        entityIds = (int[]) PacketOutType.EntityDestroy.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.EntityDestroy.newPacket(entityIds);
    }
}
