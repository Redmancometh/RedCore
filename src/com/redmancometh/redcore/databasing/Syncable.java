package com.redmancometh.redcore.databasing;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.redmancometh.redcore.Defaultable;

public interface Syncable<T> extends Defaultable<T>
{
    public default void trySync()
    {
        Player p = Bukkit.getPlayer(this.getUniqueId());
        if (p != null) sync(p);
    }

    public default void tryUpdateLocal(UUID uuid)
    {
        Player p = Bukkit.getPlayer(this.getUniqueId());
        if (p != null) updateFromLocal(p);
    }

    public abstract void sync(Player p);

    public abstract void updateFromLocal(Player p);
}
