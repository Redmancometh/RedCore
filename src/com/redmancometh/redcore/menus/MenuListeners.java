package com.redmancometh.redcore.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import com.redmancometh.redcore.RedCore;

public class MenuListeners implements Listener
{
    @EventHandler
    public void listenForBasicClick(InventoryClickEvent e)
    {
        if (e.getClickedInventory() != null && e.getClickedInventory().getTitle() != null)
        {
            if (RedCore.getInstance().getMenuManagerInstance().hasMenuWithTitle(e.getClickedInventory().getTitle()))
            {
                e.setCancelled(true);
                Menu m = RedCore.getInstance().getMenuManagerInstance().getMenuFromTitle(e.getClickedInventory().getTitle());
                if (m != null && m.hasActionAt(e.getRawSlot()))
                {
                    Player p = (Player) e.getWhoClicked();
                    if (m.getActionAt(e.getRawSlot()) == null) return;
                    m.getActionAt(e.getRawSlot()).accept(getClickType(e.isShiftClick(), e.isRightClick()), p);
                }
                //TODO: This cannot stay like this
            }

        }
    }

    public ClickType getClickType(boolean isShift, boolean isRight)
    {
        if (isShift)
        {
            if (isRight)
            {
                return ClickType.SHIFT_RIGHT;
            }
            return ClickType.SHIFT_LEFT;
        }
        if (isRight)
        {
            return ClickType.RIGHT;
        }
        return ClickType.LEFT;
    }

    @EventHandler
    public void cancelLowerClick(InventoryCloseEvent e)
    {
        if (e.getInventory() != null && e.getInventory().getTitle() != null)
        {
            if (RedCore.getInstance().getMenuManagerInstance().hasMenuWithTitle(e.getInventory().getTitle()))
            {
                Menu m = RedCore.getInstance().getMenuManagerInstance().getMenuFromTitle(e.getInventory().getTitle());
                if (m instanceof SubMenu && (!e.getPlayer().hasMetadata("lowermenu")))
                {
                    ((SubMenu) m).closeMenu((Player) e.getPlayer());
                    e.getPlayer().removeMetadata("lowermenu", RedCore.getInstance());
                }
            }
        }
    }

    //TODO: Add filter for top/bottom menu clicks
    @EventHandler
    public void cancelLowerClick(InventoryClickEvent e)
    {
        if (e.getInventory() != null && e.getInventory().getTitle() != null)
        {
            if (RedCore.getInstance().getMenuManagerInstance().hasMenuWithTitle(e.getInventory().getTitle()))
            {
                e.setCancelled(true);
                Menu m = RedCore.getInstance().getMenuManagerInstance().getMenuFromTitle(e.getInventory().getTitle());
                //Player p = (Player) e.getWhoClicked();
                if (!m.allowsClickLower())
                {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

}
