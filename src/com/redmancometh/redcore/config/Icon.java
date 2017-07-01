package com.redmancometh.redcore.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.redmancometh.redcore.util.ItemUtil;

public class Icon
{
	private Material material;
	private Short dataValue = 0;
	private String displayName;
	private List<String> lore;

	public ItemStack getIcon()
	{
		return ItemUtil.buildItem(material, displayName, dataValue, lore);
	}

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

	public List<String> getLore()
	{
		return lore;
	}

	public void setLore(List<String> lore)
	{
		this.lore = lore;
	}

}
