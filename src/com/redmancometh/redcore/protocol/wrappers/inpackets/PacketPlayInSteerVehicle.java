package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayInSteerVehicle
        extends WrappedPacket {
    public float forward;
    public boolean jump;
    public float sideways;
    public boolean unmount;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] data = PacketInType.SteerVehicle.getPacketData(packet);
        sideways = ((Float) data[0]).floatValue();
        forward = ((Float) data[1]).floatValue();
        jump = (Boolean) data[2];
        unmount = (Boolean) data[3];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.SteerVehicle.newPacket(Float.valueOf(sideways), Float.valueOf(forward), jump, unmount);
    }
}

