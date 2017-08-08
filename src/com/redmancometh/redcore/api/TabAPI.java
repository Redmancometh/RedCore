package com.redmancometh.redcore.api;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.spigotutils.SU;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.redmancometh.redcore.api.ChatAPI.TextToJson;
import static com.redmancometh.redcore.api.ChatAPI.toICBC;


public class TabAPI {
    public static void setGlobalHeaderFooter(String header, String footer) {
        setLocalHeaderFooter(header, footer, Bukkit.getOnlinePlayers());
    }

    public static void setLocalHeaderFooter(String header, String footer, Collection<? extends Player> plrs) {
        Object h = toICBC(TextToJson(header));
        Object f = toICBC(TextToJson(footer));
        Object packet = PacketOutType.PlayerListHeaderFooter.newPacket(h, f);
        for (Player p : plrs) {
            SU.tp.sendPacket(p, packet);
        }
    }

    public static void setLocalHeaderFooter(String header, String footer, Player... plrs) {
        Object h = toICBC(TextToJson(header));
        Object f = toICBC(TextToJson(footer));
        Object packet = PacketOutType.PlayerListHeaderFooter.newPacket(h, f);
        for (Player p : plrs) {
            SU.tp.sendPacket(p, packet);
        }
    }
}

