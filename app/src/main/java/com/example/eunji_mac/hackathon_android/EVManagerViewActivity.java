package com.example.eunji_mac.hackathon_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Random;

public class EVManagerViewActivity extends FragmentActivity implements
        OnMapReadyCallback {

    public GoogleMap googleMap;
    LatLng mCenterLocation = new LatLng(37.514996, 127.063131);
    // Declare a variable for the cluster manager.
    ClusterManager<MyItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evmanager_view);
Log.e("S!","!@!@!@!@");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        /*
        ClusterManager<MyItem> clusterManager = new ClusterManager<MyItem>( this, googleMap );
        googleMap.setOnCameraChangeListener(clusterManager);

        double lat;
        double lng;
        Random generator = new Random();
        for( int i = 0; i < 1000; i++ ) {
            lat = generator.nextDouble() / 3;
            lng = generator.nextDouble() / 3;
            if( generator.nextBoolean() ) {
                lat = -lat;
            }
            if( generator.nextBoolean() ) {
                lng = -lng;
            }
            clusterManager.addItem( new MyItem( new LatLng( mCenterLocation.latitude + lat, mCenterLocation.longitude + lng ) ) );
        }
        */
Log.e("s","SSSSSSs");
        // Position the map.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, googleMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        Log.e("s","sssssssss");

        // Add cluster items (markers) to the cluster manager.
        addItems();

        setUpClusterer();
    }
    private void setUpClusterer() {


    }

    private void addItems() {
        Log.e("STARt","clustering");
        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
        Log.e("END","clustering");
    }
}
