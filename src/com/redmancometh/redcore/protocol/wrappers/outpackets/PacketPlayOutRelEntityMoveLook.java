package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutRelEntityMoveLook extends WrappedPacket {
    public byte deltaX, deltaY, deltaZ, yaw, pitch;
    public int entityId;
    public boolean onGround;

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.RelEntityMoveLook.newPacket(entityId, deltaX, deltaY, deltaZ, yaw, pitch, onGround);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.RelEntityMoveLook.getPacketData(packet);
        entityId = (int) d[0];
        deltaX = (byte) d[1];
        deltaY = (byte) d[2];
        deltaZ = (byte) d[3];
        yaw = (byte) d[4];
        pitch = (byte) d[5];
        onGround = (boolean) d[6];
    }
}
