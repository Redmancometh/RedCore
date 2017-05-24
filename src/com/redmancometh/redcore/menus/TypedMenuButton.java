package com.redmancometh.redcore.menus;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TypedMenuButton<T>
{
    private BiFunction<Player, T, ItemStack> buttonConstructor;
    private BiConsumer<Player, T> clickAction;

    public TypedMenuButton()
    {

    }

    public TypedMenuButton(BiFunction<Player, T, ItemStack> buttonConstructor)
    {
        this.buttonConstructor = buttonConstructor;
    }

    public TypedMenuButton(BiFunction<Player, T, ItemStack> buttonConstructor, BiConsumer<Player, T> clickAction)
    {
        this.buttonConstructor = buttonConstructor;
        this.clickAction = clickAction;

    }

    public TypedMenuButton(BiConsumer<Player, T> clickAction)
    {
        this.clickAction = clickAction;
    }

    public void setAction(BiConsumer<Player, T> action)
    {
        this.clickAction = action;
    }

    public ItemStack constructButton(T t, Player p)
    {
        return buttonConstructor.apply(p, t);
    }

    public BiConsumer<Player, T> getClickAction()
    {
        return clickAction;
    }

    public void setClickAction(BiConsumer<Player, T> clickAction)
    {
        this.clickAction = clickAction;
    }
}
