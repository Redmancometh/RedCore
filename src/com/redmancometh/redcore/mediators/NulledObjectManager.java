package com.redmancometh.redcore.mediators;

import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.util.SpecialFuture;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NulledObjectManager extends ObjectManager {

    public NulledObjectManager()
    {
        super(RedCore.class);
    }

    @Override
    public SpecialFuture<?> getRecord(UUID uuid)
    {
        return SpecialFuture.runAsync(() ->
        {
        });
    }

    @Override
    public SpecialFuture save(Player p)
    {
        return new SpecialFuture(() ->
        {
            return null;
        });
    }

    @Override
    public SpecialFuture saveAndPurge(Player p)
    {
        return new SpecialFuture(() ->
        {
            return null;
        });
    }
}
