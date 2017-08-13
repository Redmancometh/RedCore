package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;


public class PacketPlayOutTabComplete extends WrappedPacket
{
    public String[] complete;

    public PacketPlayOutTabComplete()
    {
    }

    public PacketPlayOutTabComplete(Object nms)
    {
        loadVanillaPacket(nms);
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        complete = (String[]) PacketOutType.TabComplete.getPacketData(obj)[0];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.TabComplete.newPacket(new Object[]{complete});
    }

    public PacketPlayOutTabComplete(String[] complete)
    {
        this.complete = complete;
    }
}
