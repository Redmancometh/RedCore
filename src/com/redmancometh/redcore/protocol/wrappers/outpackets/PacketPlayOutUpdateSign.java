package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.nbt.*;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

import java.lang.reflect.Array;

public class PacketPlayOutUpdateSign extends WrappedPacket {
    public BlockLocation block;
    public ChatTag[] lines;

    public PacketPlayOutUpdateSign(BlockLocation loc, ChatTag[] lines) {
        block = loc;
        this.lines = lines;
    }

    public PacketPlayOutUpdateSign() {

    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] data = PacketOutType.UpdateSign.getPacketData(packet);
        block = new BlockLocation(data[1]);
        lines = new ChatTag[4];
        Object[] packetLines = (Object[]) data[2];
        for (int i = 0; i < 4; ++i) {
            lines[i] = ChatTag.fromICBC(packetLines[i]);
        }
    }

    @Override
    public Object getVanillaPacket() {
        if (Reflection.ver.isAbove(ServerVersion.v1_9)) {
            NBTCompound nbt = new NBTCompound();
            for (int i = 0; i < 4; ++i)
                nbt.map.put("Text" + (i + 1), new NBTPrimitive(lines[i].toString()));
            PacketPlayOutTileEntityData packet = new PacketPlayOutTileEntityData(block, 9, nbt);
            return packet.getVanillaPacket();
        }
        Object[] lines = (Object[]) Array.newInstance(ChatAPI.icbcClass, 4);
        for (int i = 0; i < 4; ++i)
            lines[i] = this.lines[i].toICBC();
        return PacketOutType.UpdateSign.newPacket(null, block.toNMS(), lines);
    }
}

