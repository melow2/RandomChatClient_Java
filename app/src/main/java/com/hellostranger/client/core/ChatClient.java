package com.hellostranger.client.core;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hellostranger.client.R;
import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.view.activity.MainActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;

public class ChatClient extends ChatBaseClient implements Runnable {

    private MainActivity mContext;
    private MainActivityBinding mBinding;
    public DataOutputStream mDos;
    public DataInputStream mDis;
    private WeakHandler handler;
    private Socket mSocket;
    private String mSex;
    public final String MSG_TOKENIZER = "/";

    private static final String TAG = ChatClient.class.getSimpleName();

    public ChatClient(MainActivity context, MainActivityBinding binding,String sex) {
        this.mContext = context;
        this.mBinding = binding;
        this.handler = new WeakHandler(Looper.getMainLooper());
        this.mSex = sex;
    }

    @Override
    public void run() {
        // 서버가 스레드 작업을 처리할때까지 대기.
        try { Thread.sleep(500); } catch (InterruptedException e) { }

        handler.post(() -> {
            mBinding.pgbMain.setVisibility(View.VISIBLE);
        });

        // 접속.
        try {
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(ChatBaseClient.IP, ChatBaseClient.PORT), ChatBaseClient.TIMEOUT);
        } catch (Exception timeoutException) {
            new Handler(Looper.getMainLooper()).post(() -> {
                showToast(mContext, mContext.getString(R.string.msg_connection_error));
            });
            return ;
        } finally {
            handler.post(() -> {
                mBinding.pgbMain.setVisibility(View.GONE);
            });
        }

        // 서버 연결 확인.
        try {
            mDos = new DataOutputStream(mSocket.getOutputStream());
            mDis = new DataInputStream(mSocket.getInputStream());
            String serverStateMent = mDis.readUTF();
            if (serverStateMent.equals(ChatCommand.SERVER_IS_BUSY)) {
                handler.post(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(null);
                    builder.setMessage(mContext.getString(R.string.msg_server_is_busy));
                    builder.setPositiveButton("확인", (dialog, which) -> {
                    });
                    builder.setCancelable(false);
                    builder.create();
                    builder.show();
                });
            } else if (serverStateMent.equals(ChatCommand.SERVER_IS_STABLE)) {
                handler.post(()->{ showToast(mContext,mContext.getString(R.string.msg_connection)); });
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 서버 키 체크.
        try { mDos.writeUTF(ChatCommand.SERVER_KEY); } catch (Exception e) { }

        // client 정보 보내기.
        try { mDos.writeUTF(mSex);} catch (Exception e){};

        // 연결 시작.
        while (true) {
            try {
                String receivedMsg = mDis.readUTF();
                StringTokenizer st = new StringTokenizer(receivedMsg,MSG_TOKENIZER);
                String message = st.nextToken();
                String client_info = st.nextToken();
                if (message.equals(ChatCommand.ROOM_END)) {
                    addView(message,null, 4);
                    break;
                } else if (message.charAt(0) == '[') {
                    addView(message,null, 2);
                } else {
                    addView(message,client_info,1);
                }
            } catch (Exception e) {
                // 연결종료.
            }
        }
        disconnect();
    }

    public void disconnect() {
        try {
            if (mDis != null) this.mDis.close();
            if (mDos != null) this.mDos.close();
            if (mSocket != null) this.mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addView(String msg, String client_info, int i) {
        handler.post(() -> {
            mBinding.lytMsgline.addView(new ChatLog(mContext, mBinding, msg,client_info,i));
            mBinding.scvMsgItem.post(() -> mBinding.scvMsgItem.fullScroll(View.FOCUS_DOWN));
            mBinding.edtMsg.post(() -> mBinding.edtMsg.requestFocus());
        });
    }

}
