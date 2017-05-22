package com.redmancometh.redcore.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class LoreUtil
{
    public static List<String> colorizeLore(List<String> ogLore)
    {
        List<String> newLore = new ArrayList();
        for (int x = 0; x < ogLore.size(); x++)
            newLore.add(x, ChatColor.translateAlternateColorCodes('&', ogLore.get(x)));
        return newLore;
    }
}
