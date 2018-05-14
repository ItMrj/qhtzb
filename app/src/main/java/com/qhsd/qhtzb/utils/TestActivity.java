package com.qhsd.qhtzb.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.qhsd.qhtzb.web.MainActivity;

import cn.jpush.android.api.JPushInterface;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("用户自定义打开的Activity");
        addContentView(tv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = null;
            String content = null;
            if (bundle != null) {
                title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                content = bundle.getString(JPushInterface.EXTRA_ALERT);
            }
            tv.setText("Title : " + title + "  " + "Content : " + content);
            Intent intent1 = new Intent();
            intent1.setAction(MainActivity.MESSAGE_RECEIVED_ACTION);
            intent1.putExtra("msg",content);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        }
        finish();
    }

}
