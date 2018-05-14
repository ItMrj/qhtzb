package com.qhsd.qhtzb.web;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qhsd.qhtzb.CheckChannelEntity;
import com.qhsd.qhtzb.CheckFuturesAppUpdateEntity;
import com.qhsd.qhtzb.R;
import com.qhsd.qhtzb.utils.AppVersionUtil;
import com.qhsd.qhtzb.utils.Downloader;
import com.qhsd.qhtzb.utils.LocalBroadcastManager;
import com.qhsd.qhtzb.utils.Utils;
import com.qhsd.qhtzb.view.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends Activity {

    private WebView webView;
    private String url;
    private CustomProgressDialog dialog;
    private LinearLayout errorLayout;
    private final String APP_CACAHE_DIRNAME = "sjdk";
    boolean loadError = false;
    /**
     *   最后一次加载错误的url
     */
    private String lastUrl;
    private ImageView iv_loading;
    private long exitTime;
    private Handler handler = new Handler();
    public static final String MESSAGE_RECEIVED_ACTION = "RESUME";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public static boolean isForeground = false;

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE2 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String u = " https://mbbd-api.houputech.com/1.0/v_android_";
                    int r = AppVersionUtil.getVersionCode(getApplicationContext());
                    URL url1 = new URL(u + r + "/Api/FuturesAppRedirectUrl?type=0&channel=" + getPackageName());
                    HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    conn.getInputStream();
                    CheckChannelEntity entity = new Gson().fromJson(inputStream2String(conn.getInputStream()), CheckChannelEntity.class);
                    url = entity.getInnerData().getUrl();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            registerMessageReceiver();
                            initMobclick();
                            initView();
                            initWebSet();
                            webView.loadUrl(url);
                            initEvent();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


//        https://mbbd-api.houputech.com/1.0/contentsV2/CheckFuturesAppUpdate?t=andriod&p=com.qhsd.xjjj_huawei&v=2.1
    }

    private void initDialog(final CheckFuturesAppUpdateEntity entity) {
        final Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_apk, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(entity.getInnerData().getContent());
        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.sure_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Downloader downloader = new Downloader(MainActivity.this);
                downloader.downloadAPK(entity.getInnerData().getDownloadUrl(), getPackageName() + ".apk");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private String inputStream2String(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.main_web);
        errorLayout = (LinearLayout) findViewById(R.id.main_web_error);
        iv_loading = (ImageView) findViewById(R.id.image_loading);
        iv_loading.setVisibility(View.VISIBLE);
        dialog = new CustomProgressDialog(this, CustomProgressDialog.THEME_DARK, "加载中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        //        dialog.show();
    }


    private void initWebSet() {
        webView.clearCache(true);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 不支持缩放(如果要支持缩放，html页面本身也要支持缩放：不能加user-scalable=no)
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);

        settings.setAllowFileAccess(true);
        settings.setSaveFormData(false);
        settings.setDomStorageEnabled(true);
        // 调整到适合webView大小
        settings.setUseWideViewPort(true);
        //调整到适合webView大小
        settings.setLoadWithOverviewMode(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);

        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
//关闭webView中缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Utils.isNetworkAvailable(getApplication())) {
            //有网络连接，设置默认缓存模式
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //无网络连接，设置本地缓存模式
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //缓存路径
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        //设置缓存路径
        settings.setAppCachePath(cacheDirPath);
        //开启缓存功能
        settings.setAppCacheEnabled(true);
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        //设置编码格式
        settings.setDefaultTextEncodingName("utf-8");
        webView.requestFocus();
        webView.setWebViewClient(new CusWebViewClient());
        webView.setWebChromeClient(new CusWebChromeClient());

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE2);
                } else {
                    Downloader downloadAPK = new Downloader(MainActivity.this);
                    downloadAPK.downloadAPK(url, getPackageName() + "allipy.apk");
                }
            }
        });
    }


    /**
     * 初始化友盟统计
     */
    private void initMobclick() {
        // 场景类型设置接口,设置为普通统计场景类型
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        String appKey;
        String channel;
        appKey = Utils.getMateDataValue(this, "UMENG_APPKEY", "5a94c652f29d987cc900021a");
        channel = Utils.getMateDataValue(this, "UMENG_CHANNEL", "anzhuo");
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this, appKey, channel);
        MobclickAgent.startWithConfigure(config);
        //自定义启动会话时间周期
