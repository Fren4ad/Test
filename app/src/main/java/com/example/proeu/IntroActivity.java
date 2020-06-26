package com.example.proeu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import static maes.tech.intentanim.CustomIntent.customType;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;


public class IntroActivity extends AppCompatActivity {

    private ViewPager scrennPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnGetStarted;
    Button btnLogin;
    private int CurrentIndex = 0;
    int position = 0;
    Animation btnAnim, btnRegisr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            CurrentIndex = savedInstanceState.getInt("CurrentIndex", 0);
        }
        //make the activity on full screen

        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        */

        setContentView(R.layout.activity_intro);
        // hide the action bar

        getSupportActionBar().hide();

        // ini views
        btnGetStarted = findViewById(R.id.btn_get_started);
        btnLogin = findViewById(R.id.btn_login);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        btnRegisr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.registr_animation);


        // fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Takallem", "Удобное приложение для изучения арабского языка", R.drawable.img1));
        mList.add(new ScreenItem("Простота использования", "Наличие гибкой системы уровней изучения арабского языка", R.drawable.img2));
        mList.add(new ScreenItem("Экономия денег", "Бесплатные возможности изучения элементарного и начального уровней арабского языка", R.drawable.img3));
        mList.add(new ScreenItem("Более 1000 слов", "Интегрированный словарь", R.drawable.img4));
        mList.add(new ScreenItem("Обратная связь", "Возможность задать вопрос преподавателям арабского языка в онлайн режиме", R.drawable.img5));


        //setup viewpager
        scrennPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        scrennPager.setAdapter(introViewPagerAdapter);


        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(scrennPager);


        // when we rech to the last screen


        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1)
                    loaddLastScreen();
                else {
                    loaPreviewScreen();
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnGetStarted.startAnimation(btnRegisr);
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                customType(IntroActivity.this, "bottom-to-up");
                finish();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.startAnimation(btnRegisr);
                startActivity(new Intent(IntroActivity.this, Authentication.class));
                customType(IntroActivity.this, "bottom-to-up");
                finish();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentIndex", CurrentIndex);
    }

    private void loaPreviewScreen() {
        btnLogin.clearAnimation();
        btnLogin.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        btnGetStarted.clearAnimation();
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {
        btnLogin.startAnimation(btnAnim);
        btnLogin.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        //setup animation
        btnGetStarted.startAnimation(btnAnim);

    }


}


