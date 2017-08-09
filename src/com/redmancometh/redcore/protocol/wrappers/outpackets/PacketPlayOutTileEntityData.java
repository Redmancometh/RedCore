package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.nbt.NBTCompound;
import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;

public class PacketPlayOutTileEntityData extends WrappedPacket {
    public int action;
    public BlockLocation block;
    public NBTCompound nbt;

    public PacketPlayOutTileEntityData()
    {
    }

    public PacketPlayOutTileEntityData(BlockLocation block, int action, NBTCompound nbt)
    {
        this.block = block;
        this.action = action;
        this.nbt = nbt;
    }

    @Override
    public void loadVanillaPacket(Object obj)
    {
        Object[] data = PacketOutType.TileEntityData.getPacketData(obj);
        block = new BlockLocation(data[0]);
        action = (int) data[1];
        nbt = new NBTCompound(data[2]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.TileEntityData.newPacket(block.toNMS(), action, nbt.toNMS());
    }
}
