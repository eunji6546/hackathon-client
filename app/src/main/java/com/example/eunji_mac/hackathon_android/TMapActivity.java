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
        mMapView.setZoomLevel(4);
        //mMapView.setLocationPoint(126.985022,37.566474);
        //mMapView.setLocationPoint(127.350827,36.367394);

        Intent intent = getIntent();
        ArrayList<String> mNames = intent.getStringArrayListExtra("mNameList");
        ArrayList<String> mPoints = intent.getStringArrayListExtra("mPointList");
        String keyword = intent.getStringExtra("mKey");

        ArrayList<TMapPoint> tPoints = new ArrayList<TMapPoint>(mNames.size());

        for (int i=0; i<mNames.size(); i++){

            Double lat, lon;
            lat = Double.parseDouble(mPoints.get(i).toString().split("/")[0]);
            lon = Double.parseDouble(mPoints.get(i).toString().split("/")[1]);
            TMapPoint one = new TMapPoint(lat,lon);
            tPoints.add(one);

            TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
            tMapMarkerItem.setTMapPoint(one);
            tMapMarkerItem.setCalloutTitle(mNames.get(i));
            tMapMarkerItem.setAutoCalloutVisible(true);
            Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.poi_dot);
            tMapMarkerItem.setIcon(icon);
            mMapView.addMarkerItem(mNames.get(i),tMapMarkerItem);

        }
        TMapInfo tMapInfo = mMapView.getDisplayTMapInfo(tPoints);
        TMapPoint center = tMapInfo.getTMapPoint();
        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
        tMapMarkerItem.setTMapPoint(center);
        tMapMarkerItem.setCalloutTitle(keyword);
        tMapMarkerItem.setAutoCalloutVisible(true);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.poi_star);
        tMapMarkerItem.setIcon(icon);

        mMapView.setLocationPoint(center.getLongitude(),center.getLatitude());
        mMapView.setCenterPoint(center.getLongitude(),center.getLatitude());
        mMapView.setZoomLevel(tMapInfo.getTMapZoomLevel());
        Log.e("g", String.valueOf(tMapInfo.getTMapZoomLevel()));

        frameLayout.addView(mMapView);


    }

}
