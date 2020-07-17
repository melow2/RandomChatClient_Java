package com.hellostranger.client.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.hellostranger.client.BuildConfig;
import com.hellostranger.client.MainHandler;
import com.hellostranger.client.R;
import com.hellostranger.client.core.RandomChatLog;
import com.hellostranger.client.core.WeakHandler;
import com.hellostranger.client.databinding.MainActivityBinding;
import com.hellostranger.client.view.dialog.CloseDialog;
import com.hellostranger.client.view.dialog.SelectSexDialog;
import com.hyeoksin.admanager.AdManager;
import com.hyeoksin.admanager.OnInterstitialAdLoadListener;
import com.hyeoksin.admanager.data.Ad;
import com.hyeoksin.admanager.data.AdName;
import com.hyeoksin.admanager.data.AdType;

import java.io.IOException;
import java.util.Locale;

import static com.hellostranger.client.core.ClientAsyncTask.ReConnectTask;
import static com.hellostranger.client.core.ClientAsyncTask.SendMessageTask;
import static com.hellostranger.client.core.ClientAsyncTask.ServerConnectTask;
import static com.hellostranger.client.core.SocketManager.exit;

public class MainActivity extends BaseActivity<MainActivityBinding> {

    private CloseDialog closeDialog;
    private WeakHandler mWeakHandler;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static String CURRENT_SEX = null;
    private final String MALE = "M";
    private final String FEMALE = "F";
    private long mLastClickTime = 0;
    private AdManager Interstitial, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView(R.layout.activity_main);
        setToolbar((Toolbar) mBinding.toolbar, false, getString(getApplicationInfo().labelRes), findViewById(R.id.tv_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        setNavigation();
        setAdvertisement();
        setPopupSex();
        init();
    }

    private void setAdvertisement() {
        Interstitial = new AdManager.Builder(MainActivity.this)
                .setAd(new Ad(AdName.ADMOB, AdType.INTERSTITIAL, getString(R.string.admob_interstitial)))
                .setOnInterstitialAdLoadListener(new OnInterstitialAdLoadListener() {
                    @Override
                    public void onAdLoaded() {
                        Interstitial.showInterstitial();
                    }

                    @Override
                    public void onAdFailedToLoad() {
                    }
                })
                .build();
        bottom = new AdManager.Builder(MainActivity.this)
                .setContainer(mBinding.adContainer)
                .setAd(new Ad(AdName.ADMOB, AdType.BANNER, getString(R.string.admob_banner_bottom)))
                .build();
        Interstitial.load();
        bottom.load();
        setPopupAd();
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
            public void onClickMale() throws IOException {
                CURRENT_SEX = MALE;
                mBinding.scvMsgItem.setBackgroundColor(getResources().getColor(R.color.colorSkyBlue));
                new ServerConnectTask(MainActivity.this, mBinding, CURRENT_SEX).execute();
                selectSexDialog.dismiss();
            }

            @Override
            public void onClickFemale() throws IOException {
                CURRENT_SEX = FEMALE;
                new ServerConnectTask(MainActivity.this, mBinding, CURRENT_SEX).execute();
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
        mBinding.edtMsg.setFilters(new InputFilter[]{new InputFilter.LengthFilter(400)});
        eventHandler.addEventListener(new MainHandler.MainHandlerEvent() {
            @Override
            public void onClickSendBtn(String msg) {
                String sendMessage = msg.trim();
                System.out.println(sendMessage.length());
                if (sendMessage.length() != 0) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        showToast(MainActivity.this, "천천히");
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new SendMessageTask().execute(sendMessage);
                    addView(msg, 3);
                }
                mBinding.edtMsg.setText("");
            }

            @Override
            public void onClickBtnReload(String msg) {
                reConnect(msg);
            }
        });
        mBinding.setHandler(eventHandler);
    }


    private void reConnect(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(null);
        builder.setMessage(msg);
        builder.setPositiveButton("확인", (dialog, which) -> {
            mBinding.lytMsgline.removeAllViews();
            new ReConnectTask().execute();
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }


    private void addView(String msg, int i) {
        mWeakHandler.post(() -> {
            mBinding.lytMsgline.addView(new RandomChatLog(MainActivity.this, mBinding, msg, null, i));
            mBinding.scvMsgItem.post(() -> mBinding.scvMsgItem.fullScroll(View.FOCUS_DOWN));
            mBinding.edtMsg.requestFocus();
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
        super.onDestroy();
        exit();
    }

    @Override
    public void onBackPressed() {
        closeDialog.show();
    }

}

