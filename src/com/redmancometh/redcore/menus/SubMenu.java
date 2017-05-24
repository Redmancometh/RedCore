package com.redmancometh.redcore.menus;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.redmancometh.redcore.RedCore;

public interface SubMenu
{
    public abstract void close(Player p);

    public default void closeMenu(Player p)
    {
        p.setMetadata("lowermenu", new FixedMetadataValue(RedCore.getInstance(), p));
        close(p);
    }
}
