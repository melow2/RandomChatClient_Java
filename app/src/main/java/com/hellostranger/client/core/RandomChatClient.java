package com.hellostranger.client.core;

import android.os.Looper;
import android.view.View;

import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.util.AES256Util;
import com.hellostranger.client.view.activity.MainActivity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.StringTokenizer;

import static com.hellostranger.client.core.MessageConstants.*;

public class RandomChatClient extends SocketManager implements Runnable {

    private WeakHandler mHandler;
    private MainActivity mContext;
    private MainActivityBinding mBinding;
    private String mSex = "";

    public RandomChatClient(MainActivity mainActivity, MainActivityBinding binding, String sex) {
        this.mContext = mainActivity;
        this.mBinding = binding;
        this.mSex = sex;
        mHandler = new WeakHandler(Looper.getMainLooper());
        try {
            connectAddress = new InetSocketAddress(AES256Util.decryption(IP),Integer.parseInt(AES256Util.decryption(PORT)));
            selector = Selector.open();
            socketChannel = SocketChannel.open(connectAddress);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, new StringBuffer());
            socketChannel.write(encoder.encode(CharBuffer.wrap(REQUIRE_ACCESS + MSG_DELIM + sex)));
        } catch (Exception e) {
            addView(MSG_CONNECT_FAIL, null, 2);
        }
    }

    @Override
    public void run() {
        try {
            if(selector!=null) {
                while (selector.select() > 0) {
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isReadable()) {
                            receive(key);
                        }
                    }
                }
            }else{
                addView(MSG_CONNECT_FAIL,null,2);
            }
        } catch (IOException e) {

        }
    }

    // 수신시 호출 함수.
    protected void receive(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        try {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 100);
            readBuffer.clear();
            channel.configureBlocking(false); // 채널은 블록킹 상태이기 때문에 논블럭킹 설정.
            int size = channel.read(readBuffer);
            readBuffer.flip();
            if (size == -1) {
                disconnect(channel, key, remoteAddr);
                return;
            }
            byte[] data = new byte[size];
            System.arraycopy(readBuffer.array(), 0, data, 0, size);
            String received = new String(data, "UTF-8");
            messageProcessing(channel,key,received);
        } catch (IOException e) {
            disconnect(channel, key, remoteAddr);
            addView(MSG_REQUIRE_RECONNECT,null,2);
        }
    }

    protected void messageProcessing(SocketChannel channel, SelectionKey key, String received) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(received, MSG_DELIM);
            String protocol = tokenizer.nextToken();
            ROOM_NUMBER = tokenizer.nextToken();
            String message = "";
            switch (protocol) {
                case CONNECTION: // 접속 시
                case NEW_CLIENT: // 상대방을 만났을 경우
                case QUIT_CLIENT: // 나갔을 경우
                case RE_CONNECT:
                    message = tokenizer.nextToken();
                    addView(message, null, 2);
                    break;
                case MESSAGING:
                    String msg = tokenizer.nextToken();
                    String clientInfo = tokenizer.nextToken();
                    mHandler.post(() -> {
                        addView(msg, clientInfo, 1);
                    });
                    while (tokenizer.hasMoreTokens()) {
                        String rProtocol = tokenizer.nextToken();
                        String rRoomNumber = tokenizer.nextToken();
                        String rMsg = tokenizer.nextToken();
                        String rClientInfo = tokenizer.nextToken();
                        mHandler.post(() -> {
                            addView(rMsg, rClientInfo, 1);
                        });
                    }
                    break;
            }
        }catch (Exception e){
            disconnect(channel,key,channel.socket().getLocalSocketAddress());
            addView(MSG_REQUIRE_RECONNECT,null,2);
        }
    }

    private void addView(String msg, String client_info, int i) {
        mHandler.post(() -> {
            mBinding.lytMsgline.addView(new RandomChatLog(mContext, mBinding, msg, client_info, i));
            mBinding.scvMsgItem.post(() -> mBinding.scvMsgItem.fullScroll(View.FOCUS_DOWN));
            mBinding.edtMsg.requestFocus();
        });
    }

}
