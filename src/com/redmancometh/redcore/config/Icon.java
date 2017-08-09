package com.redmancometh.redcore.config;

import com.redmancometh.redcore.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Icon {
    private Short dataValue = 0;
    private String displayName;
    private List<String> lore;
    private Material material;

    public short getDataValue()
    {
        return (dataValue != null) ? dataValue : 0;
    }

    public void setDataValue(short dataValue)
    {
        this.dataValue = dataValue;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public ItemStack getIcon()
    {
        return ItemUtil.buildItem(material, displayName, dataValue, lore);
    }

    public List<String> getLore()
    {
        return lore;
    }

    public void setLore(List<String> lore)
    {
        this.lore = lore;
    }

}
