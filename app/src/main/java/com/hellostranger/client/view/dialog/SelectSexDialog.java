package com.hellostranger.client.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.hellostranger.client.databinding.DialogCloseBinding;
import com.hellostranger.client.databinding.DialogSelectSexBinding;

import java.io.IOException;

public class SelectSexDialog extends Dialog {

    public static final String TAG = SelectSexDialog.class.getSimpleName();

    public SelectSexDialog.Event listener;

    public interface Event{
        void onClickMale() throws IOException;
        void onClickFemale() throws IOException;
    }

    public void addButtonListener(SelectSexDialog.Event listener){
        this.listener = listener;
    }

    public SelectSexDialog(@NonNull Context context) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogSelectSexBinding mBinding = DialogSelectSexBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        mBinding.ivFemale.setOnClickListener(v->{
            try {
                listener.onClickFemale();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mBinding.ivMale.setOnClickListener(v->{
            try {
                listener.onClickMale();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
