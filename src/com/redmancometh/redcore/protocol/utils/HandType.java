package com.redmancometh.redcore.protocol.utils;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.Method;

/**
 * Created by GyuriX on 2016.03.26..
 */
public enum HandType implements WrappedData {
    MAIN_HAND,
    OFF_HAND;
    Method valueOf = Reflection.getMethod(Reflection.getNMSClass("EnumHand"), "valueOf", String.class);

    @Override
    public Object toNMS() {
        try {
            return valueOf.invoke(null, name());
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
        }
        return null;
    }
}
