package com.example.eunji_mac.hackathon_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;

public class AlertActivity extends AppCompatActivity implements android.location.LocationListener {

    // For GPS,  참고 http://techlovejump.com/android-gps-location-manager-tutorial/
    private LocationManager locationManager;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    boolean isGetLocation = false;

    Location location;
    double lat, lon;

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 5초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
//        mp.start();

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

        //최초 gps값 받아오기
        location = getLocation();
        mText2.setText("Your Location \n" +
                "위도 : "+location.getLatitude() + "\n" +
                "경도 : "+location.getLongitude());

        String[] params = new String[2];

        params[0] = Double.toString(location.getLatitude());
        params[1] = Double.toString(location.getLongitude());

        ShowEmergency mEmergency = new ShowEmergency();
        mEmergency.execute(params);

    }

    // report 버튼 클릭 시
    public void mClick1(View view) {
        Log.v("Location is posted : ", "true");

        String[] params = new String[2];

        params[0] = Double.toString(location.getLatitude());
        params[1] = Double.toString(location.getLongitude());

        PostEmergency mEmergency = new PostEmergency();
        mEmergency.execute(params);
    }

    public void mClickHome(View view) {}


    // 현재 위치 return
    public Location getLocation() {
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
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onLocationChanged(Location location) {
        TextView mText2 = (TextView) findViewById(R.id.text2);
        mText2.setText("Your Location \n" +
                "위도 : "+location.getLatitude() + "\n" +
                "경도 : "+location.getLongitude());

        Log.v("Location is changed : ", "true");

        String[] params = new String[2];

        params[0] = Double.toString(location.getLatitude());
        params[1] = Double.toString(location.getLongitude());

        ShowEmergency mEmergency = new ShowEmergency();
        mEmergency.execute(params);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    private class ShowEmergency extends AsyncTask< String[], Void, Boolean> {

        @Override
        protected Boolean doInBackground(String[]... params) {
            try {
                UrlConnection urlconn = new UrlConnection();
                Log.v("current latitude : ", params[0][0]);
                Log.v("current longitude", params[0][1]);
                return urlconn.Getalert(params[0][0], params[0][1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean existence) {
            TextView mText4 = (TextView) findViewById(R.id.text4);

            if (existence) {
                mText4.setText("Status  :  Caution!!");
            } else {
                mText4.setText("Status  :  Normal");
            }
        }
    }

    private class PostEmergency extends AsyncTask< String[], Void, Void> {

        @Override
        protected Void doInBackground(String[]... params) {
            try {
                UrlConnection urlconn = new UrlConnection();
                Log.v("current latitude : ", params[0][0]);
                Log.v("current longitude", params[0][1]);
                urlconn.Reportnow(params[0][0], params[0][1]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void params) {
            TextView mText4 = (TextView) findViewById(R.id.text4);
            Toast.makeText(AlertActivity.this, "보고해주셔서 감사합니다", Toast.LENGTH_SHORT).show();
            if ((mText4.getText().toString()).equals("Status  :  Normal"))
                mText4.setText("Status  :  Caution!!");
            else
                mText4.setText("Status  :  Normal");
        }
    }
}
