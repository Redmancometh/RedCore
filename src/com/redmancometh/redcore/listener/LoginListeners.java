package com.redmancometh.redcore.listener;

import com.redmancometh.redcore.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class LoginListeners implements Listener {
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
