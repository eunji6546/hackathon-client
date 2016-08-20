package com.example.eunji_mac.hackathon_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap googleMap;
    public Marker[] Markers;
    String serverKey = "AIzaSyC9eMo7RBS9Bz-6pUm6ek_rQUXrJLrKbko";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 지도 객체 가져옴 (fragment로)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap map) {
        // 콜백 함수, 이걸로 map을 handle할 수 있다.
        googleMap = map;

        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        try{
            new HttpUtil().execute("36.337155", "127.398756", "36.336792", "127.402693");

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public class HttpUtil extends AsyncTask<String, Void, Void> {
        @Override
        public Void doInBackground(String... params) {
            try {
                String url = "https://apis.skplanetx.com/tmap/multiViaPointRoute?";
                //version={version}&endX={endX}&endY={endY}&endRpFlag={endRpFlag}
                // //&endPoiId={endPoiId}&reqCoordType={reqCoordType}&startX={startX}
                // //&startY={startY}&passList={passList}&resCoordType={resCoordType}&callback={callback}";
                url += "origin=" + params[0].toString() + "/" + params[1].toString();
                url += "&destination=" + params[2].toString() + "/" + params[3].toString();
                url += "&key=" + serverKey;
                url += "&mode=driving";
                Log.e("URL",url);
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                Log.e("PST","POSREQ");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");

                byte[] outputInBytes = params[0].getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                Log.e("OUT","POSREQ");

                os.write( outputInBytes );
                os.close();

                int retCode = conn.getResponseCode();

                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = br.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                br.close();

                String res = response.toString();
                Log.e("RES",res);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }
    }


}
