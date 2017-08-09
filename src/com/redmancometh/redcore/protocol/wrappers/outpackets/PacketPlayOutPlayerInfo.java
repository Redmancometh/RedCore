package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.*;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;
import org.bukkit.GameMode;

import java.lang.reflect.*;
import java.util.*;

public class PacketPlayOutPlayerInfo extends WrappedPacket {
    public PlayerInfoAction action = PlayerInfoAction.ADD_PLAYER;
    public ArrayList<PlayerInfoData> players = new ArrayList<>();

    public PacketPlayOutPlayerInfo()
    {

    }

    public PacketPlayOutPlayerInfo(PlayerInfoAction action, PlayerInfoData... pls)
    {
        this.action = action;
        for (PlayerInfoData pid : pls)
            players.add(pid);
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.PlayerInfo.getPacketData(packet);
        action = PlayerInfoAction.valueOf(d[0].toString());
        loadVanillaDataList((List) d[1]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.PlayerInfo.newPacket(action.toNMS(), toVanillaDataList());
    }

    private List toVanillaDataList()
    {
        List l = new ArrayList();
        for (PlayerInfoData p : players) {
            l.add(p.toNMS());
        }
        return l;
    }

    private void loadVanillaDataList(List l)
    {
        players = new ArrayList<>();
        for (Object o : l) {
            players.add(new PlayerInfoData(o));
        }
    }

    public enum PlayerInfoAction implements WrappedData {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;
        private static final Method valueOf = Reflection.getMethod(
                Reflection.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), "valueOf", String.class);

        public Object toNMS()
        {
            try {
                return valueOf.invoke(null, name());
            } catch (Throwable e) {
                return null;
            }
        }
    }

    public static class PlayerInfoData implements WrappedData {
        private static final Class vanillaParent = Reflection.getNMSClass("PacketPlayOutPlayerInfo");
        private static final Class vanillaCl = Reflection.getInnerClass(vanillaParent, "PlayerInfoData");
        private static final Field pingF = Reflection.getFirstFieldOfType(vanillaCl, int.class),
                gpF = Reflection.getFirstFieldOfType(vanillaCl, com.mojang.authlib.GameProfile.class),
                gmF = Reflection.getFirstFieldOfType(vanillaCl, WorldType.enumGmCl),
                icbcF = Reflection.getFirstFieldOfType(vanillaCl, ChatAPI.icbcClass);
        private static final Constructor vanillaConst = Reflection.getConstructor(vanillaCl,
                vanillaParent, com.mojang.authlib.GameProfile.class, int.class, WorldType.enumGmCl, ChatAPI.icbcClass);
        public ChatTag displayName;
        public GameMode gameMode;
        public GameProfile gameProfile;
        public int ping;

        public PlayerInfoData()
        {

        }

        public PlayerInfoData(int ping, GameMode gm, GameProfile gp, ChatTag dn)
        {
            this.ping = ping;
            gameMode = gm;
            gameProfile = gp;
            displayName = dn;
        }

        public PlayerInfoData(Object vd)
        {
            try {
                ping = pingF.getInt(vd);
                Object nmsGm = gmF.get(vd);
                gameMode = nmsGm == null ? null : GameMode.valueOf(nmsGm.toString());
                gameProfile = new GameProfile(gpF.get(vd));
                displayName = ChatTag.fromICBC(icbcF.get(vd));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public Object toNMS()
        {
            try {
                return vanillaConst.newInstance(null, gameProfile.toNMS(), ping,
                        WorldType.toVanillaGameMode(gameMode), displayName == null ? null : displayName.toICBC());
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }
    }
}
