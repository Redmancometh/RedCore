package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.02.28..
 */
public class PacketPlayOutSpawnPosition extends WrappedPacket {
    public BlockLocation location;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        location = new BlockLocation(PacketOutType.SpawnPosition.getPacketData(packet)[0]);

    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.SpawnPosition.newPacket(location.toNMS());
    }

}
