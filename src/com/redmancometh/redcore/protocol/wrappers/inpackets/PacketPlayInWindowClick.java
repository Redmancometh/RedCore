package com.redmancometh.redcore.protocol.wrappers.inpackets;

import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.event.PacketInType;
import com.redmancometh.redcore.protocol.utils.InventoryClickType;
import com.redmancometh.redcore.protocol.utils.ItemStackWrapper;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.ServerVersion;

public class PacketPlayInWindowClick extends WrappedPacket
{
    public short actionNumber;
    public int button;
    public InventoryClickType clickType;
    public ItemStackWrapper item;
    public int slot;
    public int windowId;

    public PacketPlayInWindowClick()
    {

    }

    public PacketPlayInWindowClick(int windowId, int slot, int button, short actionNumber, ItemStackWrapper item, InventoryClickType clickType)
    {
        this.windowId = windowId;
        this.slot = slot;
        this.button = button;
        this.actionNumber = actionNumber;
        this.item = item;
        this.clickType = clickType;
    }

    @Override
    public void loadVanillaPacket(Object packet)
    {
        Object[] o = PacketInType.WindowClick.getPacketData(packet);
        windowId = (Integer) o[0];
        slot = (Integer) o[1];
        button = (Integer) o[2];
        actionNumber = (Short) o[3];
        item = new ItemStackWrapper(o[4]);
        clickType = Reflection.ver.isAbove(ServerVersion.v1_9) ? InventoryClickType.valueOf(o[5].toString()) : InventoryClickType.values()[(int) o[5]];
    }

    @Override
    public Object getVanillaPacket()
    {
        return PacketInType.WindowClick.newPacket(windowId, slot, button, actionNumber, item == null ? null : item.toNMS(), clickType.toNMS());
    }
}

