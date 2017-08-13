package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutExperience extends WrappedPacket
{
    public float bar;
    public int level;
    public int totalExperience;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.Experience.getPacketData(packet);
        bar = (float) d[0];
        level = (int) d[1];
        totalExperience = (int) d[2];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.Experience.newPacket(bar, level, totalExperience);
    }
}
