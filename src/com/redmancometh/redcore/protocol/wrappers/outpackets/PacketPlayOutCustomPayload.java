package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;
import io.netty.buffer.*;

import java.lang.reflect.*;

import static com.redmancometh.redcore.protocol.Reflection.*;

/**
 * Created by com.redmancometh on 25/03/2017.
 */
public class PacketPlayOutCustomPayload extends WrappedPacket {
    private static final Constructor con = getConstructor(getNMSClass("PacketDataSerializer"), ByteBuf.class);
    private static final Field f = getField(getNMSClass("PacketDataSerializer"), "a");
    public String channel;
    public byte[] data;

    public PacketPlayOutCustomPayload()
    {

    }

    public PacketPlayOutCustomPayload(String channel, byte[] data)
    {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        Object[] data = PacketOutType.CustomPayload.getPacketData(obj);
        channel = (String) data[0];
        try {
            this.data = ((ByteBuf) f.get(data[1])).array();
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    @Override
    public Object getVanillaPacket()
    {
        try {
            return PacketOutType.CustomPayload.newPacket(channel, con.newInstance(ByteBufAllocator.DEFAULT.buffer().writeBytes(data)));
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return null;
    }
}
