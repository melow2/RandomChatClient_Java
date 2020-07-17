package com.hellostranger.client.core;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.hellostranger.client.R;
import com.hellostranger.client.databinding.ChatlogCommandBinding;
import com.hellostranger.client.databinding.ChatlogLeftBinding;
import com.hellostranger.client.databinding.ChatlogRightBinding;
import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.view.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RandomChatLog extends LinearLayout {

    private Context mContext;
    private String mReceivedMsg;
    private int mDirection;
    private String mClientInfo;
    private MainActivityBinding mBinding;
    private ChatlogRightBinding mBindingRight;
    private ChatlogLeftBinding mBindingLeft;
    private ChatlogCommandBinding mBindingCommand;

    private static final String TAG = RandomChatLog.class.getSimpleName();

    public static String CURRENT_LOG = "ME";
    public static String STATE_STRANGER = "STRANGER";
    public static String STATE_ME = "ME";
    public final String MAIL = "M";
    public final String FEMAIL = "F";

    public RandomChatLog(Context context, MainActivityBinding binding, String receivedMsg, String client_info, int i) {
        super(context);
        this.mContext = context;
        this.mBinding = binding;
        this.mReceivedMsg = receivedMsg;
        this.mDirection = i;
        this.mClientInfo = client_info;
        writeLog();
    }

    private void writeLog() {
        switch (mDirection) {
            case 1: // 상대방이 메시지를 보냈을 경우.
                mBindingLeft = DataBindingUtil.inflate(((MainActivity) mContext).getLayoutInflater(), R.layout.chatlog_left, this, true);
                if (!CURRENT_LOG.equals(STATE_STRANGER)) {
                    mBindingLeft.ivProfile.setVisibility(View.VISIBLE);
                    mBindingLeft.tvName.setVisibility(View.VISIBLE);
                    if (mClientInfo.equals(FEMAIL)) {
                        mBindingLeft.tvName.setText(mContext.getString(R.string.female));
                        mBindingLeft.tvMsg.setBackgroundResource(R.drawable.background_chatlog_left_female);
                        mBindingLeft.ivProfile.setImageResource(R.drawable.icons_female_profile_512);
                    } else {
                        mBindingLeft.tvName.setText(mContext.getString(R.string.male));
                    }
                    CURRENT_LOG = STATE_STRANGER;
                } else {
                    if (mClientInfo.equals(FEMAIL)) {
                        mBindingLeft.tvMsg.setBackgroundResource(R.drawable.background_chatlog_left_female);
                        ;
                    }
                    mBindingLeft.ivProfile.setVisibility(View.INVISIBLE);
                    mBindingLeft.tvName.setVisibility(View.GONE);
                }
                mBindingLeft.tvMsg.setText(mReceivedMsg);
                mBindingLeft.tvTime.setText(getCurrentTime());
                break;
            case 2: // 상대방이 입장대기 및 입장.
                mBindingCommand = DataBindingUtil.inflate(((MainActivity) mContext).getLayoutInflater(), R.layout.chatlog_command, this, true);
                mBindingCommand.tvCommand.setText(mReceivedMsg);
                CURRENT_LOG = STATE_ME;
                break;
            case 3: // 내가 메시지를 보냈을 경우.
                mBindingRight = DataBindingUtil.inflate(((MainActivity) mContext).getLayoutInflater(), R.layout.chatlog_right, this, true);
                mBindingRight.tvMsg.setText(mReceivedMsg);
                mBindingRight.tvTime.setText(getCurrentTime());
                CURRENT_LOG = STATE_ME;
                break;
        }
    }

    private String getCurrentTime() {
        int hour = Integer.parseInt(new SimpleDateFormat("kk").format(new Date()));
        int minute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
        String curTime = "";
        if (12 < hour) {
            hour -= 12;
            curTime += "오후 " + hour;
        } else {
            curTime += "오전 " + hour;
        }
        if (minute < 10) curTime += ":0" + minute;
        else curTime += ":" + minute;
        return curTime;
    }

}
