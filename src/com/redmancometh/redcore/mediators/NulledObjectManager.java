package com.redmancometh.redcore.mediators;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.mediators.ObjectManager;

public class NulledObjectManager extends ObjectManager
{

    public NulledObjectManager()
    {
        super(RedCore.class);
    }

    @Override
    public CompletableFuture getRecord(UUID uuid)
    {
        return CompletableFuture.runAsync(() ->
        {
        });
    }

    @Override
    public CompletableFuture save(Player p)
    {
        return CompletableFuture.runAsync(() ->
        {
        });
    }

    @Override
    public CompletableFuture saveAndPurge(Player p)
    {
        return CompletableFuture.runAsync(() ->
        {
        });
    }
}
