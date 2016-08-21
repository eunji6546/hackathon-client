package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class EVActivity extends AppCompatActivity {

    String mUserType; // 1 for driver, 0 for walker
    String mCarType, mCarNumber, mCash;

    RadioButton option1, option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ev);

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        TextView mText2 = (TextView) findViewById(R.id.text2);
        TextView mText3 = (TextView) findViewById(R.id.text3);
        TextView mText4 = (TextView) findViewById(R.id.text4);
        TextView mText5 = (TextView) findViewById(R.id.text5);
        TextView mText6 = (TextView) findViewById(R.id.text6);
        TextView mText7 = (TextView) findViewById(R.id.text7);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf);
        mText4.setTypeface(tf);
        mText5.setTypeface(tf);
        mText6.setTypeface(tf);
        mText7.setTypeface(tf);

        // userinfo 받기
        Intent intent = getIntent();
        mUserType = intent.getExtras().getString("usertype");

        if (mUserType.equals("1")) { // for driver
            mCarType = intent.getExtras().getString("cartype");
            mCarNumber = intent.getExtras().getString("carnumber");
            mCash = intent.getExtras().getString("cash");
        }
    }

    public void mClick1(View view) {
        option1 = (RadioButton) findViewById(R.id.text2);
        option2 = (RadioButton) findViewById(R.id.text3);

        if (option1.isChecked()) {
            Intent intent1 = new Intent(EVActivity.this, SearchNearStationActivity.class);
            intent1.putExtra("usertype", mUserType);

            if (mUserType.equals("1")) { // for driver
                intent1.putExtra("carnumber", mCarNumber);
                intent1.putExtra("cartype", mCarType);
                intent1.putExtra("cash", mCash);
            }
            startActivity(intent1);
        } else {
            Intent intent2 = new Intent(EVActivity.this, SelectRegionActivity.class);
            intent2.putExtra("usertype", mUserType);

            if (mUserType.equals("1")) { // for driver
                intent2.putExtra("carnumber", mCarNumber);
                intent2.putExtra("cartype", mCarType);
                intent2.putExtra("cash", mCash);
            }
            startActivity(intent2);
        }

    }
}
