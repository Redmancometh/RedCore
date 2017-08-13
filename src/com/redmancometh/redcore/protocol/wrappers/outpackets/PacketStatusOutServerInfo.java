package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.GameProfile;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by GyuriX on 2016.03.08..
 */
public class PacketStatusOutServerInfo extends WrappedPacket
{
    public ServerInfo info;

    public PacketStatusOutServerInfo()
    {

    }

    public PacketStatusOutServerInfo(ServerInfo info)
    {
        this.info = info;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        info = new ServerInfo(PacketOutType.StatusOutServerInfo.getPacketData(packet)[0]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.StatusOutServerInfo.newPacket(info.toNMS());
    }

    public static class PlayerList implements WrappedData
    {
        private static Class nmsClass = Reflection.getNMSClass("ServerPing$ServerPingPlayerSample");
        private static Field[] fields = nmsClass.getDeclaredFields();

        static
        {
            try
            {
                for (int i = 0; i < 3; ++i)
                    fields[i].setAccessible(true);
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public int max, online;
        public ArrayList<GameProfile> sample = new ArrayList<>();

        public PlayerList()
        {
        }

        public PlayerList(Object nms)
        {
            try
            {
                max = fields[0].getInt(nms);
                online = fields[1].getInt(nms);
                for (Object o : (Object[]) fields[2].get(nms))
                {
                    sample.add(new GameProfile(o));
                }
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public PlayerList clone()
        {
            PlayerList out = new PlayerList();
            out.max = max;
            out.online = online;
            for (GameProfile gp : sample)
            {
                out.sample.add(gp.clone());
            }
            return out;
        }

        @Override
        public Object toNMS()
        {
            try
            {
                Object nms = Reflection.newInstance(nmsClass);
                fields[0].set(nms, max);
                fields[1].set(nms, online);
                Object[] nmsGPs = (Object[]) Array.newInstance(GameProfile.cl, sample.size());
                for (int i = 0; i < sample.size(); ++i)
                    nmsGPs[i] = sample.get(i).toNMS();
                fields[2].set(nms, nmsGPs);
                return nms;
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }
    }

    public static class ServerData implements WrappedData
    {
        private static Class nmsClass = Reflection.getNMSClass("ServerPing$ServerData");
        private static Field nameField = Reflection.getFirstFieldOfType(nmsClass, String.class);
        private static Field protocolField = Reflection.getFirstFieldOfType(nmsClass, int.class);
        public String name;
        public int protocol;

        public ServerData()
        {

        }

        public ServerData(Object nms)
        {
            try
            {
                name = (String) nameField.get(nms);
                protocol = protocolField.getInt(nms);
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public ServerData(String name, int protocol)
        {
            this.name = name;
            this.protocol = protocol;
        }

        @Override
        public Object toNMS()
        {
            try
            {
                Object nms = Reflection.newInstance(nmsClass);
                nameField.set(nms, name);
                protocolField.setInt(nms, protocol);
                return nms;
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }

        public ServerData clone()
        {
            return new ServerData(name, protocol);
        }
    }

    public static class ServerInfo implements WrappedData
    {
        private static Class nmsClass = Reflection.getNMSClass("ServerPing");
        private static Field[] fields = nmsClass.getDeclaredFields();

        static
        {
            try
            {
                for (int i = 0; i < 4; ++i)
                    fields[i].setAccessible(true);
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public String description;
        public String favicon;
        public PlayerList players;
        public ServerData version;

        public ServerInfo()
        {

        }

        public ServerInfo(Object nms)
        {
            try
            {
                description = ChatTag.fromICBC(fields[0].get(nms)).toColoredString();
                players = new PlayerList(fields[1].get(nms));
                version = new ServerData(fields[2].get(nms));
                favicon = (String) fields[3].get(nms);
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public ServerInfo clone()
        {
            ServerInfo out = new ServerInfo();
            out.description = description;
            out.favicon = favicon;
            out.players = players.clone();
            out.version = version.clone();
            return out;
        }

        @Override
        public Object toNMS()
        {
            try
            {
                Object nms = Reflection.newInstance(nmsClass);
                fields[0].set(nms, ChatTag.fromColoredText(description).toICBC());
                fields[1].set(nms, players.toNMS());
                fields[2].set(nms, version.toNMS());
                fields[3].set(nms, favicon);
                return nms;
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }


    }

}