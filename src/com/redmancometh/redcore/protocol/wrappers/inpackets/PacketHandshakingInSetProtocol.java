package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.lang.reflect.Method;

/**
 * Created by GyuriX on 2016.02.21..
 */
public class PacketHandshakingInSetProtocol extends WrappedPacket
{
    public String hostName;
    public EnumProtocol nextState;
    public int port;
    public int version;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketInType.HandshakingInSetProtocol.getPacketData(packet);
        version = (int) d[0];
        hostName = (String) d[1];
        port = (int) d[2];
        nextState = EnumProtocol.valueOf(d[3].toString());
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.HandshakingInSetProtocol.newPacket(version, hostName, port, nextState.getVanillaEnumProtocol());
    }

    public enum EnumProtocol
    {
        HANDSHAKING(-1), PLAY(0), STATUS(1), LOGIN(2);
        private static Method vanilla = Reflection.getMethod(Reflection.getNMSClass("EnumProtocol"), "valueOf", String.class);
        public int id;

        EnumProtocol(int id)
        {
            this.id = id;
        }

        public Object getVanillaEnumProtocol()
        {
            try
            {
                return vanilla.invoke(null, name());
            } catch (Throwable e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
}
