package com.redmancometh.redcore.spigotutils;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Utils for managing time in your plugins
 */
public class TimeUtils {
    /**
     * Returns a language remaining time until the given time
     *
     * @param plr  - Target player
     * @param time - Expire time
     * @return The language based time string
     */
    public static String getExpire(Player plr, Long time)
    {
        if (time == null || time <= 0 || time == Long.MAX_VALUE) {
            return "never";
        }
        return getTime(plr, time - System.currentTimeMillis());
    }

    /**
     * Returns the language based time message of the given time.
     *
     * @param plr  - Target player
     * @param time - The formatable time in milliseconds
     * @return The language based time string
     */
    public static String getTime(Player plr, Long time)
    {
        time /= 1000;
        if (time == null || time >= Long.MAX_VALUE / 1000) {
            return "never";
        }
        if (time < 0)
            time = 0L;
        int w = (int) (time / 604800);
        int d = (int) (time % 604800 / 86400);
        int h = (int) (time % 86400 / 3600);
        int m = (int) (time % 3600 / 60);
        int s = (int) (time % 60);
        StringBuilder sb = new StringBuilder();
        String sep = ", ";
        if (w > 0)
            sb.append(w > 1 ? "weeks" : "week").append(sep);
        if (d > 0)
            sb.append(d > 1 ? "days" : "day").append(sep);
        if (h > 0)
            sb.append(h > 1 ? "hours" : "hour").append(sep);
        if (m > 0)
            sb.append(m > 1 ? "minutes" : "minute").append(sep);
        if (sb.length() == 0 || s > 0)
            sb.append(s > 1 ? "seconds" : "second").append(sep);
        return sb.substring(0, sb.length() - sep.length());
    }

    /**
     * Converts user entered time to milliseconds
     *
     * @param plr - Target player
     * @param in  - The input string
     * @return The entered time in long
     */
    public static long toTime(Player plr, String in)
    {
        in = in.replace(" ", "").replace(",", "");
        long out = 0;
        long cur = 0;
        HashMap<String, Long> multipliers = new HashMap<>();
        for (String s : new String[]{"w", "week", "weeks"})
            multipliers.put(s, 604800L);
        for (String s : new String[]{"d", "day", "days"})
            multipliers.put(s, 86400L);
        for (String s : new String[]{"h", "hour", "hours"})
            multipliers.put(s, 3600L);
        for (String s : new String[]{"m", "min", "minute", "minutes"})
            multipliers.put(s, 60L);
        for (String s : new String[]{"s", "sec", "second", "seconds"})
            multipliers.put(s, 1L);
        StringBuilder curP = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (c > 47 && c < 58) {
                if (curP.length() > 0) {
                    out += cur * NullUtils.to0(multipliers.get(curP.toString()));
                    curP.setLength(0);
                    cur = 0;
                }
                cur = cur * 10 + (c - 48);
            } else
                curP.append(c);
        }
        if (curP.length() > 0) {
            out += cur * NullUtils.to0(multipliers.get(curP.toString()));
            cur = 0;
        }
        return (out + cur) * 1000L;
    }
}
