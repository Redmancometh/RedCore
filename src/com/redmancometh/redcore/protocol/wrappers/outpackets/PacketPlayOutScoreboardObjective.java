package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.scoreboard.ScoreboardDisplayMode;

public class PacketPlayOutScoreboardObjective extends WrappedPacket {
    /**
     * Possible values:
     * 0 - create the scoreboard
     * 1 - remove the scoreboard
     * 2 - update title
     */
    public int action;
    public ScoreboardDisplayMode displayMode;
    public String name, title;


    public PacketPlayOutScoreboardObjective() {

    }

    public PacketPlayOutScoreboardObjective(Object packet) {
        loadVanillaPacket(packet);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] o = PacketOutType.ScoreboardObjective.getPacketData(packet);
        name = (String) o[0];
        title = (String) o[1];
        displayMode = o[2] == null ? null : ScoreboardDisplayMode.valueOf(o[2].toString());
        action = (int) o[3];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.ScoreboardObjective.newPacket(name, title, displayMode == null ? null : displayMode.toNMS(), action);
    }

    public PacketPlayOutScoreboardObjective(String name, String title, ScoreboardDisplayMode displayMode, int action) {
        this.name = name;
        this.title = title;
        this.displayMode = displayMode;
        this.action = action;
    }
}

