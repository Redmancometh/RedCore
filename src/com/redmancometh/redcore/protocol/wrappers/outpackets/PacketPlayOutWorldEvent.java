package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutWorldEvent extends WrappedPacket {
    public int data;
    public boolean disableRelVolume;
    public int effectId;
    public BlockLocation loc;

    public PacketPlayOutWorldEvent() {

    }

    public PacketPlayOutWorldEvent(int effectId, BlockLocation loc, int data, boolean disableRelVolume) {
        this.effectId = effectId;
        this.loc = loc;
        this.data = data;
        this.disableRelVolume = disableRelVolume;
    }


    @Override
    public Object getVanillaPacket() {
        return PacketOutType.WorldEvent.newPacket(effectId, loc.toNMS(), data, disableRelVolume);
    }

    @Override
    public void loadVanillaPacket(Object obj) {
        Object[] d = PacketOutType.WorldEvent.getPacketData(obj);
        effectId = (int) d[0];
        loc = new BlockLocation(d[1]);
        data = (int) d[2];
        disableRelVolume = (boolean) d[3];
    }
}
