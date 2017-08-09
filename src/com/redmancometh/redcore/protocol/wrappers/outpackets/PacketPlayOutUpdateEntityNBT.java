package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.nbt.NBTCompound;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutUpdateEntityNBT extends WrappedPacket {
    public int entityId;
    public NBTCompound nbt;

    public PacketPlayOutUpdateEntityNBT()
    {
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        Object[] data = PacketOutType.UpdateEntityNBT.getPacketData(obj);
        entityId = (int) data[0];
        nbt = new NBTCompound(data[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.UpdateEntityNBT.newPacket(entityId, nbt.toNMS());
    }
}
