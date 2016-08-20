package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
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
    String mUserType; // 1 for driver, 0 for walker
    String mCarType;
    String mCarNumber;
    String mCash;

    //For searching location of station
    ArrayAdapter<String> mTownAdapter = null;
    ArrayList<String> town_str = null;
    String mStationType;
    Integer mcityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);

        // userinfo 받기
        Intent intent = getIntent();
        mUserType = intent.getExtras().getString("usertype");

        if (mUserType.equals("1")) { // for driver
            mCarType = intent.getExtras().getString("cartype");
            mCarNumber = intent.getExtras().getString("carnumber");
            mCash = intent.getExtras().getString("cash");
        }

        final List<String> cityList = new ArrayList<String>(Arrays.asList("강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "서울", "울산", "인천", "전남", "전북", "제주", "충남", "충북"));
        String[] car_str = getResources().getStringArray(R.array.carSpinnerArray);
        String[] city_str = getResources().getStringArray(R.array.citySpinnerArray);
        town_str = new ArrayList<String>(Arrays.asList("---구,군 선택---"));

        ArrayAdapter<String> mCarAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, car_str);
        final ArrayAdapter<String> mCityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, city_str);
        mTownAdapter = new ArrayAdapter<String>(SelectRegionActivity.this, android.R.layout.simple_spinner_dropdown_item, town_str);

        Spinner mCarSpinner = (Spinner) findViewById(R.id.vehical);
        final Spinner mCitySpinner = (Spinner) findViewById(R.id.city);
        final Spinner mTownSpinner = (Spinner) findViewById(R.id.town);
        final Button mStationBtn = (Button) findViewById(R.id.searchstation);

        mCarSpinner.setAdapter(mCarAdapter);
        mCitySpinner.setAdapter(mCityAdapter);
        mTownSpinner.setAdapter(mTownAdapter);

        //현재 시간 및 날짜 받아오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        sdf.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = sdf.format(new Date());


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
        });

        //충전소 검색 버큰 클릭 이벤트
        mStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SelectRegionActivity.this, SearchStationActivity.class);

                if ((mcityPosition==0)||(mTownSpinner.getSelectedItem().toString().equals("---구,군 선택---"))) {
                    Toast.makeText(SelectRegionActivity.this,"지역을 선택해 주세요.",Toast.LENGTH_SHORT).show();
                }

                else if (mStationType.equals("선택안함")) {
                    Toast.makeText(SelectRegionActivity.this,"차량을 선택헤 주세요.",Toast.LENGTH_SHORT).show();
                }

                else {
                    intent.putExtra("CITY", cityList.get(mcityPosition - 1));
                    intent.putExtra("TOWN", mTownSpinner.getSelectedItem().toString());
                    intent.putExtra("STATION_TYPE", mStationType);
                    intent.putExtra("usertype", mUserType);

                    if (mUserType.equals("1")) { // for driver
                        intent.putExtra("carnumber", mCarNumber);
                        intent.putExtra("cartype", mCarType);
                        intent.putExtra("cash", mCash);
                    }
                    startActivity(intent);
                }
            }
        });

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
