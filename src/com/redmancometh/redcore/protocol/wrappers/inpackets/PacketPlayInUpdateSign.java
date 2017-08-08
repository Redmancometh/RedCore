package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

import java.lang.reflect.Array;

public class PacketPlayInUpdateSign extends WrappedPacket {
    public BlockLocation block;
    public ChatTag[] lines;

    @Override
    public Object getVanillaPacket() {
        Object[] lines = (Object[]) Array.newInstance(ChatAPI.icbcClass, 4);
        if (Reflection.ver.isAbove(ServerVersion.v1_9))
            for (int i = 0; i < 4; ++i)
                lines[i] = this.lines[i].toColoredString();
        else
            for (int i = 0; i < 4; ++i)
                lines[i] = this.lines[i].toICBC();
        return PacketInType.UpdateSign.newPacket(block.toNMS(), lines);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] data = PacketInType.UpdateSign.getPacketData(packet);
        block = new BlockLocation(data[0]);
        lines = new ChatTag[4];
        Object[] packetLines = (Object[]) data[1];
        if (Reflection.ver.isAbove(ServerVersion.v1_9))
            for (int i = 0; i < 4; ++i)
                lines[i] = ChatTag.fromColoredText((String) packetLines[i]);
        else
            for (int i = 0; i < 4; ++i)
                lines[i] = ChatTag.fromICBC(packetLines[i]);
    }
}

