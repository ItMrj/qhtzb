package com.qhsd.qhtzb.web;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.qhsd.qhtzb.R;

import java.lang.ref.WeakReference;

public class WelcomeActivity extends AppCompatActivity {
    // 是否为第一次进入APP
    private boolean isFirstIn = false;
    // 欢迎页面延迟3秒
    private static final int TIME = 3000;
    // 消息发送 标志 引导界面还是主界面
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private MyHandler handler;

    static class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        MyHandler(WelcomeActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WelcomeActivity act = (WelcomeActivity) mActivity.get();
            switch (msg.what) {
                case 1000:
                    act.goHome();
                    break;
                case 1001:
                    act.goGuide();
                    break;

            }
        }
    }

    private void init() {
        SharedPreferences perPreferences = getSharedPreferences("FirstCommit", MODE_PRIVATE);
            isFirstIn = perPreferences.getBoolean("isFirstIn", true);
            if (!isFirstIn) {
                handler.sendEmptyMessageDelayed(GO_HOME, TIME);
            } else {
                handler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
                SharedPreferences.Editor editor = perPreferences.edit();
                editor.putBoolean("isFirstIn", false);
                editor.commit();
        }

    }

    private void goHome() {
        Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goGuide() {
        Intent i = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler = new MyHandler(this);
            init();
    }


}
