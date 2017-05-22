package com.redmancometh.redcore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_11_R1.ItemArmor;

public class ItemUtil
{
    public static List<Material> swords = Arrays.asList(new Material[]
    { Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD });

    public static ItemStack buildItem(Material m, String itemName, String... lore)
    {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(Arrays.asList(lore));
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack constructItem(Material m, String displayName, int data, String... lore)
    {
        ItemStack i = new ItemStack(m, 1, (short) data);
        ItemMeta iMeta = i.getItemMeta();
        iMeta.setDisplayName(displayName);
        iMeta.setLore(Arrays.asList(lore));
        i.setItemMeta(iMeta);
        return i;
    }

    public static ItemStack buildItem(Material m, String itemName, List<String> lore)
    {
        ItemStack i = new ItemStack(m, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack buildItem(Material m, String itemName, byte dataValue, List<String> lore)
    {
        ItemStack i = new ItemStack(m, 1, dataValue);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack buildItem(Material m, String itemName, byte dataValue, String... lore)
    {
        ItemStack i = new ItemStack(m, 1, dataValue);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(Arrays.asList(lore));
        i.setItemMeta(meta);
        return i;
    }

    public static boolean isArmor(ItemStack item)
    {
        if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor)
        {
            return true;
        }
        return false;
    }

    public static boolean isWeapon(ItemStack item)
    {
        return swords.contains(item.getType());
    }

    public static boolean isPick(ItemStack i)
    {
        if (i == null)
        {
            return false;
        }
        switch (i.getType())
        {
            case WOOD_PICKAXE:
                return true;
            case STONE_PICKAXE:
                return true;
            case IRON_PICKAXE:
                return true;
            case GOLD_PICKAXE:
                return true;
            case DIAMOND_PICKAXE:
                return true;
            default:
                return false;
        }
    }

    public void takeOne(Player p, ItemStack i)
    {
        if (i.getAmount() <= 1)
        {
            p.getInventory().removeItem(i);
        }
        if (i.getAmount() > 1)
        {
            i.setAmount(i.getAmount() - 1);
        }
        p.updateInventory();
    }

    public static void takeOne(ItemStack i, Player p)
    {
        for (ItemStack item : p.getInventory())
        {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            {
                String name = item.getItemMeta().getDisplayName();
                if (name.equals(i.getItemMeta().getDisplayName()))
                {
                    if (item.getAmount() > 1)
                    {
                        item.setAmount(item.getAmount() - 1);
                        return;
                    }
                    p.getInventory().removeItem(i);
                    return;
                }
            }

        }
    }

    public static boolean isRawFood(ItemStack item)
    {
        if (item != null && item.getType() != Material.AIR)
        {
            switch (item.getType())
            {
                case RAW_FISH:
                    return true;
                case RAW_BEEF:
                    return true;
                case RAW_CHICKEN:
                    return true;
                case MUTTON:
                    return true;
                case RABBIT:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public static boolean isCookedFood(ItemStack item)
    {
        if (item != null && item.getType() != Material.AIR)
        {
            switch (item.getType())
            {
                case COOKED_FISH:
                    return true;
                case COOKED_BEEF:
                    return true;
                case COOKED_CHICKEN:
                    return true;
                case COOKED_MUTTON:
                    return true;
                case COOKED_RABBIT:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public static ItemStack addLore(ItemStack i, String lore)
    {
        ItemMeta meta = i.getItemMeta();
        List<String> loreList = i.getItemMeta().getLore();
        if (loreList == null)
        {
            loreList = new ArrayList<>();
        }
        loreList.add(lore);
        meta.setLore(loreList);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack setLore(ItemStack i, List<String> lore)
    {
        ItemMeta meta = i.getItemMeta();
        meta.setLore(lore);
        ItemStack i2 = i;
        i2.setItemMeta(meta);
        return i2;
    }

    public static void setName(ItemStack i, String name)
    {
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);
    }

    public static void addLore(ItemStack i, String... lore)
    {
        ItemMeta meta = i.getItemMeta();
        List<String> loreList = i.getItemMeta().getLore();
        if (loreList == null)
        {
            loreList = new ArrayList();
        }
        loreList.addAll(Arrays.asList(lore));
        meta.setLore(loreList);
        i.setItemMeta(meta);
    }
}
