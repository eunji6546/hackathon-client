package com.example.eunji_mac.hackathon_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.plus.Account;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

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
    static Marker my;

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


    TMapView tmapview = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        // 장소 키워드 검색을 위한 티맵
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.MapView);

        tmapview = new TMapView(this);

        frameLayout.addView(tmapview);

        tmapview.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLocationPoint(127.350827,36.367394);



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

                    String title="fast";
                    if (jo.has("charge") && jo.get("charge").equals("완속")){
                        //클릭한 마커, 즉 충전소가 완속일 경우
                        title = "slow";
                    }
                    Marker oneMarker = googleMap.addMarker(new MarkerOptions().position(mLatlng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .alpha(0.7f)
                            .title(title));


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
                                            String[] params = new String[7];
                                            String mChargeCash = "-" + cashInput.getText().toString();

                                            String mSum =
                                                    String.valueOf(Integer.parseInt(AccountActivity.mCarCash)
                                                            + Integer.parseInt(mChargeCash));

                                            if (Integer.parseInt(mSum) < 0){
                                                // 진행 불가
                                                Toast.makeText(PayActivity.this,
                                                        String.format("%d 원의 캐쉬를 보유하고 있습니다.\n그 이하의 금액을 지불할 수 있습니다.",Integer.parseInt(AccountActivity.mCarCash))
                                                        ,Toast.LENGTH_LONG).show();
                                            }else{
                                                AccountActivity.mCarCash = mSum;

                                                Log.v("Car Cash is updated", AccountActivity.mCarCash);

                                                params[0] = AccountActivity.mCarNumber;
                                                params[1] = AccountActivity.mCarType;
                                                params[2] = mChargeCash;
                                                params[3] = String.valueOf(marker.getPosition().latitude);
                                                params[4] = String.valueOf(marker.getPosition().longitude);

                                                params[5] = marker.getPosition().latitude+"/"+marker.getPosition().longitude;
                                                params[6] = marker.getTitle();

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
    private class ShowNewMoney extends AsyncTask< String[], Void, String> {

        UrlConnection urlconn = new UrlConnection();


        @Override
        protected String doInBackground(String[]... params) {
            try {
                Log.v("DOINBACK : ", params[0][0]);
                Log.v("DOINBACK : ", params[0][1]);
                Log.v("DOINBACK : ", params[0][2]);
                urlconn.Save(params[0][0], params[0][1], params[0][2]);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //    토탈금액 & 경도/위도 & slow or fast
            return AccountActivity.mCarCash+"&"+params[0][5]+"&"+params[0][6];

        }

        protected void onPostExecute(String mResult) {

            String mStationType = mResult.split("&")[2];
            String[] split = mResult.split("&")[1].split("/");
            final String lat, lon;
            lat = split[0];
            lon = split[1];
            String mType = "완속";
            if (mStationType.equals("fast")){
                mType = "급속";
            }

            Toast.makeText(PayActivity.this, String.format("결제 성공! 남은 캐쉬는 %s 원입니다.",
                    AccountActivity.mCarCash),Toast.LENGTH_LONG).show();

            final EditText Keyword = new EditText(PayActivity.this);

            AlertDialog alert = new AlertDialog.Builder(PayActivity.this)
                    .setTitle("장소 추천")
                    .setMessage(String.format("%s충전소입니다.\n충전소 주변 장소 키워드를 입력하세요.\n 예)카페 or 편의점", mType))
                    .setView(Keyword)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(PayActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    }).setPositiveButton("Search",  new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            final String keyword = Keyword.getText().toString();
                            // 이거 빈거 예외 처리 ?????


                            // 장소 검색

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    // 일단 급속이라고 가정
                                    final TMapPoint station = new TMapPoint(Double.parseDouble(lat),Double.parseDouble(lon));
                                    final TMapData tMapData = new TMapData();
                                    try {
                                        ArrayList<TMapPOIItem> POIItem = tMapData.findAroundNamePOI(station, "카페");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ParserConfigurationException e) {
                                        e.printStackTrace();
                                    } catch (SAXException e) {
                                        e.printStackTrace();
                                    }

                                    tMapData.findAroundNamePOI(station, keyword, new TMapData.FindAroundNamePOIListenerCallback()
                                    {
                                        @Override
                                        public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {

                                            if (arrayList==null) {
                                                return;
                                            }
                                            ArrayList<String> points = new ArrayList<String>(arrayList.size());
                                            ArrayList<TMapPoint> tpoints = new ArrayList<TMapPoint>(arrayList.size());
                                            ArrayList<String> names = new ArrayList<String>(arrayList.size());

                                            for (int i=0; i<arrayList.size(); i++){
                                                TMapPOIItem item = arrayList.get(i);
                                                String mName = item.getPOIName().toString();
                                                String mPoint = item.getPOIPoint().toString();
                                                TMapPoint tMapPoint = item.getPOIPoint();
                                                Log.e("RESULT", "POI Name: " + mName + ", "
                                                        + "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                                                        "Point: " + mPoint);
                                                points.add(tMapPoint.getLatitude()+"/"+tMapPoint.getLongitude());
                                                names.add(mName);

                                                //add(tMapPoint,mName);
                                                /*
                                                googleMap.addMarker( new MarkerOptions()
                                                        .position(new LatLng(tMapPoint.getLatitude(),tMapPoint.getLongitude()))
                                                        .title(mName)
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                                        */
/*
                                                TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
                                                tMapMarkerItem.setTMapPoint(tMapPoint);
                                                tMapMarkerItem.setCalloutTitle(mName);
                                                tMapMarkerItem.setAutoCalloutVisible(true);
                                                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.map_pin_red);
                                                tMapMarkerItem.setIcon(icon);
                                                tmapview.addMarkerItem(mName,tMapMarkerItem);
*/
                                            }
                                            /*
                                            TMapInfo tMapInfo = tmapview.getDisplayTMapInfo(tpoints);
                                            TMapPoint center = tMapInfo.getTMapPoint();

                                            tmapview.setLocationPoint(center.getLongitude(),center.getLatitude());
                                            tmapview.setCenterPoint(center.getLongitude(),center.getLatitude());
                                            tmapview.setZoomLevel(tMapInfo.getTMapZoomLevel());
                                            */



                                            //tMapPoint.getLatitude()+"*"+tMapPoint.getLongitude()

                                            Intent intent = new Intent(PayActivity.this,TMapActivity.class);
                                            intent.putExtra("mPointList",points);
                                            intent.putExtra("mNameList",names);
                                            intent.putExtra("mKey",keyword);
//                                            intent.putExtra("mMyPoint", my.getPosition().latitude+"/"+my.getPosition().longitude);
                                            intent.putExtra("mStation",station.getLatitude()+"/"+station.getLongitude());
                                            startActivity(intent);

                                        }
                                    });


                            /*

                             */

                                    return null;
                                }
                            }.execute();


                        }
                    }).show();



        }
    }
    public void add(TMapPoint tMapPoint,String mName){
        googleMap.addMarker( new MarkerOptions()
                .position(new LatLng(tMapPoint.getLatitude(),tMapPoint.getLongitude()))
                .title(mName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


    }
}