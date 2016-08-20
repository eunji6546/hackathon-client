package com.example.eunji_mac.hackathon_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    ListViewAdapter mAdapter;
    ArrayList<String> town_str = null;
    String mStationType;
    Integer mcityPosition;

    // For GoogleMap
    // Marker titles, 충전소 순서대로 타이틀 붙여야함
    public String[] titles = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public GoogleMap googleMap;
    public List<Marker> markers = new ArrayList<Marker>();
    public List<Marker> my = new ArrayList<Marker>();

    // For GPS,  참고 http://techlovejump.com/android-gps-location-manager-tutorial/
    private LocationManager locationManager;
    public LatLng myLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);


        // 지도 객체 가져옴 (fragment로)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(SearchStationActivity.this);


        // Location Manager 선언
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Permission Checking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"Fine location denied",Toast.LENGTH_LONG).show();
            return;
        }

       android.location.LocationListener locationListener = new android.location.LocationListener() {
           @Override
           public void onLocationChanged(Location location) {
               //있었던 마커 지워줌
               List<Marker> my = new ArrayList<Marker>();
               Log.e("@@","@##############");

               String msg = "New Latitude: " + location.getLatitude() + "New Longitude: " + location.getLongitude();

               Marker myMarker = googleMap.addMarker( new MarkerOptions().title("Me").position(new LatLng(location.getLatitude(),location.getLongitude())));
               my.add(myMarker);

               Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

           }

           @Override
           public void onStatusChanged(String s, int i, Bundle bundle) {

           }

           @Override
           public void onProviderEnabled(String s) {

           }

           @Override
           public void onProviderDisabled(String s) {

           }
       };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1,locationListener);

        ListView mListview ;


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

        //현재 시간 및 날짜 받아오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        sdf.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = sdf.format(new Date());

        //도시 선택시 이벤트
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

        //차 종류 선택시 이벤트 (충전소 종류 식별자)
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

        //충전소 검색 버큰 클릭 이벤트
        mStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSearchedStation searchedStation = new ShowSearchedStation();
                searchedStation.execute(cityList.get(mcityPosition-1),mTownSpinner.getSelectedItem().toString(),mStationType);
            }
        });
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

        /*
            주유소 정보 listview에 띄우기
         */
        protected void onPostExecute(ArrayList<String> items) {
            mAdapter = new ListViewAdapter();
            try {
            for (int i=0;i<items.size();i++) {
                JSONObject jo = new JSONObject(items.get(i));
                mAdapter.addItem(jo.getString("address"),100,10);


                Log.e("******************", String.valueOf(i));
                Log.e("location",jo.getString("map"));
                Log.e("holiday",jo.getString("holiday"));
                Log.e("address",jo.getString("address"));

            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // 콜백 함수, 이걸로 map을 handle할 수 있다.
        googleMap = map;

    //    LatLng sydney = new LatLng(-34, 151);
     //   googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
      //  googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


}
