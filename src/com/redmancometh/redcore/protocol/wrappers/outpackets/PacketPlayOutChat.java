package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.json.JsonAPI;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutChat extends WrappedPacket {
    private static Object[] nmsValues;

    static {
        try {
            nmsValues = (Object[]) Reflection.getMethod(Reflection.getNMSClass("ChatMessageType"), "values").invoke(null);
        } catch (Throwable e) {
        }
    }

    public ChatTag tag;
    /**
     * The type of this chat message 0: chat (chat box) 1: system message (chat box) 2: action bar
     */
    public byte type;

    public PacketPlayOutChat() {
    }

    public PacketPlayOutChat(byte type, ChatTag tag) {
        this.type = type;
        this.tag = tag;
    }

    @Override
    public void loadVanillaPacket(Object obj) {
        Object[] data = PacketOutType.Chat.getPacketData(obj);
        if (data[1] != null) {
            tag = ChatTag.fromBaseComponents((BaseComponent[]) data[1]);
        } else
            tag = JsonAPI.deserialize(ChatAPI.toJson(data[0]), ChatTag.class);
        type = (byte) (Reflection.ver.isAbove(ServerVersion.v1_12) ? (byte) (int) ((Enum) data[2]).ordinal() : data[2]);
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.Chat.newPacket(ChatAPI.toICBC(tag.toString()), null, Reflection.ver.isAbove(ServerVersion.v1_12) ? nmsValues[type] : type);
    }
}
