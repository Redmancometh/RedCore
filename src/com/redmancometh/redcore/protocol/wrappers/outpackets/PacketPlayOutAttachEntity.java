package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

/**
 * Created by GyuriX, on 2017. 02. 05..
 */
public class PacketPlayOutAttachEntity extends WrappedPacket
{
    public int entity1, entity2;

    public PacketPlayOutAttachEntity()
    {

    }

    public PacketPlayOutAttachEntity(int entity1, int entity2)
    {
        this.entity1 = entity1;
        this.entity2 = entity2;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.AttachEntity.getPacketData(packet);
        entity1 = (int) d[0];
        entity2 = (int) d[1];
    }

    @Override
    public Object getVanillaPacket()
    {
        return Reflection.ver.isAbove(ServerVersion.v1_9) ? PacketOutType.AttachEntity.newPacket(entity1, entity2) : PacketOutType.AttachEntity.newPacket(0, entity1, entity2);
    }
}
