package com.redmancometh.redstats;

import com.redmancometh.redcore.Defaultable;
import com.redmancometh.redstats.stats.StatType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity(name = "rm_stat")
public class PlayerStatRecord implements Serializable, Defaultable<UUID> {
    private static final long serialVersionUID = 3822617009102233543L;
    @Column(name = "owner", unique = true)
    @Id
    @Type(type = "uuid-char")
    private UUID owner;

    @MapKeyClass(value = StatType.class)
    @ElementCollection(targetClass = Long.class)
    private Map<StatType, Long> statMap = new HashMap();

    public PlayerStatRecord()
    {

    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public long getStat(StatType type)
    {
        return statMap.get(type);
    }

    public Map<StatType, Long> getStatMap()
    {
        //Column is definitely the value
        return statMap;
    }

    public void setStatMap(Map<StatType, Long> statMap)
    {
        this.statMap = statMap;
    }

    @Override
    public void setDefaults(UUID key)
    {
        this.setOwner(key);
        Map<StatType, Long> statMap = new HashMap();
        StatType.forEach((type) -> statMap.put(type, 1L));
        this.statMap = statMap;
    }

    public long setStat(StatType type, long l)
    {
        return statMap.put(type, l);
    }

}
