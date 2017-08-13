package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.DataWatcher;
import com.redmancometh.redcore.protocol.utils.DataWatcher.WrappedItem;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutEntityMetadata extends WrappedPacket
{
    public int entityId;
    public ArrayList<WrappedItem> meta = new ArrayList<>();

    public PacketPlayOutEntityMetadata()
    {

    }

    public PacketPlayOutEntityMetadata(int entityId, ArrayList<WrappedItem> meta)
    {
        this.entityId = entityId;
        this.meta = meta;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.EntityMetadata.getPacketData(packet);
        entityId = (int) d[0];
        meta = DataWatcher.wrapNMSItems((List) d[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.EntityMetadata.newPacket(entityId, DataWatcher.convertToNmsItems(meta));

    }
}
