package com.example.glowskincareapp;

import android.os.StrictMode;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommonUtility {
    public static boolean isInternetAvailable() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }
}
