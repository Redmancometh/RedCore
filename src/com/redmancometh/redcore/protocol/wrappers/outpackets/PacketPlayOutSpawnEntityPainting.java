package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.utils.Direction;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.util.UUID;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutSpawnEntityPainting extends WrappedPacket
{
    public int entityId;
    public UUID entityUUID;
    public Direction facing;
    public BlockLocation location;
    public String title;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.SpawnEntityPainting.getPacketData(packet);
        entityId = (int) d[0];
        entityUUID = (UUID) d[1];
        location = new BlockLocation(d[2]);
        facing = Direction.valueOf(d[3].toString());
        title = (String) d[4];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.SpawnEntityPainting.newPacket(entityId, entityUUID, location.toNMS(), facing.toNMS(), title);
    }
}
