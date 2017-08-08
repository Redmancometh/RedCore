package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.08.23..
 */
public class PacketPlayOutHeldItemSlot extends WrappedPacket {
    public int slot;

    public PacketPlayOutHeldItemSlot() {

    }

    public PacketPlayOutHeldItemSlot(int slot) {
        this.slot = slot;
    }

    public PacketPlayOutHeldItemSlot(Object packet) {
        loadVanillaPacket(packet);
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        slot = (int) PacketOutType.HeldItemSlot.getPacketData(packet)[0];
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.HeldItemSlot.newPacket(slot);
    }
}
