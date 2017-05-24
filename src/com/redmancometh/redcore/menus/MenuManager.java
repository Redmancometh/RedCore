package com.redmancometh.redcore.menus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MenuManager
{
    public Map<String, Menu> menuMap = new ConcurrentHashMap();

    public void addMenu(Menu m)
    {
        menuMap.put(m.getName(), m);
    }

    public Menu getMenuFromTitle(String title)
    {
        return menuMap.get(title);
    }

    public boolean hasMenuWithTitle(String name)
    {
        return menuMap.containsKey(name);
    }
}
