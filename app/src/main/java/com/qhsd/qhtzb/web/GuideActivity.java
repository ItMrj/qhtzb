package com.qhsd.qhtzb.web;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qhsd.qhtzb.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private TextView loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initViews();
    }
    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        View firstViews = inflater.inflate(R.layout.gulide_one, null);
        View secondViews = inflater.inflate(R.layout.gulide_two, null);
        View threeviews=inflater.inflate(R.layout.gulide_three,null);
        views.add(firstViews);
        views.add(secondViews);
        views.add(threeviews);
        vpAdapter = new ViewPagerAdapter(this, views);
        vp = (ViewPager) findViewById(R.id.viewpager);
        loginActivity = (TextView) views.get(2).findViewById(R.id.first_login);
        vp.setAdapter(vpAdapter);
        loginActivity.setOnClickListener(this);
        vp.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
