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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SelectRegionActivity extends AppCompatActivity {

    //Get userinfo by intent
    int mUserType; // 1 for driver, 0 for walker
    String mCarType, mCarNumber, mCash;

    Spinner mCitySpinner, mTownSpinner;

    final List<String> cityList =
            new ArrayList<String>(Arrays.asList("강원", "경기", "경남", "경북", "광주",
                    "대구", "대전", "부산", "서울", "울산", "인천", "전남", "전북", "제주", "충남", "충북"));


    //For searching location of station
    ArrayAdapter<String> mTownAdapter = null;
    ArrayList<String> town_str = null;
    String mStationType;
    Integer mcityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        TextView mText2 = (TextView) findViewById(R.id.text2);
        TextView mText3 = (TextView) findViewById(R.id.text3);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf);

        // userinfo 받기
        mUserType = AccountActivity.mUserType;
        if (mUserType==1) {
            mCarType = AccountActivity.mCarType;
            mCarNumber = AccountActivity.mCarNumber;
            mCash = AccountActivity.mCarCash;
        }

        String[] car_str = getResources().getStringArray(R.array.carSpinnerArray);
        String[] city_str = getResources().getStringArray(R.array.citySpinnerArray);
        town_str = new ArrayList<String>(Arrays.asList("---구,군 선택---"));

        ArrayAdapter<String> mCarAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, car_str);
        final ArrayAdapter<String> mCityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, city_str);
        mTownAdapter = new ArrayAdapter<String>(SelectRegionActivity.this, android.R.layout.simple_spinner_dropdown_item, town_str);

        Spinner mCarSpinner = (Spinner) findViewById(R.id.vehicle);
        Spinner mCitySpinner = (Spinner) findViewById(R.id.city);
        Spinner mTownSpinner = (Spinner) findViewById(R.id.town);

        mCarSpinner.setAdapter(mCarAdapter);
        mCitySpinner.setAdapter(mCityAdapter);
        mTownSpinner.setAdapter(mTownAdapter);


        //도시 선택시 이벤트
        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                mcityPosition = position;
                if (position != 0) {
                    ShowTownSpinner mTownSpinner = new ShowTownSpinner();
                    mTownSpinner.execute(cityList.get(position - 1));
                } else {
                    town_str.clear();
                    town_str.add("---구,군 선택---");
                    mTownAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //차 종류 선택시 이벤트 (충전소 종류 식별자)
        mCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mStationType = "선택안함";
                } else if (position == 4) {
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
        });//차 종류 선택시 이벤트 (충전소 종류 식별자)
        mCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mStationType = "선택안함";
                } else if (position == 4) {
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

    public void mClick1(View v) {
        mTownSpinner = (Spinner) findViewById(R.id.town);
        Intent intent = new Intent(SelectRegionActivity.this, SearchStationActivity.class);

        if ((mcityPosition==0)||(mTownSpinner.getSelectedItem().toString().equals("---구,군 선택---"))) {
            Toast.makeText(SelectRegionActivity.this,"지역을 선택하세요",Toast.LENGTH_SHORT).show();
        }

        else if (mStationType.equals("선택안함")) {
            Toast.makeText(SelectRegionActivity.this,"차량을 선택하세요",Toast.LENGTH_SHORT).show();
        }

        else {

            startActivity(intent);
        }
    }


    private class ShowTownSpinner extends AsyncTask<String, Void, ArrayList<String>> {
        /* 시 선택에 따른 구, 군 리스트 받아오기 */

        UrlConnection url = new UrlConnection();
        ArrayList<String> items = new ArrayList();


        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                items = url.GetTown(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return items;
        }

        protected void onPostExecute(ArrayList<String> items) {
            town_str.clear();
            town_str.add("---구,군 선택---");
            for (int i = 0; i < items.size(); i++) {
                town_str.add(items.get(i));
            }
            mTownAdapter.notifyDataSetChanged();
        }
    }


}
