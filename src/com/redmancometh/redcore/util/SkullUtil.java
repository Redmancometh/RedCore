package com.redmancometh.redcore.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullUtil
{

    public static String getOwnerFromType(EntityType type)
    {
        return "MHF_" + type.name();
    }

    public static ItemStack getSkullItemOfType(EntityType type)
    {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(getOwnerFromType(type));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getSkullOwnedBy(String player)
    {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        item.setItemMeta(meta);
        return item;
    }
}
