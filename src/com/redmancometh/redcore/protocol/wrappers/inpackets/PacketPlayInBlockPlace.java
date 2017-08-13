package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.utils.Direction;
import com.redmancometh.redcore.protocol.utils.HandType;
import com.redmancometh.redcore.protocol.utils.ItemStackWrapper;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

public class PacketPlayInBlockPlace extends WrappedPacket
{
    public float cursorX;
    public float cursorY;
    public float cursorZ;
    public Direction face;
    public HandType hand;
    public ItemStackWrapper itemStack;
    public BlockLocation location;
    public long timestamp;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] data = PacketInType.BlockPlace.getPacketData(packet);
        if (Reflection.ver.isBellow(ServerVersion.v1_8))
        {
            location = new BlockLocation(data[0]);
            face = Direction.get((Integer) data[1]);
            itemStack = data[2] == null ? null : new ItemStackWrapper(data[2]);
            cursorX = (Float) data[3];
            cursorY = (Float) data[4];
            cursorZ = (Float) data[5];
            timestamp = (Long) data[6];
        } else
        {
            hand = HandType.valueOf(data[0].toString());
            timestamp = (Long) data[1];
        }
    }

    @Override
    public Object getVanillaPacket()
    {
        Object[] d;
        if (Reflection.ver.isBellow(ServerVersion.v1_8))
        {
            d = new Object[7];
            d[0] = location.toNMS();
            d[1] = face == null ? 255 : face.ordinal();
            d[2] = itemStack == null ? null : itemStack.toNMS();
            d[3] = cursorX;
            d[4] = cursorY;
            d[5] = cursorZ;
            d[6] = timestamp;
        } else
        {
            d = new Object[2];
            d[0] = hand.toNMS();
            d[1] = timestamp;
        }
        return PacketInType.BlockPlace.newPacket(d);
    }
}

