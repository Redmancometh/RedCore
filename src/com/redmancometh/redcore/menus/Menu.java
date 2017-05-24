package com.redmancometh.redcore.menus;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.redmancometh.redcore.util.ItemUtil;

public abstract class Menu
{
    protected String name;
    protected Map<Integer, MenuButton> actionMap = new ConcurrentHashMap();
    protected Function<Player, Inventory> constructInventory;
    int size = 18;
    protected boolean allowLower = false;
    private boolean lowerMenu = false;

    public boolean allowsClickLower()
    {
        return allowLower;
    }

    public void setAllowClickLower(boolean letClickLower)
    {
        this.allowLower = letClickLower;
    }

    /**
     * Construct a menu, and provide your own generified inventory constructor
     * @param name
     * @param constructInventory2
     */
    public Menu(String name, Function<Player, Inventory> constructInventory2)
    {
        this.name = name;
        this.constructInventory = constructInventory2;
    }

    /**
     * Construct a menu with completely default parameters. 
     * This well default to size 9, and the default generified inventory constructor.
     * @param name
     * @param size
     */
    public Menu(String name)
    {
        this.name = name;
        this.constructInventory = (p) ->
        {
            Inventory menuInv = Bukkit.createInventory(null, size, this.getName());
            actionMap.forEach((number, button) -> menuInv.setItem(number, button.constructButton(p)));
            return menuInv;
        };
    }

    /**
     * Construct a menu with the default inventory constructor.
     * This constructor will call each MenuButton on the menu, and set the 
     * inventory's items from MenuButton.constructButton(T t, Player p)
     * @param name
     * @param size
     */
    public Menu(String name, int size)
    {
        this.name = name;
        this.size = size;
        this.constructInventory = (p) ->
        {
            Inventory menuInv = Bukkit.createInventory(null, size, this.getName());
            actionMap.forEach((number, button) -> menuInv.setItem(number, button.constructButton(p)));
            return menuInv;
        };
    }

    /**
     * Construct an inventory with both a custom constructor function, and non-default size
     * @param name
     * @param size
     */
    public Menu(String name, Function<Player, Inventory> constructInventory2, int size)
    {
        this.name = name;
        this.constructInventory = constructInventory2;
        this.size = size;
    }

    public void setButton(int slot, MenuButton button)
    {
        actionMap.put(slot, button);
    }

    public boolean hasActionAt(int slot)
    {
        return actionMap.containsKey(slot);
    }

    public BiConsumer<ClickType, Player> getActionAt(int slot)
    {
        return actionMap.get(slot).getClickAction();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Function<Player, Inventory> getConstructInventory()
    {
        return constructInventory;
    }

    public void setConstructInventory(Function<Player, Inventory> constructInventory)
    {
        this.constructInventory = constructInventory;
    }

    public boolean isLowerMenu()
    {
        return lowerMenu;
    }

    public void setLowerMenu(boolean lowerMenu)
    {
        this.lowerMenu = lowerMenu;
    }

    public Menu decorateMenu(int dataValueOne, int dataValueTwo, int dataValueThree)
    {
        for (int x = 0; x < size; x++)
        {
            if (actionMap.containsKey(x)) continue;
            if (x == 0 || (x % 9 == 0) || ((x + 1) % 9 == 0))
                setButton(x, new MenuButton((p) -> ItemUtil.buildItem(Material.STAINED_GLASS_PANE, " ", (short) dataValueOne, new ArrayList())));
            else if (x % 3 == 1)
                setButton(x, new MenuButton((p) -> ItemUtil.buildItem(Material.STAINED_GLASS_PANE, " ", (short) dataValueTwo, new ArrayList())));
            else
                setButton(x, new MenuButton((p) -> ItemUtil.buildItem(Material.STAINED_GLASS_PANE, " ", (short) dataValueThree, new ArrayList())));
        }
        return this;
    }
}
