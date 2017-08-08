package com.redmancometh.redcore.protocol.wrappers;

import com.redmancometh.redcore.protocol.utils.WrappedData;

/**
 * Represents a wrapped (user friendly) form of a Vanilla/NMS packet.
 */
public abstract class WrappedPacket implements WrappedData {
    /**
     * Loads a Vanilla/NMS packet to this wrapper
     *
     * @param packet - The loadable packet
     */
    public abstract void loadVanillaPacket(Object packet);

    @Override
    public Object toNMS() {
        return getVanillaPacket();
    }

    /**
     * Converts this wrapped packet to a Vanilla/NMS packet
     *
     * @return The conversion result, NMS packet
     */
    public abstract Object getVanillaPacket();
}

