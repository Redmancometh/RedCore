package com.redmancometh.redcore.protocol.wrappers.outpackets;

import com.redmancometh.redcore.protocol.event.PacketOutType;
import com.redmancometh.redcore.protocol.utils.BlockLocation;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.BlockUtils;

/**
 * Created by GyuriX, on 2017. 02. 05..
 */
public class PacketPlayOutBlockAction extends WrappedPacket {
    public int actionId, actionData, blockId;
    public BlockLocation loc;

    public PacketPlayOutBlockAction()
    {
    }

    public PacketPlayOutBlockAction(BlockLocation loc, int actionId, int actionData, int blockId)
    {
        this.loc = loc;
        this.actionId = actionId;
        this.actionData = actionData;
        this.blockId = blockId;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] d = PacketOutType.BlockAction.getPacketData(packet);
        loc = new BlockLocation(d[0]);
        actionId = (int) d[1];
        actionData = (int) d[2];
        blockId = BlockUtils.getNMSBlockTypeId(d[3]);
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketOutType.BlockAction.newPacket(loc.toNMS(), actionId, actionData, BlockUtils.getNMSBlockType(blockId));
    }
}
