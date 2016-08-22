package com.example.eunji_mac.hackathon_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.*;

public class EVStationActivity  extends FragmentActivity implements
        OnMapReadyCallback,android.location.LocationListener {

    //Get userinfo by intent
    String mCarType;

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
    double lat, lon; // 위도 & 경도
    int i = 0;

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 5초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;

    private LatLng reportBadLocation;
    private LatLng stationNeedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_evstation);

        // 지도 객체 가져옴 (fragment로)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(EVStationActivity.this);


        mCarType = AccountActivity.mCarType;

        location = getLocation();
        stationNeedLocation = new LatLng(location.getLatitude(),location.getLongitude());

        ShowNearStation nearStation = new ShowNearStation();
        nearStation.execute(Double.toString(lat-0.08),Double.toString(lat+0.08),Double.toString(lon-0.08),Double.toString(lon+0.08),mCarType);


        //충전기 고장신고하기 버튼 클릭 이벤트
        final Button reportBadBtn = (Button)findViewById(R.id.bad_reportBtn);
        reportBadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reportBadLocation.equals(new LatLng(0,0))) {
                    Toast.makeText(EVStationActivity.this,"불량한 충전기가 있는 충전소를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EVStationActivity.this,"충전기 불량 신고가 정상적으로 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    //서버에 불량 충전기 업데이트 하기
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UrlConnection urlconn = new UrlConnection();
                            urlconn.PostReportBadnessStation(Double.toString(reportBadLocation.latitude),Double.toString(reportBadLocation.longitude));
                        }
                    }).start();

                }
            }
        });

        //충전소 필요 버튼 클릭 이벤트
        Button reportNeedBtn = (Button)findViewById(R.id.wish_stationBtn);
        reportNeedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EVStationActivity.this,"충전소 설치 요청을 접수하였습니다.", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UrlConnection urlcon = new UrlConnection();
                        urlcon.PostRequestBuildStation(Double.toString(stationNeedLocation.latitude),Double.toString(stationNeedLocation.longitude));
                    }
                }).start();


                //stationNeedLocation
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        /* 콜백 함수, 이걸로 map을 handle할 수 있다. */
        googleMap = map;
        pickMyLocation(googleMap);


    }
    public void pickMyLocation(GoogleMap map){

        // 현재 위치 받아오기

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(location.getLatitude(),location.getLongitude()))
                .fillColor(0x44ff0000)
                .strokeColor(0xffff0000)
                .strokeWidth(8)
                .radius(100);
        Circle circle = googleMap.addCircle(circleOptions);

        location = getLocation();
        my = googleMap.addMarker(new MarkerOptions().title("+")
                .position(new LatLng(location.getLatitude(),location.getLongitude()))
                .draggable(true));

        my.showInfoWindow();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                (new LatLng(location.getLatitude(),location.getLongitude()),10));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 13.0f ) );

        // Location Manager 선언
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Permission Checking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Fine location denied",Toast.LENGTH_LONG).show();
            return;
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


    }

    private class ShowNearStation extends AsyncTask<String, Void, ArrayList<String>> {
        /* 지역, 차종에 따른 검색 결과에 따른 충전소 보여주기 */
        ArrayList<String> mStation;
        ArrayList<String> mBadStation;

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            UrlConnection urlconn = new UrlConnection();

            try {
                mStation = urlconn.GetDropByStation(strings[0],strings[1],strings[2],strings[3],strings[4]);
                mBadStation = urlconn.GetReportBadnessStation();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mStation;
        }

        @Override
        protected void onPostExecute(ArrayList<String> items) {

            //마커 클릭 이벤트
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle().equals("+")) {
                        Toast.makeText(EVStationActivity.this,"햔재 위치 입니다.(주유소만 클릭 가능합니다.)",Toast.LENGTH_SHORT).show();
                    }
                    else if (marker.getTitle().equals("")){
                        reportBadLocation = marker.getPosition();
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        marker.setTitle("고장");
                    }
                    else {
                        reportBadLocation = new LatLng(0,0);
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        marker.setTitle("");
                    }
                    return false;
                }
            });

            // 찍혀져있던 마커 지우기
            for (int i=0;i<markers.size();i++){
                markers.get(i).remove();
            }
            // 새로운 검색 결과에 대한 마커 찍기
            JSONObject jo= null;
            for (int i=0;i<items.size();i++) {


                try {
                    jo = new JSONObject(items.get(i));
                    double lon = Double.parseDouble( jo.get("lon").toString());
                    double lat = Double.parseDouble(jo.get("lat").toString());


                    // 충정기의 불량 상태 파악
                    boolean flags = true; //false가 되면 점검상태!!
                    JSONObject jo2;
                    for (int j=0;j<mBadStation.size();j++) {
                        try {
                            jo2 = new JSONObject(mBadStation.get(j));
                            Log.e("111111",jo2.getString("lon"));
                            Log.e("222222",Double.toString(lon));
                            if ((jo2.getString("lon").equals(Double.toString(lon)))
                                    &&(jo2.getString("lat").equals(Double.toString(lat)))) {
                                flags = false;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    LatLng mLatlng = new LatLng(lat,lon);
                    Marker oneMarker ;
                    if (flags) {
                        oneMarker = googleMap.addMarker(new MarkerOptions()
                                .title("")
                                .position(mLatlng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .alpha(0.7f));
                        oneMarker.showInfoWindow();
                        markers.add(oneMarker);
                    }

                    else {
                        oneMarker = googleMap.addMarker(new MarkerOptions()
                                .title("점검")
                                .position(mLatlng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .alpha(0.7f));
                        oneMarker.showInfoWindow();
                        markers.add(oneMarker);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return new Location("-1");
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public void onStatusChanged(String provider, int status, Bundle extras) {}

}