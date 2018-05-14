package com.qhsd.qhtzb.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.qhsd.qhtzb.R;

public class LoadingActivity extends AppCompatActivity {
    private ImageView ivBanner;
    private static final int TIME = 3000;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ivBanner = (ImageView) findViewById(R.id.iv_banner);
//        initData();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME);

    }

    private void initData() {


        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
        aa.setDuration(3000);
        aa.setFillAfter(true);
        ivBanner.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                ivBanner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                ivBanner.setVisibility(View.VISIBLE);
            }
        });
    }
}
