package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.*;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketPlayOutEntityTeleport extends WrappedPacket {
    public int entityId;
    public boolean onGround;
    public byte pitch;
    public double x;
    public double y;
    public byte yaw;
    public double z;

    public PacketPlayOutEntityTeleport() {

    }

    public PacketPlayOutEntityTeleport(int entityId, LocationData loc) {
        this.entityId = entityId;
        setLocation(loc);
    }

    public void setLocation(LocationData loc) {
        x = loc.x;
        y = loc.y;
        z = loc.z;
        yaw = (byte) (loc.yaw / 360.0 * 256);
        pitch = (byte) (loc.pitch / 360.0 * 256);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.EntityTeleport.getPacketData(packet);
        entityId = (int) d[0];
        if (Reflection.ver.isAbove(ServerVersion.v1_9)) {
            x = (double) d[1];
            y = (double) d[2];
            z = (double) d[3];
        } else {
            x = (int) d[1] / 32.0;
            y = (int) d[2] / 32.0;
            z = (int) d[3] / 32.0;
        }
        yaw = (byte) d[4];
        pitch = (byte) d[5];
        if (Reflection.ver.isAbove(ServerVersion.v1_8))
            onGround = (boolean) d[6];
    }

    @Override
    public Object getVanillaPacket() {
        if (Reflection.ver.isAbove(ServerVersion.v1_9))
            return PacketOutType.EntityTeleport.newPacket(entityId, x, y, z, yaw, pitch, onGround);
        else if (Reflection.ver == ServerVersion.v1_8)
            return PacketOutType.EntityTeleport.newPacket(entityId, (int) (x / 32), (int) (y / 32), (int) (z / 32), yaw, pitch, onGround);
        else
            return PacketOutType.EntityTeleport.newPacket(entityId, (int) (x / 32), (int) (y / 32), (int) (z / 32), yaw, pitch);
    }
}
