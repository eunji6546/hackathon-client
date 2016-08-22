package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.MeasureUnit;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    String mUserType; // 1 for driver, 0 for walker
    String mCarType;
    String mCarNumber;
    String mCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        TextView mText2 = (TextView) findViewById(R.id.text2);
        TextView mText3 = (TextView) findViewById(R.id.text3);
        TextView mText4 = (TextView) findViewById(R.id.text4);
        TextView mText5 = (TextView) findViewById(R.id.text5);
        TextView mText6 = (TextView) findViewById(R.id.text6);
        TextView mText7 = (TextView) findViewById(R.id.text7);
        TextView mText8 = (TextView) findViewById(R.id.text8);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf);
        mText4.setTypeface(tf);
        mText5.setTypeface(tf);
        mText6.setTypeface(tf);
        mText7.setTypeface(tf);
        mText8.setTypeface(tf);

        mText7.setText(AccountActivity.mCarNumber);
        mText8.setText(AccountActivity.mCarCash + " won");
        Log.v("From userNumber", String.valueOf(AccountActivity.mCarNumber));
        Log.v("From userType", String.valueOf(AccountActivity.mUserType));

    }

    public void mClick1(View view) {
        Intent intent1 = new Intent(this,AccountActivity.class);
        startActivity(intent1);
    }
    public void mClick2(View view) {

        Intent intent2 = new Intent(this,SearchPathActivity.class);
        startActivity(intent2);

    }
    public void mClick3(View view) {
        Intent intent3 = new Intent(this,MyCashActivity.class);
        if (AccountActivity.mUserType == 0)
            Toast.makeText(MenuActivity.this,"차량등록 후 이용가능합니다", Toast.LENGTH_SHORT).show();
        else {
            startActivity(intent3);
        }
    }
    public void mClick4(View view) {
        Intent intent4 = new Intent(this,EVStationActivity.class);
        startActivity(intent4);
    }

    public void mClick5(View view){
        Intent intent5 = new Intent(this,AlertActivity.class);
        startActivity(intent5);
    }

    public void mClick6(View view) {
        Intent intent6 = new Intent(this,CarPoolActivity.class);
        if (AccountActivity.mUserType == 0)
            Toast.makeText(MenuActivity.this,"차량등록 후 이용가능합니다", Toast.LENGTH_SHORT).show();
        else {
            startActivity(intent6);
        }
    }

    protected void onResume() {
        super.onResume();

        TextView mText7 = (TextView) findViewById(R.id.text7);
        TextView mText8 = (TextView) findViewById(R.id.text8);

        mText7.setText(AccountActivity.mCarNumber);
        mText8.setText(AccountActivity.mCarCash + " won");

    }

}
