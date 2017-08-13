package com.redmancometh.redcore.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

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
