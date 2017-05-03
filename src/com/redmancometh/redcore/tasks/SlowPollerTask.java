package com.redmancometh.redcore.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.redmancometh.redcore.RedCore;

public class SlowPollerTask extends BukkitRunnable
{
    public void startTask()
    {
        this.runTaskTimer(RedCore.getInstance(), 0, 6000);
    }

    public void stopTask()
    {
        this.cancel();
    }

    @Override
    public void run()
    {
        RedCore.getInstance().getPluginManager().forEach((plugin) -> plugin.poll());
    }

}
