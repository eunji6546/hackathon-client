package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    // 인트로 화면 대기시간
    private static final int SPLASH_TIMEOUT = 2000;
    protected TextView mFrontText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mFrontText = (TextView) findViewById(R.id.frontText);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");
        mFrontText.setTypeface(tf);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, AccountActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

}


