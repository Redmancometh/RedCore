package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.EntityUtils;
import org.bukkit.World;

import java.lang.reflect.Array;

/**
 * Created by GyuriX, on 2017. 03. 29..
 */
public class PacketPlayOutMapChunkBulk extends WrappedPacket {
    public PacketPlayOutMapChunk.ChunkMap[] chunkMaps;
    public int[] chunkX;
    public int[] chunkZ;
    public boolean hasSkyLight;
    public World world;

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.MapChunkBulk.newPacket(chunkX, chunkZ, getNMSChunkMapArray(), hasSkyLight, world == null ? null : EntityUtils.getNMSWorld(world));
    }

    public Object[] getNMSChunkMapArray() {
        Object[] out = (Object[]) Array.newInstance(PacketPlayOutMapChunk.ChunkMap.nmsChunkMap, chunkMaps.length);
        for (int i = 0; i < chunkMaps.length; ++i)
            out[i] = chunkMaps[i].toNMS();
        return out;
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.MapChunkBulk.getPacketData(packet);
        chunkX = (int[]) d[0];
        chunkZ = (int[]) d[1];
        chunkMaps = loadNMSChunkMapArray((Object[]) d[2]);
        hasSkyLight = (boolean) d[3];
        world = d[4] == null ? null : EntityUtils.getBukkitWorld(d[4]);
    }

    public PacketPlayOutMapChunk.ChunkMap[] loadNMSChunkMapArray(Object[] ar) {
        PacketPlayOutMapChunk.ChunkMap[] out = new PacketPlayOutMapChunk.ChunkMap[ar.length];
        for (int i = 0; i < ar.length; ++i)
            out[i] = new PacketPlayOutMapChunk.ChunkMap(ar[i]);
        return out;
    }
}
