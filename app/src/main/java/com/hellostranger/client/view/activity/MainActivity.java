package com.hellostranger.client.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.hellostranger.client.BuildConfig;
import com.hellostranger.client.MainHandler;
import com.hellostranger.client.R;
import com.hellostranger.client.core.ChatClient;
import com.hellostranger.client.core.ChatLog;
import com.hellostranger.client.core.WeakHandler;
import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.view.dialog.CloseDialog;
import com.hellostranger.client.view.dialog.SelectSexDialog;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends BaseActivity<MainActivityBinding> {

    private ExecutorService mClientThreadPool;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private ChatClient mChatClient = null;
    private WeakHandler mWeakHandler;
    private CloseDialog closeDialog;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static String CURRENT_SEX = null;
    private final String MALE = "M";
    private final String FEMALE = "F";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView(R.layout.activity_main);
        setToolbar((Toolbar) mBinding.toolbar, false, getString(getApplicationInfo().labelRes), findViewById(R.id.tv_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        setNavigation();
        setPopupAd();
        setPopupSex();
        init();
    }

    private void setNavigation() {
        mBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_item_notice:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    case R.id.navigation_item_review:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        break;
                }
                mBinding.drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setPopupSex() {
        SelectSexDialog selectSexDialog = new SelectSexDialog(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            selectSexDialog.create();
        }

        selectSexDialog.addButtonListener(new SelectSexDialog.Event() {
            @Override
            public void onClickMale() {
                CURRENT_SEX = MALE;
                mBinding.scvMsgItem.setBackgroundColor(getResources().getColor(R.color.colorSkyBlue));
                connect(MALE);
                selectSexDialog.dismiss();
            }

            @Override
            public void onClickFemale() {
                CURRENT_SEX = FEMALE;
                connect(FEMALE);
                selectSexDialog.dismiss();
            }
        });
        selectSexDialog.setCancelable(false);
        selectSexDialog.show();
    }


    private void setPopupAd() {
        closeDialog = new CloseDialog(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeDialog.create();
        }
        closeDialog.addButtonListener(new CloseDialog.ButtonEvent() {
            @Override
            public void onPositiveBtn() {
                closeDialog.dismiss();
                finish();
            }

            @Override
            public void onNegativeBtn() {
                closeDialog.dismiss();
            }
        });
    }


    private void init() {
        mWeakHandler = new WeakHandler(Looper.getMainLooper());
        MainHandler eventHandler = new MainHandler(this, mBinding);
        eventHandler.addEventListener(new MainHandler.MainHandlerEvent() {
            @Override
            public void onClickSendBtn(String msg) {
                mThreadPoolExecutor.execute(() -> {
                    try {
                        String sendMsg = msg.trim();
                        if (!sendMsg.equals("")) {
                            mChatClient.mDos.writeUTF(msg);
                            addView(msg, 3);
                        }
                        mWeakHandler.post(() -> {
                            mBinding.edtMsg.setText("");
                        });
                    } catch (IOException e) {
                        mWeakHandler.post(() -> {
                            showToast(getApplicationContext(), "낯선사람이 떠났습니다.");
                        });
                    }
                });
            }

            @Override
            public void onClickBtnReload(String msg) {
                reload(msg);
            }
        });
        mBinding.setHandler(eventHandler);
    }


    private void reload(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null);
        builder.setMessage(getString(R.string.msg_reload));
        builder.setPositiveButton("확인", (dialog, which) -> {
            mBinding.lytMsgline.removeAllViews();
            mChatClient.disconnect();
            mClientThreadPool.shutdownNow();
            mThreadPoolExecutor.shutdownNow();
            // mWeakHandler.postDelayed(this::connect,500);
            connect(CURRENT_SEX);
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void connect(String sex) {
        mClientThreadPool = Executors.newFixedThreadPool(5);
        mThreadPoolExecutor = (ThreadPoolExecutor) mClientThreadPool;
        mChatClient = new ChatClient(this, mBinding, CURRENT_SEX);
        mThreadPoolExecutor.execute(mChatClient);
    }

    private void addView(String msg, int i) {
        mWeakHandler.post(() -> {
            mBinding.lytMsgline.addView(new ChatLog(MainActivity.this, mBinding, msg, null, i));
            mBinding.scvMsgItem.post(() -> mBinding.scvMsgItem.fullScroll(View.FOCUS_DOWN));
            mBinding.edtMsg.post(() -> mBinding.edtMsg.requestFocus());
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBinding.drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.share:
                String title = String.format(Locale.getDefault(), getString(R.string.msg_share_message), getString(getApplicationInfo().labelRes)); // app_name
                String content = String.format(Locale.getDefault(), getString(R.string.share_store_url), getPackageName());                         // com.flower

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onOptionsItemSelected()");
                    Log.d(TAG, "title: " + title);
                    Log.d(TAG, "content: " + content);
                }
                Intent intent = new Intent(Intent.ACTION_SEND) // 공유하기.
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, getString(getApplicationInfo().labelRes))
                        .putExtra(Intent.EXTRA_TEXT, title + "\n" + content);
                startActivity(Intent.createChooser(intent, getString(R.string.share_app)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        // 제대로 실행이 되지 않음.
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if (mChatClient != null) {
            mChatClient.disconnect();
            Log.d(TAG, "disconnect()");
/*            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // 앱이 먼저 종료됨.
                mClientThreadPool.shutdownNow();
                mThreadPoolExecutor.shutdownNow();
                Log.d(TAG,"ShutDown hook");
            }));*/
        }
    }

    @Override
    public void onBackPressed() {
        closeDialog.show();
    }
}

