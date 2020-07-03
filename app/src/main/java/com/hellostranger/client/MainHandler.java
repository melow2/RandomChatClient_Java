package com.hellostranger.client;

import android.content.Context;
import android.view.View;

import com.hellostranger.client.databinding.MainActivityBinding;

public class MainHandler {

    private Context mContext;
    private MainActivityBinding mMainActivityBinding;
    private MainHandlerEvent mListener;

    public interface MainHandlerEvent{
        void onClickSendBtn(String msg);
        void onClickBtnReload(String msg);
    }

    public void addEventListener(MainHandlerEvent listener){
        this.mListener = listener;
    }


    public MainHandler(Context context, MainActivityBinding binding){
        this.mContext = context;
        mMainActivityBinding = binding;
    }

    public void onClickSendBtn(View view){
        mListener.onClickSendBtn(mMainActivityBinding.edtMsg.getText().toString());
    }

    public void onClickBtnReload(View view){
        mListener.onClickBtnReload(mContext.getString(R.string.msg_reload));
    }
}
