package com.redmancometh.redcore.util;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import com.redmancometh.redcore.RedCore;

public class MetaUtil
{
    public static void removeMetaAfter(Player p, String metaName, int seconds)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RedCore.getInstance(), () -> p.removeMetadata(metaName, RedCore.getInstance()), seconds * 20L);
    }

    public static void addMetaThenRemoveAfter(Player p, String metaName, int seconds)
    {
        p.setMetadata(metaName, new FixedMetadataValue(RedCore.getInstance(), true));
        Bukkit.getScheduler().scheduleSyncDelayedTask(RedCore.getInstance(), () -> p.removeMetadata(metaName, RedCore.getInstance()), seconds * 20L);
    }

    /**
     * 
     * @param p
     * @param metaName
     * @param seconds
     * @param callback
     */
    public static void addRemoveThen(Player p, String metaName, long seconds, Consumer<Player> callback)
    {
        p.setMetadata(metaName, new FixedMetadataValue(RedCore.getInstance(), true));
        Bukkit.getScheduler().scheduleSyncDelayedTask(RedCore.getInstance(), () ->
        {
            callback.accept(p);
            p.removeMetadata(metaName, RedCore.getInstance());
        }, seconds * 20L);
    }
}