//        MobclickAgent.setSessionContinueMillis(2000);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            if (webView.canGoBack()) {
                webView.goBack();
                return false;
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public void initEvent() {
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadError = false;
                dialog.show();
                webView.loadUrl(lastUrl);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        webView.removeAllViews();
        webView.destroy();
        isForeground = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    /**
     * for receive customer msg from jPush server
     */
    private MessageReceiver mMessageReceiver;

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (intent.getStringExtra("msg").startsWith("美原油") || intent.getStringExtra("msg").startsWith("温馨提示")) {
                        View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_open_success, null);
                        TextView timeTv = (TextView) view1.findViewById(R.id.dialog_open_success_tv);
                        TextView tieleTv = (TextView) view1.findViewById(R.id.dialog_title);
                        timeTv.setText(intent.getStringExtra("msg"));
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        tieleTv.setText(format.format(new Date()).substring(5, format.format(new Date()).length()) + " 策略建议");
                        Button dialogBtn = (Button) view1.findViewById(R.id.dialog_btn);

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(view1);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        dialogBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        // 设置点击屏幕Dialog不消失
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                        WindowManager m = getWindowManager();
                        DisplayMetrics outMetrics = new DisplayMetrics();
                        m.getDefaultDisplay().getMetrics(outMetrics);
                        //获取对话框当前的参数值
                        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
//                    p.height = (int) (outMetrics.heightPixels * 0.6);   //高度设置为屏幕的0.5
                        //宽度设置为屏幕的0.8
                        p.width = (int) (outMetrics.widthPixels * 0.8);
                        //设置生效
                        dialog.getWindow().setAttributes(p);
                    } else {

                    }
                }
            } catch (Exception e) {
            }
        }
    }


    /**
     * 退出系统
     */
    protected void exitSystem() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
        System.exit(0);
    }

    /**
     * 调起支付宝并跳转到指定页面
     */
    private void startAlipayActivity(String url) {
        Intent intent;
        try {
            intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CusWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {

            String tag = "tel";
            if (url.contains("alipays://platformapi")) {
                if (checkAliPayInstalled(MainActivity.this)) {
                    startAlipayActivity(url);
                } else {
                    Toast.makeText(MainActivity.this, "支付宝未安装，请先安装支付宝！", Toast.LENGTH_SHORT).show();
//                    return true;
                }
                return true;
            }


            if (url.contains(tag)) {
                String mobile = url.substring(url.lastIndexOf("/") + 1);
                Intent mIntent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse(mobile);
                mIntent.setData(data);
                //Android6.0以后的动态获取打电话权限
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(mIntent);
                    //这个超连接,java已经处理了，webview不要处理
                    return true;
                } else {
                    //申请权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return true;
                }
            }
            if (url.startsWith("http:") || url.startsWith("https:")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(url);
                } else {
                    view.loadUrl(url);
                }
            }
            return true;
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            loadError = true;
            lastUrl = view.getUrl();
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);

            handler.proceed();
        }


        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
//         这个方法在6.0才出现
            int statusCode = errorResponse.getStatusCode();
            if (403 == statusCode || 500 == statusCode) {
                loadError = true;
                lastUrl = view.getUrl();
            }
        }

        /**
         * 网页加载结束的时候执行的回调方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_loading.setVisibility(View.GONE);
                    if (!checkPermissionAllGranted(
                            new String[]{
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }
                    )) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.CALL_PHONE,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                MY_PERMISSION_REQUEST_CODE
                        );
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String u = "https://mbbd-api.houputech.com/1.0/contentsV2/CheckFuturesAppUpdate?t=android";
                                    URL url1 = new URL(u + "&p=" + getPackageName() + "_" + getChannelName(MainActivity.this) + "&v=" + AppVersionUtil.getVersionName(MainActivity.this));
                                    HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
                                    conn.setRequestMethod("GET");
                                    conn.setDoInput(true);
                                    conn.connect();
                                    conn.getInputStream();
                                    final CheckFuturesAppUpdateEntity entity = new Gson().fromJson(inputStream2String(conn.getInputStream()), CheckFuturesAppUpdateEntity.class);
                                    Log.d("marj", "run: " + entity.getMessage());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (entity != null) {
                                                if (entity.isResult()) {
                                                    initDialog(entity);
                                                }
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }, 1000);


            webView.setVisibility(View.VISIBLE);

        }
    }


    public static boolean checkAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    private class CusWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                dialog.dismiss();
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.d("marj", "onReceivedTitle: " + title);
            lastUrl = view.getUrl();
            //android 6.0 以下通过title获取
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                if (title.contains("404") || title.contains("500") || title.contains("Error")|| title.contains("找不到网页")) {
//                    loadError = true;
//                    lastUrl = view.getUrl();
//                }
//            }
        }
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    /**
     * 获取友盟渠道名
     *
     * @param ctx 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName(Activity ctx) {
        if (ctx == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        //此处这样写的目的是为了在debug模式下也能获取到渠道号，如果用getString的话只能在Release下获取到。
                        channelName = applicationInfo.metaData.get("UMENG_CHANNEL") + "";
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }


    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String u = "https://mbbd-api.houputech.com/1.0/contentsV2/CheckFuturesAppUpdate?t=android";
//                            URL url1 = new URL(u +"&p="+getPackageName()+"_"+getChannelName(MainActivity.this)+"&v="+AppVersionUtil.getVersionCode(MainActivity.this));
//                            HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
//                            conn.setRequestMethod("GET");
//                            conn.setDoInput(true);
//                            conn.connect();
//                            conn.getInputStream();
//                            final CheckFuturesAppUpdateEntity entity = new Gson().fromJson(inputStream2String(conn.getInputStream()), CheckFuturesAppUpdateEntity.class);
//                            Log.d("marj", "run: "+entity.getMessage());
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (entity!=null){
//                                        if (entity.isResult()){
//                                            initDialog(entity);
//                                        }
//                                    }
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();

            } else {

            }
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     *
     * @param permissions 权限集合
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

}
