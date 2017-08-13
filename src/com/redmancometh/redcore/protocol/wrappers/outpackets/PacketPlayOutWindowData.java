package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayOutWindowData extends WrappedPacket
{
    public int property;
    public int value;
    public int windowId;

    public PacketPlayOutWindowData()
    {
    }

    public PacketPlayOutWindowData(int windowId, int property, int value)
    {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] o = PacketOutType.WindowData.getPacketData(packet);
        windowId = (Integer) o[0];
        property = (Integer) o[1];
        value = (Integer) o[2];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.WindowData.newPacket(windowId, property, value);
    }
}

