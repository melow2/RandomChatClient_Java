package com.hellostranger.client.core;

import android.os.AsyncTask;
import android.os.Looper;
import android.view.View;

import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.view.activity.MainActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hellostranger.client.core.MessageConstants.MESSAGING;
import static com.hellostranger.client.core.MessageConstants.MSG_DELIM;
import static com.hellostranger.client.core.MessageConstants.MSG_REQUIRE_RECONNECT;
import static com.hellostranger.client.core.MessageConstants.RE_CONNECT;
import static com.hellostranger.client.core.MessageConstants.parseMessage;
import static com.hellostranger.client.core.SocketManager.ROOM_NUMBER;
import static com.hellostranger.client.core.SocketManager.exit;
import static com.hellostranger.client.core.SocketManager.selector;
import static com.hellostranger.client.core.SocketManager.socketChannel;

public abstract class ClientAsyncTask {

    private static MainActivity _mContext;
    private static MainActivityBinding _mBinding;
    private static String _mCurrentSex;
    private static WeakHandler _weakHandler;

    public static class ServerConnectTask extends AsyncTask<Void, Void, Boolean> {

        public ServerConnectTask(MainActivity mainActivity, MainActivityBinding binding, String currentSex) {
            _mContext = mainActivity;
            _mBinding = binding;
            _mCurrentSex = currentSex;
            _weakHandler = new WeakHandler(Looper.getMainLooper());
        }

        @Override
        protected Boolean doInBackground(Void... sexes) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            RandomChatClient _mChatClient = new RandomChatClient(_mContext, _mBinding, _mCurrentSex);
            executorService.execute(_mChatClient);
            return true;
        }
    }

    public static class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... msg) {
            if (!msg[0].equals("")) {
                try {
                    ByteBuffer buffer = parseMessage(MESSAGING + MSG_DELIM
                                    + ROOM_NUMBER + MSG_DELIM
                                    + msg[0] + MSG_DELIM
                                    + _mCurrentSex);
                    socketChannel.write(buffer);
                } catch (IOException e) {
                    addView(MSG_REQUIRE_RECONNECT,2);
                }
            }
            return true;
        }
        private void addView(String msg, int i) {
            _weakHandler.post(() -> {
                _mBinding.lytMsgline.addView(new RandomChatLog(_mContext, _mBinding, msg, null, i));
                _mBinding.scvMsgItem.post(() -> _mBinding.scvMsgItem.fullScroll(View.FOCUS_DOWN));
                _mBinding.edtMsg.requestFocus();
            });
        }
    }

    public static class ReConnectTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                socketChannel.write(parseMessage(RE_CONNECT + MSG_DELIM + ROOM_NUMBER + MSG_DELIM + "RE_CONNECT"));
            } catch (IOException e) {
                exit();
                addView(MSG_REQUIRE_RECONNECT,2);
            }
            return true;
        }
        private void addView(String msg, int i) {
            _weakHandler.post(() -> {
                _mBinding.lytMsgline.addView(new RandomChatLog(_mContext, _mBinding, msg, null, i));
                _mBinding.scvMsgItem.post(() -> _mBinding.scvMsgItem.fullScroll(View.FOCUS_DOWN));
                _mBinding.edtMsg.requestFocus();
            });
        }
    }


}
