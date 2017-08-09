package com.redmancometh.redstats.stats;

import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.databasing.SubDatabase;
import com.redmancometh.redcore.exceptions.ObjectNotPresentException;
import com.redmancometh.redstats.PlayerStatRecord;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatManager {
    public void incrementStat(Player p, StatType type)
    {
        incrementStat(p, type, 1);
    }

    public void incrementStat(Player p, StatType type, int increment)
    {
        getRecord(p.getUniqueId()).thenAccept((record) -> record.getStatMap().compute(type, (t, amount) -> amount + increment));
    }

    public CompletableFuture<PlayerStatRecord> getRecord(UUID uuid)
    {
        return getSubDB().getObject(uuid);
    }

    private SubDatabase<UUID, PlayerStatRecord> getSubDB()
    {
        return RedCore.getInstance().getMasterDB().getSubDBForType(PlayerStatRecord.class);
    }

    public CompletableFuture<Void> save(Player p)
    {
        return save(p.getUniqueId());
    }

    public CompletableFuture<Void> save(UUID uuid)
    {
        return getSubDB().getObject(uuid).thenAccept((record) -> getSubDB().saveObject(record));
    }

    public void saveAndPurge(Player p)
    {
        UUID uuid = p.getUniqueId();
        getSubDB().getObject(uuid).thenAccept((record) ->
        {
            try {
                getSubDB().saveAndPurge(record);
            } catch (ObjectNotPresentException e) {
                e.printStackTrace();
            }
        });
    }

    public void subtractFromStat(Player p, StatType type, int amount)
    {
        UUID uuid = p.getUniqueId();
        getSubDB().getObject(uuid).thenAccept((record) -> record.setStat(type, record.getStat(type) - 1));
    }

}
