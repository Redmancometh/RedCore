package com.redmancometh.redcore.protocol.event;

import com.google.common.collect.Lists;
import com.redmancometh.redcore.protocol.Reflection;
import com.redmancometh.redcore.protocol.wrappers.WrappedPacket;
import com.redmancometh.redcore.spigotutils.SU;

import java.lang.reflect.*;
import java.util.*;

public enum PacketOutType {
    Abilities,
    Advancements,
    Animation,
    AttachEntity,
    Bed,
    Boss,
    BlockAction,
    BlockBreakAnimation,
    BlockChange,
    Camera,
    Chat,
    CloseWindow,
    Collect,
    CombatEvent,
    CustomPayload,
    CustomSoundEffect,
    Entity,
    EntityDestroy,
    EntityEffect,
    EntityEquipment,
    EntityHeadRotation,
    EntityLook,
    EntityMetadata,
    EntityStatus,
    EntityTeleport,
    EntityVelocity,
    Experience,
    Explosion,
    GameStateChange,
    HeldItemSlot,
    KeepAlive,
    KickDisconnect,
    Login,
    LoginOutDisconnect,
    LoginOutEncryptionBegin,
    LoginOutSetCompression,
    LoginOutSuccess,
    Map,
    MapChunk,
    MapChunkBulk,
    Mount,
    MultiBlockChange,
    NamedEntitySpawn,
    NamedSoundEffect,
    OpenSignEditor,
    OpenWindow,
    PlayerInfo,
    PlayerListHeaderFooter,
    Position,
    RelEntityMove,
    RelEntityMoveLook,
    Recipes,
    RemoveEntityEffect,
    ResourcePackSend,
    Respawn,
    ScoreboardDisplayObjective,
    ScoreboardObjective,
    ScoreboardScore,
    ScoreboardTeam,
    SelectAdvancementTab,
    ServerDifficulty,
    SetCompression,
    SetCooldown,
    SetSlot,
    SpawnEntity,
    SpawnEntityExperienceOrb,
    SpawnEntityLiving,
    SpawnEntityPainting,
    SpawnEntityWeather,
    SpawnPosition,
    Statistic,
    TabComplete,
    TileEntityData,
    Title,
    Transaction,
    UnloadChunk,
    UpdateAttributes,
    UpdateEntityNBT,
    UpdateHealth,
    UpdateSign,
    UpdateTime,
    VehicleMove,
    WindowData,
    WindowItems,
    WorldBorder,
    WorldEvent,
    WorldParticles,
    StatusOutPong,
    StatusOutServerInfo;

    private static final HashMap<Class, PacketOutType> packets = new HashMap<>();
    public Class<? extends WrappedPacket> wrapper;
    ArrayList<Field> fs;
    private Constructor emptyConst;
    private boolean supported;

    /**
     * Get the type of an outgoing packet
     *
     * @param packet - The outgoing packet
     * @return The type of the given packet
     */
    public static PacketOutType getType(Object packet) {
        Class cl = packet.getClass();
        while (cl != null && cl != Object.class) {
            String cn = cl.getName();
            while (cn.contains("$")) {
                try {
                    cl = Class.forName(cn.substring(0, cn.indexOf("$")));
                    cn = cl.getName();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            PacketOutType type = packets.get(cl);
            if (type != null)
                return type;
            cl = cl.getSuperclass();
        }
        return null;
    }

    /**
     * Initializes the PacketOutType, DO NOT USE THIS METHOD
     */
    public static void init() {
        for (PacketOutType t : PacketOutType.values()) {
            String name = t.name();
            String cln = "Packet" + (name.startsWith("LoginOut") || name.startsWith("Status") ? name : "PlayOut" + name);
            try {
                Class cl = Reflection.getNMSClass(cln);
                if (cl == null)
                    continue;
                packets.put(cl, t);
                t.emptyConst = cl.getConstructor();
                t.fs = new ArrayList();
                for (Field f : cl.getDeclaredFields()) {
                    if ((f.getModifiers() & 8) != 0) continue;
                    f.setAccessible(true);
                    t.fs.add(f);
                }
                t.supported = true;
            } catch (Throwable ignored) {
            }
            try {
                t.wrapper = (Class<? extends WrappedPacket>) Class.forName("com.redmancometh.redcore.protocol.wrappers.outpackets." + cln);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Returns the packet data of a packet
     *
     * @param packet - The packet
     * @return The contents of all the non static fields of the packet
     */
    public Object[] getPacketData(Object packet) {
        Object[] out = new Object[fs.size()];
        try {
            for (int i = 0; i < fs.size(); ++i) {
                out[i] = fs.get(i).get(packet);
            }
            return out;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tells if this packet is supported or not by the current server version.
     *
     * @return True if it is supported, false otherwise
     */
    public boolean isSupported() {
        return supported;
    }

    /**
     * Creates a new packet of this type and fills its fields with the given data
     *
     * @param data - Data to fill packet fields with
     * @return The crafted packet
     */
    public Object newPacket(Object... data) {
        try {
            Object out = emptyConst.newInstance();
            fillPacket(out, data);
            return out;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fills the given packet with the given data
     *
     * @param packet - The fillable packet
     * @param data   - The filling data
     */
    public void fillPacket(Object packet, Object... data) {
        ArrayList<Field> fields = Lists.newArrayList(fs);
        for (Object d : data) {
            for (int f = 0; f < fields.size(); f++) {
                try {
                    Field ff = fields.get(f);
                    ff.set(packet, d);
                    fields.remove(f--);
                    break;
                } catch (Throwable e) {
                    SU.error(SU.cs, e, "RedCore", "com.redmancometh");
                }
            }
        }
    }

    /**
     * Get the wrapper of the given NMS packet.
     *
     * @param nmsPacket - The NMS packet
     * @return The wrapper of the given NMS packet
     */
    public WrappedPacket wrap(Object nmsPacket) {
        try {
            WrappedPacket wp = wrapper.newInstance();
            wp.loadVanillaPacket(nmsPacket);
            return wp;
        } catch (Throwable e) {
            SU.log(SU.pl(), "§4[§cPacketAPI§4] §eError on wrapping §c" + name() + "§e out packet.");
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            return null;
        }
    }
}

