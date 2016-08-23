package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    String mStationType = "";
    EditText mCarNum;

    static String mCarNumber = "Non-driver";
    static String mCarCash = "0";
    static String mCarType;
    static int mUserType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mCarNum = (EditText) findViewById(R.id.carnumber);

//        mCarNum.setSelection(6);

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

        //차량
        String[] car_str = getResources().getStringArray(R.array.carSpinnerArray);

        ArrayAdapter<String> mCarAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, car_str);

        // 차번호
        Spinner mCarSpinner = (Spinner) findViewById(R.id.vehicle);
        mCarSpinner.setAdapter(mCarAdapter);


        mCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    mStationType = "상";
                } else if ((position == 1) || (position == 6)) {
                    mStationType = "콤보";
                } else {
                    mStationType = "차데모";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    //save my info
    public void mClick1(View view) {
        final String mCarNumString = mCarNum.getText().toString();
        if (mCarNumString.length() != 7) {
            Toast.makeText(AccountActivity.this, "잘못된 차량번호입니다", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UrlConnection urlconn = new UrlConnection();
                    mCarNumber = mCarNumString;
                    Log.v("Account Activity", mCarNumber);
                    mCarCash = urlconn.Save(mCarNumString, mStationType, "0");
                    mCarType = mStationType;
                    mUserType = 1;

                    Intent intent = new Intent(AccountActivity.this, MenuActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // no car
    public void mClick2(View view) { //
        Intent intent = new Intent(AccountActivity.this, MenuActivity.class);
        startActivity(intent);
    }
}

