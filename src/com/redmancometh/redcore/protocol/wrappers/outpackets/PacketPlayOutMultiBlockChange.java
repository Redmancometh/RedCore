package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.*;

import java.lang.reflect.*;

/**
 * Created by GyuriX, on 2017. 02. 05..
 */
public class PacketPlayOutMultiBlockChange extends WrappedPacket {
    public MultiBlockChangeInfo[] infos;
    public XZ xz;

    public PacketPlayOutMultiBlockChange()
    {
    }

    public PacketPlayOutMultiBlockChange(XZ xz, MultiBlockChangeInfo... infos)
    {
        this.xz = xz;
        this.infos = infos;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.MultiBlockChange.getPacketData(packet);
        xz = new XZ(d[0]);
        infos = loadNMSInfos((Object[]) d[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.MultiBlockChange.newPacket(xz.toNMS(), getNMSInfos());
    }

    public Object[] getNMSInfos()
    {
        Object[] d = new Object[infos.length];
        for (int i = 0; i < d.length; ++i)
            d[i] = infos[i].toNMS();
        return d;
    }

    public MultiBlockChangeInfo[] loadNMSInfos(Object[] infos)
    {
        MultiBlockChangeInfo[] d = new MultiBlockChangeInfo[infos.length];
        for (int i = 0; i < d.length; ++i)
            d[i] = d[i] = new MultiBlockChangeInfo(infos[i]);
        return d;
    }

    public static class MultiBlockChangeInfo implements WrappedData {
        private static Class nmsClass = Reflection.getNMSClass("PacketPlayOutMultiBlockChange$MultiBlockChangeInfo");
        private static Constructor nmsConst = Reflection.getConstructor(nmsClass, short.class, Reflection.getNMSClass("IBlockData"));
        private static Field posF = Reflection.getFirstFieldOfType(nmsClass, short.class), blockDataF = Reflection.getFirstFieldOfType(nmsClass, Reflection.getNMSClass("IBlockData"));
        public byte blockData;
        public int blockId;
        public short pos;

        public MultiBlockChangeInfo()
        {

        }

        public MultiBlockChangeInfo(Object nms)
        {
            try {
                pos = posF.getShort(nms);
                Object nmsBl = blockDataF.get(nms);
                blockId = BlockUtils.getNMSBlockId(nmsBl);
                blockData = BlockUtils.getNMSBlockData(nmsBl);
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public byte getRelX()
        {
            return (byte) (pos >> 12 & 15);
        }

        public byte getRelZ()
        {
            return (byte) (pos >> 8 & 15);
        }

        public int getY()
        {
            return pos & 255;
        }

        @Override
        public Object toNMS()
        {
            try {
                return nmsConst.newInstance(pos, BlockUtils.getNMSBlock(blockId, blockData));
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }
    }
}
