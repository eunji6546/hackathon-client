package com.example.eunji_mac.hackathon_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class WholeMapactivity extends FragmentActivity implements
        OnMapReadyCallback {

    public GoogleMap mMap;
    Marker my;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_mapactivity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map4);
        mapFragment.getMapAsync(WholeMapactivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(36.667900, 127.895345),7));
        Log.e("WHOLE","inside");
        pickWholeMarkers();
    }

    public void pickWholeMarkers(){

        new AsyncTask<Void,Void,ArrayList<LatLng>>(){

            @Override
            protected ArrayList<LatLng> doInBackground(Void... voids) {

                UrlConnection urlconn = new UrlConnection();
                ArrayList<String> response = urlconn.GetRequestBuildStation();
                ArrayList<LatLng> positions = null;
                if (response == null){
                    Toast.makeText(WholeMapactivity.this, "아직 아무 충전요청이 없음",Toast.LENGTH_LONG).show();
                }else {
                    positions = new ArrayList<LatLng>();

                    for (int i=0; i<response.size(); i++){
                        String oneRes = response.get(i);
                        Log.e("WHOLE",oneRes);
                        JSONObject position = null;
                        try {
                            position = new JSONObject(oneRes);

                            Double lat = Double.parseDouble(position.getString("lat"));
                            Double lon = Double.parseDouble(position.getString("lon"));

                            LatLng onePos = new LatLng(lat,lon);

                            positions.add(onePos);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                return positions;
            }

            @Override
            protected void onPostExecute(ArrayList<LatLng> positions) {

                Log.e("WHWH", positions.toString());
                for (int i = 0; i < positions.size(); i++) {

                    my = mMap.addMarker(new MarkerOptions().position(positions.get(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .alpha(0.5f));
                    my.showInfoWindow();

                }

            }

        }.execute();
    }
}
