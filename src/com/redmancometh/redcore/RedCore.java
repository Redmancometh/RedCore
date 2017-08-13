package com.redmancometh.redcore;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redmancometh.redcore.databasing.MasterDatabase;
import com.redmancometh.redcore.listener.SUListener;
import com.redmancometh.redcore.spigotutils.SU;
import com.redmancometh.redcore.tasks.SlowPollerTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RedCore extends JavaPlugin
{
    private RedPlugins getPlugins;
    private MasterDatabase masterDB;
    private Executor pool = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder().setNameFormat("RedCore-%d").build());
    private SessionFactory sessionFactory;
    private SlowPollerTask slowPoller;

    public static RedCore getInstance()
    {
        return (RedCore) Bukkit.getPluginManager().getPlugin("RedCore");
    }

    public MasterDatabase getMasterDB()
    {
        return masterDB;
    }

    public void setMasterDB(MasterDatabase masterDB)
    {
        this.masterDB = masterDB;
    }

    public RedPlugins getPluginManager()
    {
        return getPlugins;
    }

    public void setPluginManager(RedPlugins pluginManager)
    {
        this.getPlugins = pluginManager;
    }

    public Executor getPool()
    {
        return pool;
    }

    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public SlowPollerTask getSlowPoller()
    {
        return slowPoller;
    }

    public void setSlowPoller(SlowPollerTask slowPoller)
    {
        this.slowPoller = slowPoller;
    }

    @Override
    public void onLoad()
    {
        SU.init(this);
    }

    @Override
    public void onDisable()
    {
        SUListener.onDisable();
        slowPoller.stopTask();
        super.onDisable();
    }

    @Override
    public void onEnable()
    {
        SU.sch.scheduleSyncDelayedTask(this, SU::postInit);
        setPluginManager(new RedPlugins());
        setMasterDB(new MasterDatabase());
        slowPoller = new SlowPollerTask();
        slowPoller.startTask();
    }
}
