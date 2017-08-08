package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayOutScoreboardDisplayObjective extends WrappedPacket {
    /**
     * Display slot of the scoreboard objective, possible values:
     * 0 - list
     * 1 - sidebar
     * 2 - below name
     */
    public int displaySlot;
    public String name;

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] o = PacketOutType.ScoreboardDisplayObjective.getPacketData(packet);
        displaySlot = (Integer) o[0];
        name = (String) o[1];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.ScoreboardDisplayObjective.newPacket(displaySlot, name);
    }
}

