package com.example.eunji_mac.hackathon_android;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SearchStationActivity extends FragmentActivity implements OnMapReadyCallback {
    ArrayAdapter<String> mTownAdapter = null;
    ArrayList<String> town_str = null;
    String mStationType;
    Integer mcityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);


        // 지도 객체 가져옴 (fragment로)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(SearchStationActivity.this);

        ListView mListview ;
        ListViewAdapter mAdapter;

        mAdapter = new ListViewAdapter() ;

        mListview = (ListView) findViewById(R.id.stationlistview1);
        mListview.setAdapter(mAdapter);

        final List<String> cityList = new ArrayList<String>(Arrays.asList("강원","경기","경남","경북","광주","대구","대전","부산","서울","울산","인천","전남","전북","제주","충남","충북"));
        String[] car_str=getResources().getStringArray(R.array.carSpinnerArray);
        String[] city_str=getResources().getStringArray(R.array.citySpinnerArray);
        town_str = new ArrayList<String>(Arrays.asList("---구,군 선택---"));

        ArrayAdapter<String> mCarAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,car_str);
        final ArrayAdapter<String> mCityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,city_str);
        mTownAdapter = new ArrayAdapter<String>(SearchStationActivity.this,android.R.layout.simple_spinner_dropdown_item,town_str);

        Spinner mCarSpinner = (Spinner)findViewById(R.id.vehical);
        final Spinner mCitySpinner = (Spinner)findViewById(R.id.city);
        final Spinner mTownSpinner = (Spinner)findViewById(R.id.town);
        final Button mStationBtn = (Button)findViewById(R.id.searchstation);

        mCarSpinner.setAdapter(mCarAdapter);
        mCitySpinner.setAdapter(mCityAdapter);
        mTownSpinner.setAdapter(mTownAdapter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        sdf.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = sdf.format(new Date());
        Log.e("seconds~~~~~~~~~~~~~~~~", currentDateandTime);



        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                mcityPosition = position;
                if (position!=0) {
                    ShowTownSpinner mTownSpinner = new ShowTownSpinner();
                    mTownSpinner.execute(cityList.get(position-1));
                }
                else {
                    town_str.clear();
                    town_str.add("---구,군 선택---");
                    mTownAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==4) {
                    mStationType = "상";
                }
                else if ((position==1)||(position==6)) {
                    mStationType = "콤보";
                }
                else {
                    mStationType = "차데모";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSearchedStation searchedStation = new ShowSearchedStation();
                searchedStation.execute(cityList.get(mcityPosition-1),mTownSpinner.getSelectedItem().toString(),mStationType);
            }
        });


        // 첫 번째 아이템 추가.
        mAdapter.addItem("서울",100,10);
        // 두 번째 아이템 추가.
        mAdapter.addItem("부산",200,20);
        // 세 번째 아이템 추가.
        mAdapter.addItem("광주",300,30);
    }
    private class ShowTownSpinner extends AsyncTask<String, Void, ArrayList<String>> {

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

    private class ShowSearchedStation extends AsyncTask<String, Void, ArrayList<String>> {
        ArrayList<String> mStation;


        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            Log.e("%%%%%%%","++++++++++");
            UrlConnection urlconn = new UrlConnection();
            try {
                mStation = urlconn.GetSupply(strings[0],strings[1],strings[2]);
                Log.e("%%%%%%%",mStation.toString());
                for (int i=0;i<mStation.size();i++) {
                    JSONObject jo= new JSONObject(mStation.get(i));
                    Log.e("******************", String.valueOf(i));
                    Log.e("location",jo.getString("map"));
                    Log.e("holiday",jo.getString("holiday"));
                    Log.e("address",jo.getString("address"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mStation;
        }

        protected void onPostExecute(ArrayList<String> items) {
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // 콜백 함수, 이걸로 map을 handle할 수 있다.
        //googleMap = map;

        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
