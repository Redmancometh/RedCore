package com.redmancometh.redcore.protocol.event;

import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public abstract class PacketEvent extends Event implements Cancellable {
    private static final HandlerList hl = new HandlerList();
    private final Object channel;
    private final Player player;
    protected Object packet;
    private boolean cancelled;

    public PacketEvent(Object channel, Player plr, Object packet) {
        this.channel = channel;
        this.packet = packet;
        player = plr;
    }

    public static HandlerList getHandlerList() {
        return hl;
    }

    public Object getChannel() {
        return channel;
    }

    public HandlerList getHandlers() {
        return hl;
    }

    public Object getPacket() {
        return packet;
    }

    public void setPacket(WrappedPacket packet) {
        this.packet = packet.getVanillaPacket();
    }

    public abstract Object[] getPacketData();

    public abstract void setPacketData(Object... var1);

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public void setPacket(Object packet) {
        this.packet = packet;
    }

    public abstract boolean setPacketData(int var1, Object var2);
}

