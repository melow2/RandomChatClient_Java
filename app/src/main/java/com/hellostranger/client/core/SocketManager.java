package com.hellostranger.client.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import static com.hellostranger.client.core.MessageConstants.*;

public abstract class SocketManager extends BaseClient {
    public static Selector selector;
    public static SocketChannel socketChannel;
    public static String ROOM_NUMBER = "";
    private static final String TAG = SocketManager.class.getSimpleName();

    protected static void disconnect(SocketChannel channel, SelectionKey key, SocketAddress addr) {
        try {
            channel.socket().close();
            channel.close();
            key.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exit(){
        try {
            selector.close();
            socketChannel.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
