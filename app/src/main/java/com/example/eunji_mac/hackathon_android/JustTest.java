package com.example.eunji_mac.hackathon_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.skp.Tmap.TMapView;

public class JustTest extends AppCompatActivity {
    TMapView tmapview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just_test);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.MapView);

        tmapview = new TMapView(this);

        frameLayout.addView(tmapview);

        tmapview.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(10);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(true);
        tmapview.setTrackingMode(true);
        tmapview.setLocationPoint(127.350827,36.367394);




    }

}
