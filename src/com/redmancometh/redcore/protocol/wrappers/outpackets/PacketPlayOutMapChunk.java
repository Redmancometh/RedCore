package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.Field;

/**
 * Created by GyuriX, on 2017. 03. 29..
 */
public class PacketPlayOutMapChunk extends WrappedPacket {
    public ChunkMap chunkMap;
    public int chunkX;
    public int chunkZ;
    public boolean groundUpContinuous;

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.MapChunk.newPacket(chunkX, chunkZ, chunkMap.toNMS(), groundUpContinuous);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.MapChunk.getPacketData(packet);
        chunkX = (int) d[0];
        chunkZ = (int) d[1];
        chunkMap = new ChunkMap(d[2]);
        groundUpContinuous = (boolean) d[3];
    }

    public static class ChunkMap implements WrappedData {
        public static final Class nmsChunkMap = Reflection.getNMSClass("PacketPlayOutMapChunk$ChunkMap");
        private static final Field byteArrayF = Reflection.getFirstFieldOfType(nmsChunkMap, byte[].class);
        private static final Field intF = Reflection.getFirstFieldOfType(nmsChunkMap, int.class);
        public byte[] data;
        public int primaryBitMask;

        public ChunkMap() {

        }

        public ChunkMap(Object o) {
            try {
                primaryBitMask = intF.getInt(o);
                data = (byte[]) byteArrayF.get(o);
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        @Override
        public Object toNMS() {
            try {
                Object out = nmsChunkMap.newInstance();
                byteArrayF.set(out, data);
                intF.set(out, primaryBitMask);
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }
    }
}
