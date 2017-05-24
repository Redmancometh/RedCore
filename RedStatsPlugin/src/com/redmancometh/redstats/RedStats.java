package com.redmancometh.redstats;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import com.redmancometh.redcore.RedPlugin;
import com.redmancometh.redcore.RedPlugins;
import com.redmancometh.redstats.stats.StatManager;
import com.redmancometh.redstats.stats.listeners.StatListeners;

public class RedStats extends JavaPlugin implements RedPlugin
{
    private SessionFactory factory;
    private List<Class> classList = new CopyOnWriteArrayList();
    private StatManager statManager;

    @Override
    public void onEnable()
    {
        classList.add(PlayerStatRecord.class);
        classList.add(UUID.class);
        this.enable();
        statManager = new StatManager();
        Bukkit.getPluginManager().registerEvents(new StatListeners(), getInstance());
    }

    public StatManager getStatManager()
    {
        return statManager;
    }

    public static RedStats getInstance()
    {
        return (RedStats) RedPlugins.getInstance(RedStats.class);
    }

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

}
