package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.lang.reflect.Method;

public class PacketPlayOutScoreboardScore extends WrappedPacket {
    public ScoreAction action;
    public String board;
    public String player;
    public int score;

    public PacketPlayOutScoreboardScore()
    {
    }

    public PacketPlayOutScoreboardScore(ScoreAction action, String board, String player, int score)
    {
        this.action = action;
        this.board = board;
        this.player = player;
        this.score = score;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] data = PacketOutType.ScoreboardScore.getPacketData(packet);
        player = (String) data[0];
        board = (String) data[1];
        score = (Integer) data[2];
        action = ScoreAction.valueOf(data[3].toString());
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.ScoreboardScore.newPacket(player, board, score, action.toNMS());
    }

    public enum ScoreAction implements WrappedData {
        CHANGE,
        REMOVE;

        private static final Method valueOf = Reflection.getMethod(Reflection.getNMSClass("PacketPlayOutScoreboardScore$EnumScoreboardAction"), "valueOf", String.class);

        ScoreAction()
        {
        }

        public Object toNMS()
        {
            try {
                return valueOf.invoke(null, name());
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}

