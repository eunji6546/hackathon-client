package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf);
        mText4.setTypeface(tf);
        mText5.setTypeface(tf);
        mText6.setTypeface(tf);

        Intent intent = getIntent();
        mUserType = intent.getExtras().getString("usertype");

        if (mUserType.equals("1")) { // for driver
            mCarType = intent.getExtras().getString("cartype");
            mCarNumber = intent.getExtras().getString("carnumber");
            mCash = intent.getExtras().getString("cash");
            mText3.setText("Cash : $" + mCash);
            mText1.setText("My Account \n" + mCarNumber);
        }

    }

    public void mClick1(View view) {
        Intent intent1 = new Intent(this,AccountActivity.class);
        startActivity(intent1);
    }
    public void mClick2(View view) {}
    public void mClick3(View view) {
        Intent intent3 = new Intent(this,MyCashActivity.class);
        intent3.putExtra("usertype", mUserType);

        if (mUserType.equals("1")) { // for driver
            intent3.putExtra("carnumber", mCarNumber);
            intent3.putExtra("cartype", mCarType);
            intent3.putExtra("cash", mCash);
        }

        startActivity(intent3);
    }
    public void mClick4(View view) {
        Intent intent4 = new Intent(this,MainActivity.class);

        intent4.putExtra("usertype", mUserType);

        if (mUserType.equals("1")) { // for driver
            intent4.putExtra("carnumber", mCarNumber);
            intent4.putExtra("cartype", mCarType);
            intent4.putExtra("cash", mCash);
        }

        startActivity(intent4);
    }

    public void mClick5(View view){
        Intent intent5 = new Intent(this,AlertActivity.class);

        intent5.putExtra("usertype", mUserType);

        if (mUserType.equals("1")) { // for driver
            intent5.putExtra("carnumber", mCarNumber);
            intent5.putExtra("cartype", mCarType);
            intent5.putExtra("cash", mCash);
        }

        startActivity(intent5);
    }

    public void mClick6(View view) {
        Intent intent6 = new Intent(this,CarPoolActivity.class);

        intent6.putExtra("usertype", mUserType);

        if (mUserType.equals("1")) { // for driver
            intent6.putExtra("carnumber", mCarNumber);
            intent6.putExtra("cartype", mCarType);
            intent6.putExtra("cash", mCash);
        }

        startActivity(intent6);
    }

}
