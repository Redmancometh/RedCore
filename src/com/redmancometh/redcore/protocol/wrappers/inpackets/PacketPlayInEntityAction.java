package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.Method;

public class PacketPlayInEntityAction extends WrappedPacket
{
    public PlayerAction action;
    public int entityId;
    public int jumpBoost;

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketInType.EntityAction.getPacketData(packet);
        entityId = (Integer) d[0];
        action = PlayerAction.valueOf(d[1].toString());
        jumpBoost = (Integer) d[2];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.EntityAction.newPacket(entityId, action.toVanillaPlayerAction(), jumpBoost);
    }

    public enum PlayerAction
    {
        START_SNEAKING, STOP_SNEAKING, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, RIDING_JUMP, OPEN_INVENTORY;

        private static final Method valueOf;

        static
        {
            valueOf = Reflection.getMethod(Reflection.getNMSClass("PacketPlayInEntityAction$EnumPlayerAction"), "valueOf", String.class);
        }

        PlayerAction()
        {
        }

        public Object toVanillaPlayerAction()
        {
            try
            {
                return valueOf.invoke(null, name());
            } catch (Throwable e)
            {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
                return null;
            }
        }
    }

}

