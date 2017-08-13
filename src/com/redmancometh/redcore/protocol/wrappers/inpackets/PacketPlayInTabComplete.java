package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

public class PacketPlayInTabComplete extends WrappedPacket
{
    public boolean assumeCommand;
    public BlockLocation block;
    public String text;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] data = PacketInType.TabComplete.getPacketData(packet);
        text = (String) data[0];
        if (Reflection.ver.isAbove(ServerVersion.v1_10))
        {
            assumeCommand = (boolean) data[1];
            block = data[2] == null ? null : new BlockLocation(data[2]);
        } else block = data[1] == null ? null : new BlockLocation(data[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        if (Reflection.ver.isAbove(ServerVersion.v1_10))
            return PacketInType.TabComplete.newPacket(text, assumeCommand, block == null ? null : block.toNMS());
        return PacketInType.TabComplete.newPacket(text, block == null ? null : block.toNMS());
    }
}

