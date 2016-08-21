package com.example.eunji_mac.hackathon_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SearchStationActivity extends FragmentActivity implements OnMapReadyCallback,android.location.LocationListener {

    //Get userinfo by intent
    String mUserType; // 1 for driver, 0 for walker
    String mCarType;
    String mCarNumber;
    String mCash;

    //user가 선택한 지역
    String mCity;
    String mTown;
    String mStationType;

    ListViewAdapter mAdapter;

    // For GoogleMap
    // Marker titles, 충전소 순서대로 타이틀 붙여야함
    public String[] titles = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public GoogleMap googleMap;
    public List<Marker> markers = new ArrayList<Marker>();

    // For GPS,  참고 http://techlovejump.com/android-gps-location-manager-tutorial/
    private LocationManager locationManager;
    public LatLng myLocation;
    Marker my;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도
    int i = 0;

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 5초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;


    // Strings for parsing xml Document
    final String NODE_ROOT = "kml";
    final String NODE_DISTANCE = "tmap:totalDistance";
    final String NODE_TIME = "tmap:totalTime";
    final String NODE_FARE = "tmap:totalFare";
    final String NODE_TAXIFARE = "tmap:taxiFare";

    ArrayList<Integer> distances;
    ArrayList<Integer> seconds;
    ArrayList<String> items;

    android.os.Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_station);
        FrameLayout framelayout = (FrameLayout) findViewById(R.id.MapView);
        TMapView tMapView = new TMapView(this);
        framelayout.addView(tMapView);

        tMapView.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");

        TMapData tMapData = new TMapData();
        //TMapPoint startpoint = new TMapPoint(37.538958,127.028073);
        //TMapPoint endpoint = new TMapPoint(36.369608, 127.364014);

        final String NODE_ROOT = "kml";
        final String NODE_DISTANCE = "tmap:totalDistance";
        final String NODE_TIME = "tmap:totalTime";
        final String NODE_FARE = "tmap:totalFare";
        final String NODE_TAXIFARE = "tmap:taxiFare";

        Log.e("SS","SSSSSSS");
        tMapData.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {

                Log.e("SSSS","SSSSTTSSSS");
                XMLDOMParser parser = new XMLDOMParser();
                Document doc = document;
                // Get elements by name employee
                NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element e = (Element) nodeList.item(i);
                    Log.e("AAAAA",parser.getValue(e, NODE_DISTANCE));
                    Log.e("BBBB",parser.getValue(e, NODE_TIME));
                    Log.e("CCCC",parser.getValue(e, NODE_FARE));
                    Log.e("BBBB",parser.getValue(e, NODE_TAXIFARE));

                }

            }
        });
        Log.e("SS","SSSSSSSS");


        //TCalculator tCalculator = new TCalculator();
        //Log.e("SDADSFDAFDdfsAA", tCalculator.calculate(startpoint,endpoint));

        // 지도 객체 가져옴 (fragment로)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(SearchStationActivity.this);

        // userinfo 받기
        Intent intent = getIntent();
        mUserType = intent.getExtras().getString("usertype");

        if (mUserType.equals("1")) { // for driver
            mCarType = intent.getStringExtra("cartype");
            mCarNumber = intent.getStringExtra("carnumber");
            mCash = intent.getStringExtra("cash");
        }

        //지역 받기
        mCity = intent.getStringExtra("CITY");
        mTown = intent.getStringExtra("TOWN");
        mStationType = intent.getStringExtra("STATION_TYPE");

        //listview 생성
        ListView mListview ;
        mAdapter = new ListViewAdapter() ;
        mListview = (ListView) findViewById(R.id.stationlistview1);
        mListview.setAdapter(mAdapter);

        //현재 시간 및 날짜 받아오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        sdf.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = sdf.format(new Date());

        location = getLocation();
        Log.e("LLLLLOCA",location.toString());

         handler = new android.os.Handler(){

            public void handleMessage(Message message){

                distances = new ArrayList<>(markers.size());
                seconds = new ArrayList<>(markers.size());

                //TMapPoint startpoint = new TMapPoint(lat,lon);

                // 각 주유소마다 경유 시 소요 시간, 거리 계산
                for (int i=0; i<markers.size(); i++){
                    TMapData tMapData = new TMapData();
                    Log.e("Call","ssssS");
                    Log.e("LAT",String.valueOf(markers.get(i).getPosition().latitude));
                    Log.e("LAT", String.valueOf(markers.get(i).getPosition().longitude));
                    //Log.e("STR",startpoint.toString());

                    //TMapPoint endpoint = new TMapPoint(markers.get(i).getPosition().latitude,markers.get(i).getPosition().longitude);

                    TMapPoint startpoint = new TMapPoint(37.538958,127.028073);
                    TMapPoint endpoint = new TMapPoint(36.369608, 127.364014);

                    TCalculator tCalculator = new TCalculator();
                    tCalculator.calculate(startpoint, endpoint);
                    //Log.e("AAAAAAAA", tCalculator.calculate());



                }



                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });

                Log.e("markers",String.valueOf(markers.size()));
                Log.e("distances",String.valueOf(distances.size()));
                Log.e("seconds",String.valueOf(seconds.size()));
