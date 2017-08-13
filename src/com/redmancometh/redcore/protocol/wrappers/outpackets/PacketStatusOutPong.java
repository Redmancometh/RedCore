package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketStatusOutPong extends WrappedPacket
{
    public long id;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        id = (long) PacketOutType.StatusOutPong.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.StatusOutPong.newPacket(id);
    }
}
