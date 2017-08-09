package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.02.28..
 */
public class PacketPlayOutUpdateTime extends WrappedPacket {
    public long timeOfDay;
    public long worldAge;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.UpdateTime.getPacketData(packet);
        worldAge = (long) d[0];
        timeOfDay = (long) d[1];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.UpdateTime.newPacket(worldAge, timeOfDay);
    }
}
