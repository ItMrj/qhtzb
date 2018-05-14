package com.qhsd.qhtzb.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.qhsd.qhtzb.R;

/**
 * 自定义进度对话框
 * 
 */
public class CustomProgressDialog extends Dialog {

    public static final int THEME_DARK = 0;
    public static final int THEME_LIGHT = 1;

    private ImageView iv;
    private Animation animation;

    public CustomProgressDialog(Context context, int style, int resId) {
        this(context, THEME_DARK ==style ? R.style.base_ProgressDialogDark : R.style.base_ProgressDialogLight, style, resId);
	}

    public CustomProgressDialog(Context context, int style, String text) {
        this(context, THEME_DARK ==style ? R.style.base_ProgressDialogDark : R.style.base_ProgressDialogLight, style, text);
    }

	public CustomProgressDialog(Context context, int theme, int style, int resId) {
		super(context, theme);

        if (THEME_DARK == style) {
            setContentView(R.layout.base_dialog_progress_dark);
        } else {
            setContentView(R.layout.base_dialog_progress_light);
        }
		getWindow().getAttributes().gravity = Gravity.CENTER;

		TextView tv = (TextView) findViewById(R.id.tv_dialog_loading);
        tv.setText(resId);
        iv = (ImageView) findViewById(R.id.iv_dialog_loading);
        animation = AnimationUtils.loadAnimation(context, R.anim.base_progress_rotate);
        animation.setInterpolator(new LinearInterpolator()); //使用xml配置会有停顿
	}

    public CustomProgressDialog(Context context, int theme, int style, String text) {
        super(context, theme);

        if (THEME_DARK == style) {
            setContentView(R.layout.base_dialog_progress_dark);
        } else {
            setContentView(R.layout.base_dialog_progress_light);
        }
        getWindow().getAttributes().gravity = Gravity.CENTER;

        TextView tv = (TextView) findViewById(R.id.tv_dialog_loading);
        tv.setText(text);
        iv = (ImageView) findViewById(R.id.iv_dialog_loading);
        animation = AnimationUtils.loadAnimation(context, R.anim.base_progress_rotate);
        animation.setInterpolator(new LinearInterpolator()); //使用xml配置会有停顿
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			dismiss();
		}
	}

    @Override
    public void show() {
        super.show();
        iv.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.clearAnimation();
            }
        });
    }
}