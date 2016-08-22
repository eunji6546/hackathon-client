package com.example.eunji_mac.hackathon_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PayActivity extends FragmentActivity implements
        OnMapReadyCallback,android.location.LocationListener {

    //Get userinfo by intent
    int mUserType; // 1 for driver, 0 for walker
    String mCarType, mCarNumber, mCash;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        // 지도 객체 가져옴 (fragment로)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map3);
        mapFragment.getMapAsync(PayActivity.this);

        // userinfo 받기
        Intent intent = getIntent();
        mUserType = AccountActivity.mUserType;
        mCarType = AccountActivity.mCarType;
        mCarNumber = AccountActivity.mCarNumber;
        mCash = AccountActivity.mCarCash;

        location = getLocation();

        SearchStationForPay mStation = new SearchStationForPay();
        mStation.execute(Double.toString(lat-0.3),Double.toString(lat+0.3),Double.toString(lon-0.3),Double.toString(lon+0.3),mCarType);
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
        my = googleMap.addMarker(new MarkerOptions().title("ME").
                position(new LatLng(location.getLatitude(),location.getLongitude())));
        my.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                (new LatLng(location.getLatitude(),location.getLongitude()),10));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
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
        my = googleMap.addMarker( new MarkerOptions().title("ME").position(
                new LatLng(location.getLatitude(),location.getLongitude())));
        my.showInfoWindow();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    private class SearchStationForPay extends AsyncTask<String, Void, ArrayList<String>> {
        /* 지역, 차종에 따른 검색 결과에 따른 충전소 보여주기 */
        ArrayList<String> mStation;

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            UrlConnection urlconn = new UrlConnection();

            try {
                mStation = urlconn.GetDropByStation(strings[0],strings[1],strings[2],strings[3],strings[4]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("mStation",mStation.toString());
            return mStation;
        }

        @Override
        protected void onPostExecute(ArrayList<String> items) {

            // 찍혀져있던 마커 지우기
            for (int i=0;i<markers.size();i++){
                markers.get(i).remove();
            }
            // 새로운 검색 결과에 대한 마커 찍기
            for (int i=0;i<items.size();i++) {
                JSONObject jo= null;
                try {
                    jo = new JSONObject(items.get(i));
                    double lon = Double.parseDouble( jo.get("lon").toString());
                    double lat = Double.parseDouble(jo.get("lat").toString());
                    LatLng mLatlng = new LatLng(lat,lon);
                    Marker oneMarker = googleMap.addMarker(new MarkerOptions().position(mLatlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .alpha(0.7f)
                    .title(""));


                    oneMarker.showInfoWindow();

                    markers.add(oneMarker);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        if (marker.getTitle().equals("ME")){
                            return false;
                        }else {
                            marker.setTitle("Here!");


                            final EditText cashInput = new EditText(PayActivity.this);
                            cashInput.setHint("10000 (숫자만 입력하세요)");
                            AlertDialog alert = new AlertDialog.Builder(PayActivity.this)
                                    .setTitle("PAY CASH")
                                    .setMessage(String.format("현재 보유 캐쉬 : %s원\n지불할 금액을 입력하세요", AccountActivity.mCarCash))
                                    .setView(cashInput)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 서버에 지불한 금액 날리기
                                            String[] params = new String[5];
                                            String mChargeCash = "-"+cashInput.getText().toString();

                                            int mSumString =
                                                    Integer.parseInt(mChargeCash) + Integer.parseInt(AccountActivity.mCarCash);
                                            if (mSumString<0){
                                                // 진행 불가
                                                Toast.makeText(PayActivity.this,
                                                        String.format("%d 원의 캐쉬를 보유하고 있습니다.\n그 이하의 금액을 지불할 수 있습니다.",Integer.parseInt(AccountActivity.mCarCash))
                                                        ,Toast.LENGTH_LONG).show();
                                            }else{

                                                Log.v("Car Cash is updated", AccountActivity.mCarCash);

                                                AccountActivity.mCarCash = String.valueOf(mSumString);

                                                params[0] = AccountActivity.mCarNumber;
                                                params[1] = AccountActivity.mCarType;
                                                params[2] = AccountActivity.mCarCash;
                                                params[3] = String.valueOf(marker.getPosition().latitude);
                                                params[4] = String.valueOf(marker.getPosition().longitude);

                                                ShowNewMoney mGetMoney = new ShowNewMoney();
                                                mGetMoney.execute(params);

                                            }
                                        }
                                    }).show();
                            return false;
                        }

                    }
                });
            }
        }
    }
    private class ShowNewMoney extends AsyncTask< String, Void, String> {

        UrlConnection urlconn = new UrlConnection();


        @Override
        protected String doInBackground(String... params) {
            try {
                Log.v("first element", params[0]);
                Log.v("second element", params[1]);
                Log.v("third element", params[2]);
                urlconn.Pay(params[0], params[1], params[2], params[3], params[4]);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return params[2];

        }

        protected void onPostExecute(String mSumString) {

            Toast.makeText(PayActivity.this, String.format("결제 성공! 남은 캐쉬는 %s 원입니다.", AccountActivity.mCarCash),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PayActivity.this, MenuActivity.class);
            startActivity(intent);
        }
    }
}