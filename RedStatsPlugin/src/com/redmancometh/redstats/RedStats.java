package com.redmancometh.redstats;

import com.redmancometh.redcore.RedPlugin;
import com.redmancometh.redcore.RedPlugins;
import com.redmancometh.redstats.stats.StatManager;
import com.redmancometh.redstats.stats.listeners.StatListeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedStats extends JavaPlugin implements RedPlugin
{
    private List<Class> classList = new CopyOnWriteArrayList();
    private SessionFactory factory;
    private StatManager statManager;

    @Override
    public List<Class> getMappedClasses()
    {
        return classList;
    }

    @Override
    public JavaPlugin getBukkitPlugin()
    {
        return this;
    }

    @Override
    public SessionFactory getInternalFactory()
    {
        return factory;
    }

    @Override
    public void setInternalFactory(SessionFactory factory)
    {
        this.factory = factory;
    }

    public StatManager getStatManager()
    {
        return statManager;
    }

    @Override
    public void onEnable()
    {
        classList.add(PlayerStatRecord.class);
        classList.add(UUID.class);
        this.enable();
        statManager = new StatManager();
        Bukkit.getPluginManager().registerEvents(new StatListeners(), getInstance());
    }

    public static RedStats getInstance()
    {
        return (RedStats) RedPlugins.getInstance(RedStats.class);
    }

}
