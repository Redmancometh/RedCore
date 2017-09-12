package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.GameProfile;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import javafx.util.Pair;

/**
 * Created by GyuriX on 2016.03.03..
 */
public class PacketLoginInStart extends WrappedPacket
{
    public GameProfile gp;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Pair p;
        gp = new GameProfile(PacketInType.LoginInStart.getPacketData(packet)[0]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.LoginInStart.newPacket(gp.toNMS());
    }
}
