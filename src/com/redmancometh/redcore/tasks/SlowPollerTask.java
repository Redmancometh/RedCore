package com.redmancometh.redcore.tasks;

import com.redmancometh.redcore.RedCore;
import org.bukkit.scheduler.BukkitRunnable;

public class SlowPollerTask extends BukkitRunnable
{
    @Override
    public void run()
    {
        RedCore.getInstance().getPluginManager().forEach((plugin) -> plugin.poll());
    }

    public void startTask()
    {
        this.runTaskTimer(RedCore.getInstance(), 0, 6000);
    }

    public void stopTask()
    {
        this.cancel();
    }

}
