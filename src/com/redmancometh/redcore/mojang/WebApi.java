package com.redmancometh.redcore.mojang;

import com.redmancometh.redcore.spigotutils.SU;
import org.apache.commons.io.IOUtils;

import java.net.*;
import java.nio.charset.Charset;

/**
 * Created by GyuriX on 2016. 06. 10..
 */
public class WebApi {
    public static String get(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            return IOUtils.toString(con.getInputStream(), Charset.forName("UTF-8"));
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            return null;
        }
    }

    public static String post(String urlString, String req) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(req.getBytes(Charset.forName("UTF-8")));
            return IOUtils.toString(con.getInputStream(), Charset.forName("UTF-8"));
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            return null;
        }
    }

    public static String postWithHeader(String urlString, String headerKey, String headerValue, String req) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Length", String.valueOf(req.length()));
            con.setRequestProperty(headerKey, headerValue);
            con.getOutputStream().write(req.getBytes(Charset.forName("UTF-8")));
            return IOUtils.toString(con.getInputStream(), Charset.forName("UTF-8"));
        } catch (Throwable e) {
            SU.error(SU.cs, e, "RedCore", "com.redmancometh");
            return null;
        }
    }
}
