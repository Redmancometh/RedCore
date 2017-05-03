package com.redmancometh.redcore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redmancometh.redcore.databasing.MasterDatabase;
import com.redmancometh.redcore.tasks.SlowPollerTask;

public class RedCore extends JavaPlugin
{
    private SessionFactory sessionFactory;
    private Executor pool = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder().setNameFormat("RedCore-%d").build());
    private RedPlugins getPlugins;
    private static RedCore instance;
    private MasterDatabase masterDB;
    private SlowPollerTask slowPoller;

    @Override
    public void onEnable()
    {
        instance = this;
        setPluginManager(new RedPlugins());
        setMasterDB(new MasterDatabase());
        slowPoller = new SlowPollerTask();
        slowPoller.startTask();
    }

    @Override
    public void onDisable()
    {
        slowPoller.stopTask();
        super.onDisable();
    }

    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public Executor getPool()
    {
        return pool;
    }

    public static RedCore getInstance()
    {
        return instance;
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

    public SlowPollerTask getSlowPoller()
    {
        return slowPoller;
    }

    public void setSlowPoller(SlowPollerTask slowPoller)
    {
        this.slowPoller = slowPoller;
    }

}
