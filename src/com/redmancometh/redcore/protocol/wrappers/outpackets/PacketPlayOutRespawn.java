package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.WorldType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.EntityUtils;
import org.bukkit.*;

import static com.redmancometh.redcore.protocol.utils.WorldType.*;

/**
 * Created by GyuriX on 2016.08.22..
 */
public class PacketPlayOutRespawn extends WrappedPacket implements Cloneable {
    public Difficulty difficulty;
    public int dimension;
    public GameMode gameMode;
    public WorldType worldType;

    public PacketPlayOutRespawn(int dimension, Difficulty difficulty, GameMode gameMode, WorldType worldType)
    {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.worldType = worldType;
    }

    public PacketPlayOutRespawn(World w)
    {
        dimension = w.getEnvironment().getId();
        difficulty = w.getDifficulty();
        Object wd = EntityUtils.getWorldData(w);
        gameMode = GameMode.ADVENTURE;
        worldType = EntityUtils.getWorldType(wd);
    }

    public PacketPlayOutRespawn(int dimension, World w)
    {
        this.dimension = dimension;
        difficulty = w.getDifficulty();
        Object wd = EntityUtils.getWorldData(w);
        gameMode = GameMode.ADVENTURE;
        worldType = EntityUtils.getWorldType(wd);
    }

    public PacketPlayOutRespawn()
    {
    }

    public PacketPlayOutRespawn(Object packet)
    {
        loadVanillaPacket(packet);
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.Respawn.getPacketData(packet);
        dimension = (int) d[0];
        difficulty = Difficulty.valueOf(d[1].toString());
        gameMode = GameMode.valueOf(d[2].toString());
        worldType = fromVanillaWorldType(d[3]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.Respawn.newPacket(dimension, toVanillaDifficulty(difficulty), toVanillaGameMode(gameMode), worldType.toNMS());
    }

    @Override
    public PacketPlayOutRespawn clone()
    {
        return new PacketPlayOutRespawn(dimension, difficulty, gameMode, worldType);
    }
}
