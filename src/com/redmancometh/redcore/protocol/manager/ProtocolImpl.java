package com.redmancometh.redcore.protocol.manager;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import com.redmancometh.redcore.protocol.Protocol;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInEvent;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.event.PacketOutEvent;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.EntityUtils;
import com.redmancometh.redcore.spigotutils.SU;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.redmancometh.redcore.protocol.Reflection.*;
import static com.redmancometh.redcore.spigotutils.SU.pl;

public final class ProtocolImpl extends Protocol
{
    static final Field getGameProfile = getFirstFieldOfType(getNMSClass("PacketLoginInStart"), GameProfile.class), playerConnectionF = getField(getNMSClass("EntityPlayer"), "playerConnection"), networkManagerF = getField(getNMSClass("PlayerConnection"), "networkManager"), channelF = getField(getNMSClass("NetworkManager"), "channel");
    private static final Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();
    private static final Method handleM = getMethod(getNMSClass("NetworkManager"), "handle", getNMSClass("Packet"));
    private static final Class minecraftServerClass = getNMSClass("MinecraftServer");
    private static final Map<Channel, Object> networkManagers = new MapMaker().weakValues().makeMap();
    private static final Class serverConnectionClass = getNMSClass("ServerConnection");
    private static Object oldH;
    private static Field oldHChildF;
    private ChannelFuture cf;

    public ProtocolImpl()
    {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        injectPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent e)
    {
        injectPlayer(e.getPlayer());
    }

    @Override
    public Player getPlayer(Object channel)
    {
        ClientChannelHook ch = ((Channel) channel).pipeline().get(ClientChannelHook.class);
        if (ch == null) return null;
        return ch.player;
    }

    @Override
    public void unregisterServerChannelHandler() throws IllegalAccessException
    {
        removeHandler(cf.channel(), "RedCoreServer");
    }

    @Override
    public void injectPlayer(final Player plr)
    {
        Channel ch = getChannel(plr);
        if (ch != null)
        {
            ClientChannelHook cch = ch.pipeline().get(ClientChannelHook.class);
            if (cch != null) cch.player = plr;
        }
    }

    @Override
    public void init() throws Throwable
    {
        Object minecraftServer = getFirstFieldOfType(Reflection.getOBCClass("CraftServer"), minecraftServerClass).get(SU.srv);
        Object serverConnection = getFirstFieldOfType(minecraftServerClass, serverConnectionClass).get(minecraftServer);
        cf = (ChannelFuture) ((List) getFirstFieldOfType(serverConnectionClass, List.class).get(serverConnection)).iterator().next();
        registerServerChannelHook();
        SU.srv.getOnlinePlayers().forEach(this::injectPlayer);
    }

    @Override
    public Channel getChannel(Player plr)
    {
        if (plr == null) return null;
        Channel c = channelLookup.get(plr.getName());
        if (c == null) try
        {
            Object nmsPlayer = EntityUtils.getNMSEntity(plr);
            Object playerConnection = playerConnectionF.get(nmsPlayer);
            Object networkManager = networkManagerF.get(playerConnection);
            Channel channel = (Channel) channelF.get(networkManager);
            SU.cs.sendMessage("Channel is " + channel);
            channelLookup.put(plr.getName(), c = channel);
        } catch (Throwable e)
        {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return c;
    }

    @Override
    public void printPipeline(Iterable<Map.Entry<String, ?>> pipeline)
    {
        ArrayList<String> list = new ArrayList<>();
        pipeline.forEach((e) -> list.add(e.getKey()));
        SU.cs.sendMessage("§ePipeline: §f" + StringUtils.join(list, ", "));
    }

    @Override
    public void receivePacket(Object channel, Object packet)
    {
        if (packet instanceof WrappedPacket) packet = ((WrappedPacket) packet).getVanillaPacket();
        ((Channel) channel).pipeline().context("encoder").fireChannelRead(packet);
    }

    @Override
    public void sendPacket(Object channel, Object packet)
    {
        if (channel == null || packet == null)
        {
            SU.error(SU.cs, new RuntimeException("§cFailed to send packet " + packet + " to channel " + channel), "RedCore", "com.redmancometh");
            return;
        }
        if (packet instanceof WrappedPacket) packet = ((WrappedPacket) packet).getVanillaPacket();
        try
        {
            handleM.invoke(networkManagers.get(channel), packet);
        } catch (Throwable e)
        {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
    }

    @Override
    public void registerServerChannelHook() throws Throwable
    {
        Channel serverCh = cf.channel();
        oldH = serverCh.pipeline().get(Reflection.getClass("io.netty.bootstrap.ServerBootstrap$ServerBootstrapAcceptor"));
        oldHChildF = Reflection.getField(oldH.getClass(), "childHandler");
        serverCh.pipeline().addFirst("RedCoreServer", new ServerChannelHook((ChannelHandler) oldHChildF.get(oldH)));
    }

    @Override
    public void removeHandler(Object ch, String handler)
    {
        try
        {
            ((Channel) ch).pipeline().remove(handler);
        } catch (Throwable ignored)
        {
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent e)
    {
        final String pln = e.getPlayer().getName();
        SU.sch.scheduleSyncDelayedTask(pl(), () -> {
            Player p = Bukkit.getPlayer(pln);
            if (p == null) channelLookup.remove(pln);
        });
    }

    public class ClientChannelHook extends ChannelDuplexHandler
    {
        public Player player;

        public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception
        {
            try
            {
                Channel channel = ctx.channel();
                PacketInEvent e = new PacketInEvent(channel, player, packet);
                if (e.getType() == PacketInType.LoginInStart)
                {
                    GameProfile profile = (GameProfile) getGameProfile.get(packet);
                    channelLookup.put(profile.getName(), channel);
                }
                dispatchPacketInEvent(e);
                packet = e.getPacket();
                if (!e.isCancelled()) ctx.fireChannelRead(packet);
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }

        public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception
        {
            try
            {
                PacketOutEvent e = new PacketOutEvent(ctx.channel(), player, packet);
                dispatchPacketOutEvent(e);
                packet = e.getPacket();
                if (!e.isCancelled()) ctx.write(packet, promise);
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
        }
    }

    public class ServerChannelHook extends ChannelInboundHandlerAdapter
    {
        public final ChannelHandler childHandler;

        public ServerChannelHook(ChannelHandler childHandler)
        {
            this.childHandler = childHandler;
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
        {
            if (childHandler.getClass().getName().equals("lilypad.bukkit.connect.injector.NettyChannelInitializer"))
                Reflection.getField(childHandler.getClass(), "oldChildHandler").set(childHandler, oldHChildF.get(oldH));
            Channel c = (Channel) msg;
            c.pipeline().addLast("RedCoreInit", new ChannelInboundHandlerAdapter()
            {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception
                {
                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.remove("RedCoreInit");
                    networkManagers.put(ctx.channel(), pipeline.get("packet_handler"));
                    pipeline.addBefore("packet_handler", "RedCore", new ClientChannelHook());
                    ctx.fireChannelRead(o);
                }
            });
            ctx.fireChannelRead(msg);
        }
    }
}

