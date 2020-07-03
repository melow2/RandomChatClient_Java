package com.hellostranger.client.core;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

public abstract class ChatBaseClient {

    //203.245.44.15
    public static final String IP = "203.245.44.15";
    public static final int PORT = 49000;
    public static final int TIMEOUT = 7000;

    public static void showToast(@NonNull Context context, @NonNull String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
