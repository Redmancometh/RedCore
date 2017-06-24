package com.redmancometh.redcore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.redmancometh.redcore.RedCore;
import com.redmancometh.redcore.RedPlugins;

public class LoginListeners implements Listener
{
    @EventHandler
    public void onLogin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        RedPlugins plugins = RedCore.getInstance().getPluginManager();
        plugins.forEach((redPlugin) ->
        {
            if (redPlugin.loginFetch()) redPlugin.getManager().getRecord(p.getUniqueId());
        });
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        RedPlugins plugins = RedCore.getInstance().getPluginManager();
        plugins.forEach((redPlugin) ->
        {
            if (redPlugin.logoutSave()) redPlugin.getManager().saveAndPurge(p);
        });
    }
}
