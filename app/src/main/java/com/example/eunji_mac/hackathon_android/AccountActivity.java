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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    String mStationType = "";
    EditText mCarNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mCarNum = (EditText) findViewById(R.id.carnumber);
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

        String[] car_str = getResources().getStringArray(R.array.carSpinnerArray);

        ArrayAdapter<String> mCarAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, car_str);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UrlConnection urlconn = new UrlConnection();
                    String mCash =
                            urlconn.Save(mCarNum.getText().toString(), mStationType, Double.toString(0.0));

                    Intent intent = new Intent(AccountActivity.this, MenuActivity.class);
                    intent.putExtra("usertype", "1"); // for driver
                    intent.putExtra("carnumber", mCarNum.getText().toString());
                    intent.putExtra("cartype", mStationType);
                    intent.putExtra("cash", mCash); // for car driver

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
        intent.putExtra("usertype", "0"); // for walker
        startActivity(intent);
    }



}

