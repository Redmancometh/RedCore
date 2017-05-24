package com.redmancometh.redcore.menus;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MenuButton
{
    private Function<Player, ItemStack> buttonConstructor;
    private BiConsumer<ClickType, Player> clickAction;

    public MenuButton()
    {

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

    public MenuButton(Function<Player, ItemStack> buttonConstructor)
    {
        this.buttonConstructor = buttonConstructor;
    }

    public MenuButton(Function<Player, ItemStack> buttonConstructor, BiConsumer<ClickType, Player> clickAction)
    {
        this.buttonConstructor = buttonConstructor;
        this.clickAction = clickAction;

    }

    public void setConstructor(Function<Player, ItemStack> buttonConstructor)
    {
        this.buttonConstructor = buttonConstructor;
    }

    public void setAction(BiConsumer<ClickType, Player> action)
    {
        this.clickAction = action;
    }

    public ItemStack constructButton(Player p)
    {
        return buttonConstructor.apply(p);
    }

    public BiConsumer<ClickType, Player> getClickAction()
    {
        return clickAction;
    }

    public void setClickAction(BiConsumer<ClickType, Player> clickAction)
    {
        this.clickAction = clickAction;
    }
}
