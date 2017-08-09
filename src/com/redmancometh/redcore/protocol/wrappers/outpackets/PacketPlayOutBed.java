package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutBed extends WrappedPacket {
    public BlockLocation bed;
    public int entityId;

    public PacketPlayOutBed()
    {
    }

    public PacketPlayOutBed(int entityId, BlockLocation bed)
    {
        this.entityId = entityId;
        this.bed = bed;
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        Object[] data = PacketOutType.Bed.getPacketData(obj);
        entityId = (int) data[0];
        bed = new BlockLocation(data[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.Bed.newPacket(entityId, bed.toNMS());
    }
}
