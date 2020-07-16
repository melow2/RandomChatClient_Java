package com.hellostranger.client.core;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.net.InetSocketAddress;

public abstract class BaseClient {
    //203.245.44.15
    public static final String IP = "203.245.44.15";
    public static final int PORT = 45558;
    public static final int TIMEOUT = 3000;
    public static InetSocketAddress connectAddress;
}
