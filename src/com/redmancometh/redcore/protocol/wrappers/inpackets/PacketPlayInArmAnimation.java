package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.HandType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

public class PacketPlayInArmAnimation extends WrappedPacket {
    public HandType hand;
    public long timestamp;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object d = PacketInType.ArmAnimation.getPacketData(packet)[0];
        if (Reflection.ver.isAbove(ServerVersion.v1_9))
            hand = HandType.valueOf(d.toString());
        else
            timestamp = (long) d;
    }

    @Override
    public Object getVanillaPacket()
    {
        return Reflection.ver.isAbove(ServerVersion.v1_9) ? PacketInType.ArmAnimation.newPacket(hand.toNMS()) : PacketInType.ArmAnimation.newPacket(timestamp);
    }
}

