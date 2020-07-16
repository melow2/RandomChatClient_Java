package com.hellostranger.client.core;

import android.content.Context;
import android.os.AsyncTask;

import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.view.activity.MainActivity;

import java.io.IOException;
import java.util.concurrent.Executors;

import static com.hellostranger.client.core.MessageConstants.MESSAGING;
import static com.hellostranger.client.core.MessageConstants.MSG_DELIM;
import static com.hellostranger.client.core.MessageConstants.RE_CONNECT;
import static com.hellostranger.client.core.MessageConstants.parseMessage;
import static com.hellostranger.client.core.SocketManager.ROOM_NUMBER;
import static com.hellostranger.client.core.SocketManager.socketChannel;

public abstract class ClientAsyncTask {

    public static class ServerConnectTask extends AsyncTask<Void, Void, Boolean> {
        private RandomChatClient mChatClient;
        private MainActivity mContext;
        private MainActivityBinding mBinding;
        private String mCurrentSex;

        public ServerConnectTask(MainActivity mainActivity, MainActivityBinding binding, String currentSex) {
            this.mContext = mainActivity;
            this.mBinding = binding;
            this.mCurrentSex = currentSex;
        }

        @Override
        protected Boolean doInBackground(Void... sexes) {
            mChatClient = new RandomChatClient(mContext, mBinding, mCurrentSex);
            Executors.newSingleThreadExecutor().execute(mChatClient);
            return true;
        }
    }

    public static class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        private String mCurrentSex = "";

        public SendMessageTask(String currentSex) {
            mCurrentSex = currentSex;
        }

        @Override
        protected Boolean doInBackground(String... msg) {
            if (!msg[0].equals("")) {
                try {
                    socketChannel.write(parseMessage(
                            MESSAGING + MSG_DELIM
                                    + ROOM_NUMBER + MSG_DELIM
                                    + msg[0] + MSG_DELIM
                                    + mCurrentSex));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }

    public static class ReConnectTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                socketChannel.write(parseMessage(RE_CONNECT + MSG_DELIM
                        + ROOM_NUMBER + MSG_DELIM + "RE_CONNECT"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
