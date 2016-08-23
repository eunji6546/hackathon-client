package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class TMapActivity extends AppCompatActivity {

    TMapView mMapView = null;

    TextView keywordView;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mMapView);

        mMapView = new TMapView(this);


        //mContext = this;

        mMapView = new TMapView(this);
        mMapView.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);


        //mMapView.setCompassMode(true);
        mMapView.setTrackingMode(true);
        mMapView.setZoomLevel(15);
        //mMapView.setLocationPoint(126.985022,37.566474);
        //mMapView.setLocationPoint(127.350827,36.367394);

        Intent intent = getIntent();
        ArrayList<String> mNames = intent.getStringArrayListExtra("mNameList");
        ArrayList<String> mPoints = intent.getStringArrayListExtra("mPointList");
        keyword = intent.getStringExtra("mKey");
       // String[] temps = intent.getStringExtra("mMyPoint").split("/");
        String[] temps1 = intent.getStringExtra("mStation").split("/");

        //TMapPoint myPos = new TMapPoint(Double.parseDouble(temps[0]),Double.parseDouble(temps[1]));
        LatLng temp = PayActivity.my.getPosition();
        TMapPoint myPos = new TMapPoint(temp.latitude,temp.longitude);
        TMapPoint evPos = new TMapPoint(Double.parseDouble(temps1[0]),
                Double.parseDouble(temps1[1]));

        keywordView = (TextView)findViewById(R.id.mKeyword);
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        keywordView.setText(String.format("Keyword : %s",keyword));
                    }
                });
            }
        }.start();


        TMapMarkerItem myItem = new TMapMarkerItem();
        Log.e("TMAP",myPos.toString());
        myItem.setTMapPoint(myPos);
        myItem.setCalloutTitle("내위치");
        myItem.setAutoCalloutVisible(true);
        Log.e("TMAP",myPos.toString());



        myItem.setIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.poi_dot));
        mMapView.addMarkerItem("내위치",myItem);


        TMapMarkerItem evItem = new TMapMarkerItem();
        evItem.setTMapPoint(evPos);
        evItem.setCalloutTitle("주유소");
        evItem.setAutoCalloutVisible(true);

        Bitmap b =BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.flat_location_icon);
        Bitmap resized = Bitmap.createScaledBitmap(b,128, 128, true);
        evItem.setIcon(resized);
        mMapView.addMarkerItem("주유소",evItem);

        ArrayList<TMapPoint> tPoints = new ArrayList<TMapPoint>(mNames.size());

        for (int i=0; i<mNames.size(); i++){

            Double lat, lon;
            lat = Double.parseDouble(mPoints.get(i).toString().split("/")[0]);
            lon = Double.parseDouble(mPoints.get(i).toString().split("/")[1]);
            TMapPoint one = new TMapPoint(lat,lon);
            tPoints.add(one);

        }
        TMapInfo tMapInfo = mMapView.getDisplayTMapInfo(tPoints);
        TMapPoint center = tMapInfo.getTMapPoint();
        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
        tMapMarkerItem.setTMapPoint(center);
        Log.e("Center is",center.getLatitude()+"/"+center.getLongitude());
        tMapMarkerItem.setCalloutTitle(keyword);
        tMapMarkerItem.setAutoCalloutVisible(true);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.poi_star);
        tMapMarkerItem.setIcon(icon);


        mMapView.setLocationPoint(center.getLongitude(),center.getLatitude());
        //mMapView.setCenterPoint(center.getLongitude(),center.getLatitude());
        //mMapView.setZoomLevel(tMapInfo.getTMapZoomLevel());
        Log.e("g", String.valueOf(tMapInfo.getTMapZoomLevel()));

        for (int i =0; i<mNames.size(); i++){

            TMapMarkerItem tMapMarkerItem1 = new TMapMarkerItem();
            tMapMarkerItem1.setTMapPoint(tPoints.get(i));
            tMapMarkerItem1.setCalloutTitle(mNames.get(i));
            tMapMarkerItem1.setAutoCalloutVisible(true);
            Bitmap icon1 = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.poi_dot);
            tMapMarkerItem1.setIcon(icon1);
            mMapView.addMarkerItem(mNames.get(i),tMapMarkerItem1);
        }

        frameLayout.addView(mMapView);


    }

}
