package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

/**
 * Created by com.redmancometh on 25/11/2015.
 */
public class PacketPlayOutNamedSoundEffect extends WrappedPacket {
    public float pitch;
    public String soundName;
    public float volume;
    public double x;
    public double y;
    public double z;

    public PacketPlayOutNamedSoundEffect() {

    }

    public PacketPlayOutNamedSoundEffect(String soundName, double x, double y, double z, float volume, float pitch) {
        this.soundName = soundName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void loadVanillaPacket(Object obj) {
        Object[] data = PacketOutType.NamedSoundEffect.getPacketData(obj);
        soundName = (String) data[0];
        x = ((int) data[1]) / 8.0;
        y = ((int) data[2]) / 8.0;
        z = ((int) data[3]) / 8.0;
        volume = (float) data[4];
        pitch = ((int) data[5]) / 64.0f;
    }

    @Override
    public Object getVanillaPacket() {
        return PacketOutType.NamedSoundEffect.newPacket(soundName, (int) (x * 8), (int) (y * 8), (int) (z * 8), volume, (int) (pitch * 64));
    }
}
