package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.02.28..
 */
public class PacketPlayOutOpenSignEditor extends WrappedPacket {
    public BlockLocation loc;

    public PacketPlayOutOpenSignEditor()
    {

    }

    public PacketPlayOutOpenSignEditor(BlockLocation loc)
    {
        this.loc = loc;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.OpenSignEditor.getPacketData(packet);
        loc = new BlockLocation(d[0]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.OpenSignEditor.newPacket(loc.toNMS());
    }
}