//
                // 주유소 리스트뷰 갱신
                mAdapter.clear();
                try {
                    for (int i=0;i<items.size();i++) {
                        JSONObject jo = new JSONObject(items.get(i));
                        location = getLocation();

                        //distance between station and my location[Km]
                        Calculate_Distance mDistance = new Calculate_Distance();
                        double distance =
                                mDistance.distance(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        Double.parseDouble(jo.getString("map").split(",")[0]),
                                        Double.parseDouble(jo.getString("map").split(",")[1]),"K");

                       // mAdapter.addItem(jo.getString("address"),seconds.get(i)/60,distances.get(i));

                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ShowSearchedStation searchedStation = new ShowSearchedStation();
        searchedStation.execute(mCity,mTown,mStationType);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        /* 콜백 함수, 이걸로 map을 handle할 수 있다. */
        googleMap = map;
        pickMyLocation(googleMap);

    }

    public void pickMyLocation(GoogleMap map){

        // 현재 위치 받아오기
        location = getLocation();
        my = googleMap.addMarker(new MarkerOptions().title("ME").position(new LatLng(location.getLatitude(),location.getLongitude())));
        my.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),10));


        // Location Manager 선언
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Permission Checking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Fine location denied",Toast.LENGTH_LONG).show();
            return;
        }

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1,this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


    }

    private class ShowSearchedStation extends AsyncTask<String, Void, ArrayList<String>> {
        /* 지역, 차종에 따른 검색 결과에 따른 충전소 보여주기 */
        ArrayList<String> mStation;
        ArrayList<LatLng> mPosition = new ArrayList<LatLng>();

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            UrlConnection urlconn = new UrlConnection();

            try {
                mStation = urlconn.GetSupply(strings[0],strings[1],strings[2]);
                for (int i=0;i<mStation.size();i++) {
                    JSONObject jo= new JSONObject(mStation.get(i));
                    String pos = jo.getString("map");
                    String[] poss = pos.split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(poss[0]),Double.parseDouble(poss[1]));
                    mPosition.add(latLng);
                }



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            items = mStation;
            return mStation;
        }

        /*
            주유소 정보 listview에 띄우기
         */
        protected void onPostExecute(ArrayList<String> items) {
            // 찍혀져있던 마커 지우기
            for (int i=0;i<markers.size();i++){
                markers.get(i).remove();
            }
            // 새로운 검색 결과에 대한 마커 찍기
            for (int i=0; i<mPosition.size(); i++){

                Marker oneMarker = googleMap.addMarker(new MarkerOptions().position(mPosition.get(i)).title(titles[i]));

                oneMarker.showInfoWindow();

                markers.add(oneMarker);
            }

            // 모든 마커를 다 보여줄 수 있도록 카메라 업데이트

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 40; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
            handler.sendEmptyMessage(0);


        }
    }


    public Location getLocation() {
        /* 현재 위치를 받아오는 함수 */

        try {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현


            } else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return new Location("-1");
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {

                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                        }
                    }
                }


                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS() {
        /* GPS 종료 */
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return ;
            }
            locationManager.removeUpdates(this);
        }
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }




    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",Toast.LENGTH_SHORT).show();
    }

    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Gps is turned on!! ",Toast.LENGTH_SHORT).show();
    }

    /* 위치가 바뀌었을 때 동작하는 함수 */
    @Override
    public void onLocationChanged(Location location) {

        //remove current marker
        my.remove();

        my = googleMap.addMarker( new MarkerOptions().title("ME").position(new LatLng(location.getLatitude(),location.getLongitude())));
        my.showInfoWindow();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {


    }
}
