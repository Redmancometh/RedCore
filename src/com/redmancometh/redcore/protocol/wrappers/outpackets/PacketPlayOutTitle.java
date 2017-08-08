package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.api.ChatAPI;
import com.redmancometh.redcore.chat.ChatTag;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.WrappedData;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.Method;

import static com.redmancometh.redcore.chat.ChatTag.fromICBC;
import static com.redmancometh.redcore.protocol.Reflection.getMethod;
import static com.redmancometh.redcore.protocol.Reflection.getNMSClass;

/**
 * Created by com.redmancometh on 11/17/2016.
 */
public class PacketPlayOutTitle extends WrappedPacket {
    public TitleAction action;
    public int fadeIn, showTime, fadeOut;
    public ChatTag tag;

    public PacketPlayOutTitle() {

    }

    public PacketPlayOutTitle(TitleAction action, ChatTag tag, int fadeIn, int showTime, int fadeOut) {
        this.action = action;
        this.tag = tag;
        this.fadeIn = fadeIn;
        this.showTime = showTime;
        this.fadeOut = fadeOut;
    }

    @Override
    public void loadVanillaPacket(Object obj) {
        Object[] d = PacketOutType.Title.getPacketData(obj);
        action = TitleAction.valueOf(d[0].toString());
        tag = d[1] == null ? null : fromICBC(d[1]);
        fadeIn = (int) d[2];
        showTime = (int) d[3];
        fadeOut = (int) d[4];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.Title.newPacket(action.toNMS(), tag == null ? null : ChatAPI.toICBC(tag.toString()), fadeIn, showTime, fadeOut);
    }

    public enum TitleAction implements WrappedData {
        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET;
        Method m = getMethod(getNMSClass("PacketPlayOutTitle$EnumTitleAction"), "valueOf", String.class);

        public Object toNMS() {
            try {
                return m.invoke(null, name());
            } catch (Throwable e) {
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return null;
        }
    }
}
