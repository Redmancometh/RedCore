package com.redmancometh.redcore.api;

import com.redmancometh.redcore.spigotutils.SU;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class VariableAPI {
    public static final ArrayList<Object> emptyList = new ArrayList<>();
    public static final HashMap<String, VariableHandler> handlers = new HashMap();
    private static final HashSet<String> errorVars = new HashSet<>();
    private static final HashSet<String> missingHandlers = new HashSet<>();

    public static ArrayList<Object> fill(String msg, int from, Player plr, Object[] oArgs) {
        int l = msg.length();
        int sid = from;
        ArrayList<Object> out = new ArrayList<>();
        for (int i = from; i < l; ++i) {
            char c = msg.charAt(i);
            if (c == '<') {
                if (sid < i) {
                    out.add(msg.substring(sid, i));
                }
                ArrayList<Object> d = fill(msg, i + 1, plr, oArgs);
                i = (Integer) d.get(0);
                sid = i + 1;
                d.remove(0);
                out.add(fillVar(plr, d, oArgs));
                continue;
            }
            if (c != '>') continue;
            if (sid < i) {
                out.add(msg.substring(sid, i));
            }
            out.add(0, i);
            return out;
        }
        if (sid < msg.length()) {
            out.add(msg.substring(sid, msg.length()));
        }
        out.add(0, msg.length() - 1);
        return out;
    }

    public static Object fillVar(Player plr, List<Object> inside, Object[] oArgs) {
        StringBuilder sb = new StringBuilder();
        int l = inside.size();
        for (int c = 0; c < l; ++c) {
            String os = String.valueOf(inside.get(c));
            int id = os.indexOf(58);
            if (id != -1) {
                sb.append(os.substring(0, id));
                ArrayList<Object> list = new ArrayList<>(inside.subList(c + 1, l));
                if (id != os.length() - 1) {
                    list.add(0, os.substring(id + 1));
                }
                return handle(sb.toString(), plr, list, oArgs);
            }
            sb.append(os);
        }
        return handle(sb.toString(), plr, emptyList, oArgs);
    }

    public static String fillVariables(String msg, Player plr, Object... oArgs) {
        ArrayList<Object> out = fill(msg.replace("\\<", "\u0000").replace("\\>", "\u0001"), 0, plr, oArgs);
        out.remove(0);
        String s = StringUtils.join(out, "").replace('\u0000', '<').replace('\u0001', '>');
        return s;
    }

    private static Object handle(String var, Player plr, ArrayList<Object> inside, Object[] oArgs) {
        VariableHandler vh = handlers.get(var);
        if (vh == null) {
            if (missingHandlers.add(var))
                SU.log(SU.pl(), "§cMissing handler for variable §f" + var + "§c!");
            return "<" + var + ">";
        }
        try {
            return vh.getValue(plr, inside, oArgs);
        } catch (Throwable e) {
            if (errorVars.add(var)) {
                SU.log(SU.pl(), "§cError on calculating variable §f" + var + "§c!");
                SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            }
            return '<' + var + '>';
        }
    }

    public interface VariableHandler {
        Object getValue(Player plr, ArrayList<Object> args, Object[] eArgs);
    }

}

