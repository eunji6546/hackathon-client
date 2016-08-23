package com.example.eunji_mac.hackathon_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.realtime.internal.event.TextInsertedDetails;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;

public class HostingActivity extends AppCompatActivity {
    EditText mStart, mEnd, mDate, mFare, mAvailable;
    private String mDateString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        TextView mText2 = (TextView) findViewById(R.id.text2);
        mText2.setText("Car Number : " + AccountActivity.mCarNumber);
        TextView mText3 = (TextView) findViewById(R.id.text3);
        TextView mText4 = (TextView) findViewById(R.id.text4);
        TextView mText5 = (TextView) findViewById(R.id.text5);
        TextView mText6 = (TextView) findViewById(R.id.text6);
        TextView mText7 = (TextView) findViewById(R.id.text7);

        mStart = (EditText) findViewById(R.id.start);
        //mStart.setSelection(3);

        mEnd = (EditText) findViewById(R.id.end);
        //mEnd.setSelection(3);

        mDate = (EditText) findViewById(R.id.date);
        //mDate.setSelection(12);

        mFare = (EditText) findViewById(R.id.fare);

        mAvailable = (EditText) findViewById(R.id.available);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf); // 출발
        mText4.setTypeface(tf); // 도착
        mText5.setTypeface(tf);
        mText6.setTypeface(tf);
        mText7.setTypeface(tf);

        mDate.setTypeface(tf);
        mFare.setTypeface(tf);
        mAvailable.setTypeface(tf);


    }


    // 신청
    public void mClick3(View v) {

        mDateString = mDate.getText().toString();

        if (mDateString.length() != 12) {
            Toast.makeText(HostingActivity.this, "날짜를 바르게 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UrlConnection urlconn = new UrlConnection();
                    String mFareString = mFare.getText().toString();
                    String mAvString = mAvailable.getText().toString();
                    String mStartString = mStart.getText().toString();
                    String mEndString = mEnd.getText().toString();
                    urlconn.AddHosting(mStartString, mEndString, mDateString, mFareString, mAvString);

                    Intent intent = new Intent(HostingActivity.this, CarPoolActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
