package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutEntity extends WrappedPacket {
    public int entityId;

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.Entity.newPacket(entityId);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        entityId = (int) PacketOutType.Entity.getPacketData(packet)[0];
    }
}
