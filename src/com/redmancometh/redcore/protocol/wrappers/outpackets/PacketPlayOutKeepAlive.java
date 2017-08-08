package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.02.28..
 */
public class PacketPlayOutKeepAlive extends WrappedPacket {
    public int id;

    public PacketPlayOutKeepAlive() {

    }

    public PacketPlayOutKeepAlive(int id) {
        this.id = id;
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        id = (int) PacketOutType.KeepAlive.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.KeepAlive.newPacket(id);
    }
}
