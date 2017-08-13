package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

import java.lang.reflect.Method;

public class PacketPlayInClientCommand extends WrappedPacket
{
    public ClientCommand command;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        command = ClientCommand.valueOf(PacketInType.ClientCommand.getPacketData(packet)[0].toString());
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.ClientCommand.newPacket(command.toVanillaClientCommand());
    }

    public enum ClientCommand
    {
        PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT;

        private static final Method valueOf;

        static
        {
            valueOf = Reflection.getMethod(Reflection.getNMSClass("PacketPlayInClientCommand$EnumClientCommand"), "valueOf", String.class);
        }

        ClientCommand()
        {
        }

        public Object toVanillaClientCommand()
        {
            try
            {
                return valueOf.invoke(null, name());
            } catch (Throwable e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

}

