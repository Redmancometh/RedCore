package com.redmancometh.redstats.stats.listeners;

import com.redmancometh.redstats.RedStats;
import com.redmancometh.redstats.stats.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatListeners implements Listener
{

    public StatListeners()
    {
        scheduleTicks();
    }

    public void scheduleTicks()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RedStats.getInstance(), () -> Bukkit.getOnlinePlayers().forEach((p) -> RedStats.getInstance().getStatManager().incrementStat(p, StatType.TIME_PLAYED, 1)), 20, 20);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        RedStats.getInstance().getStatManager().incrementStat(e.getPlayer(), StatType.BLOCKS_BROKEN);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        RedStats.getInstance().getStatManager().incrementStat(e.getPlayer(), StatType.BLOCKS_PLACED);
    }

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent e)
    {
        if (e.getDamager() instanceof Player)
        {
            RedStats.getInstance().getStatManager().incrementStat((Player) e.getDamager(), StatType.DAMAGE, (int) e.getDamage());
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e)
    {
        RedStats.getInstance().getStatManager().saveAndPurge(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        RedStats.getInstance().getStatManager().getRecord(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKill(EntityDeathEvent e)
    {
        if (e.getEntity() instanceof Player)
        {
            RedStats.getInstance().getStatManager().incrementStat((Player) e.getEntity(), StatType.DEATHS);

        } else if (e.getEntity().getKiller() instanceof Player)
        {
            RedStats.getInstance().getStatManager().incrementStat(e.getEntity().getKiller(), StatType.KILLS);
        }
    }
}
