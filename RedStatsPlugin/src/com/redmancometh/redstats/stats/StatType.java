package com.redmancometh.redstats.stats;

import java.util.function.Consumer;

public enum StatType
{
    TIME_PLAYED("played"), DAMAGE("damage"), KILLS("kills"), DEATHS("deaths"), BLOCKS_BROKEN("broken"), BLOCKS_PLACED("placed");
    private String name;

    StatType(String name)
    {
        this.setName(name);
    }

    public static void forEach(Consumer<StatType> consumer)
    {
        for (StatType type : values())
            consumer.accept(type);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
