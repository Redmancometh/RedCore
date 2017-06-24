package com.redmancometh.redcore;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.redmancometh.redcore.mediators.ObjectManager;

public interface RedPlugin
{
    static JSONParser parser = new JSONParser();
    //Automatically rebuild the 
    static LoadingCache<String, JSONObject> configCache = CacheBuilder.newBuilder().build(new CacheLoader<String, JSONObject>()
    {
        @Override
        public JSONObject load(String javaPlugin) throws Exception
        {
            return buildConfigFromPlugin((JavaPlugin) Bukkit.getPluginManager().getPlugin(javaPlugin));
        }
    });

    public abstract String getName();

    public default boolean loginFetch()
    {
        return true;
    }

    public default boolean logoutSave()
    {
        return true;
    }

    public default void enable()
    {
        RedCore.getInstance().getPluginManager().loadPlugin(this);
    }

    public default void disable()
    {
        RedCore.getInstance().getPluginManager().unloadPlugin(this.getClass());
    }

    public default void poll()
    {
        configCache.refresh(getName());
    }

    default void initialize()
    {
        if (!(this instanceof MenuPlugin))
        {
            SessionFactory factory = buildSessionFactory(getBukkitPlugin());
            setInternalFactory(factory);
            getMappedClasses().forEach((mappingClass) -> RedCore.getInstance().getMasterDB().registerDatabase(mappingClass, factory));
        }
    }

    public abstract List<Class> getMappedClasses();

    public abstract JavaPlugin getBukkitPlugin();

    public static JSONObject buildConfigFromPlugin(JavaPlugin plugin)
    {
        File hibernateConfig = new File(plugin.getDataFolder(), "config.json");
        if (!hibernateConfig.exists()) plugin.saveResource("config.json", true);
        try (FileReader scanner = new FileReader(hibernateConfig))
        {
            return (JSONObject) parser.parse(scanner);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Configuration not initialized properly. Either config.json is missing, corrupted, or ill-formatted");
        }
    }

    public default JSONObject getConfiguration()
    {
        try
        {
            return configCache.get(this.getName());
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public default SessionFactory buildSessionFactory(JavaPlugin plugin)
    {
        File hibernateConfig = new File(plugin.getDataFolder(), "hibernate.cfg.xml");
        if (!hibernateConfig.exists()) plugin.saveResource("hibernate.cfg.xml", true);
        Configuration config = new Configuration().configure(hibernateConfig);
        JSONObject jsonConfig = getConfiguration();
        JSONObject dbConfig = (JSONObject) jsonConfig.get("DB");
        config.setProperty("hibernate.hikari.dataSource.user", dbConfig.get("user").toString());
        config.setProperty("hibernate.hikari.dataSource.password", dbConfig.get("password").toString());
        config.setProperty("hibernate.hikari.dataSource.url", dbConfig.get("url").toString());
        SessionFactory sessionFactory = config.buildSessionFactory();
        return sessionFactory;
    }

    public default void registerMenus()
    {

    }

    public default void unRegisterMenus()
    {

    }

    public abstract ObjectManager getManager();

    public abstract SessionFactory getInternalFactory();

    public abstract void setInternalFactory(SessionFactory factory);

    public default SessionFactory getSessionFactory()
    {
        if (getInternalFactory() == null)
        {
            SessionFactory factory = buildSessionFactory(getBukkitPlugin());
            setInternalFactory(factory);
            return factory;
        }
        return getInternalFactory();
    }

}
