package com.hellostranger.client.view.activity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hellostranger.client.R;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseActivity<Z extends ViewDataBinding> extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public Z mBinding;
    public Toolbar toolbar;

    protected final void bindView(int layout){
        mBinding = DataBindingUtil.setContentView(this,layout);
    }

    protected static void startActivityAnimation(@NonNull Context context){
        if(context instanceof AppCompatActivity){
            ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public static void printLog(@NonNull String tag, @NonNull String msg){
        Log.d(tag,msg);
    }

    public static void showSnackBar(@NonNull View v, @StringRes int stringResID){
        Snackbar.make(v,stringResID,Snackbar.LENGTH_LONG).show();
    }

    public static void showToast(@NonNull Context context, @NonNull String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    protected final void setToolbar(@NonNull Toolbar toolbar,
                                    boolean backBtnVisible,
                                    @NonNull String toolbarTitle,
                                    @NonNull TextView tvToolbarTitle){
        this.toolbar = toolbar;
        toolbar.setTitle("");                                                   // 기존의 툴바 타이틀 제거.
        toolbar.setContentInsetsAbsolute(0,0);       // 좌우 여백 제거.
        tvToolbarTitle.setText(toolbarTitle);
        tvToolbarTitle.setTextColor(getResources().getColor(R.color.colorRed));

        if(backBtnVisible) { // 뒤로가기 버튼 보이기.
            toolbar.setNavigationIcon(R.drawable.ic_launcher_foreground);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 슬라이드 애니메이션.
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
