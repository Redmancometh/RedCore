package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.ItemStackWrapper;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by GyuriX on 2016.02.28..
 */
public class PacketPlayOutEntityEquipment extends WrappedPacket {
    public int entityId;
    public ItemStackWrapper item;
    /**
     * Slot number: 0 - held 1 - boots 2 - leggings 3 - chestplate 4 - helmet
     */
    public int slot;

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.EntityEquipment.newPacket(entityId, slot, item.toNMS());
    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.EntityEquipment.getPacketData(packet);
        entityId = (int) d[0];
        slot = (int) d[1];
        item = new ItemStackWrapper(d[2]);
    }
}
