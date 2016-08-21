package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class PathGuideActivity2 extends AppCompatActivity {


    private TMapView mMapView = null;
    ArrayList<String> items;
    Bitmap bitmap = null;
    // For GoogleMap
    // Marker titles, 충전소 순서대로 타이틀 붙여야함
    public String[] titles = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public GoogleMap googleMap;
    public List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_guide2);

        // 출발점과 도착점을 받아옴
        Intent intent = getIntent();
        String startX = intent.getStringExtra("START_LAT");
        String startY = intent.getStringExtra("START_LNG");
        String endX = intent.getStringExtra("GOAL_LAT");
        String endY = intent.getStringExtra("GOAL_LNG");

        //충전소 정보 받아오기
        ShowStationPathOn stationPathOn = new ShowStationPathOn();
        stationPathOn.execute
                (Double.toString(Math.min(Double.parseDouble(startX),Double.parseDouble(endX))),
                        Double.toString(Math.max(Double.parseDouble(startX),Double.parseDouble(endX))),
                        Double.toString(Math.min(Double.parseDouble(startY),Double.parseDouble(endY))),
                        Double.toString(Math.max(Double.parseDouble(startY),Double.parseDouble(endY))),
                        "상");


        // 맵 뷰 상태 설정
        RelativeLayout relativeLayout = new RelativeLayout(this);
        mMapView = new TMapView(this);
        mMapView.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mMapView.setIconVisibility(true);
        mMapView.setZoomLevel(10);
        mMapView.setMapType(TMapView.MAPTYPE_TRAFFIC);

        mMapView.setCompassMode(true);
        mMapView.setTrackingMode(true);
        mMapView.setLocationPoint(126.985022, 37.566474);
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_pin_red);
        mMapView.setIcon(bitmap);
        mMapView.setSightVisible(true);


        /*
        마커 추가하는 법

        TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
        mMapView.addMarkerItem("마커아이디",mapMarkerItem);

        해당 마커 제거
        mMapView.removeMarkerItem("마커아이디");

        마커 전부 제거
        mMapView.removeAllMarkerItem();

         */

        Bitmap start = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_star);
        Bitmap end = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_star);
        mMapView.setTMapPathIcon(start, end);
        mMapView.setMarkerRotate(true);
        mMapView.setPathRotate(true);

        mMapView.setMapType(TMapView.POSITION_DEFAULT);//or NAVI

        TMapData tMapData = new TMapData();

        TMapPoint startpoint = new TMapPoint(Double.parseDouble(startX), Double.parseDouble(startY));
        TMapPoint endpoint = new TMapPoint(Double.parseDouble(endX), Double.parseDouble(endY));
        tMapData.findPathData(startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                // 경로를 맵 위에 표시해준다
                mMapView.addTMapPath(tMapPolyLine);
            }
        });


        relativeLayout.addView(mMapView);
        setContentView(relativeLayout);
    }

    private class ShowStationPathOn extends AsyncTask<String, Void, ArrayList<String>> {
        /* 지역, 차종에 따른 검색 결과에 따른 충전소 보여주기 */
        ArrayList<String> mStation;
        ArrayList<LatLng> mPosition = new ArrayList<LatLng>();
        ArrayList<String> mItems = new ArrayList<String>();

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            UrlConnection urlconn = new UrlConnection();

            try {
                mStation = urlconn.GetDropByStation(strings[0],strings[1],strings[2],strings[3],strings[4]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("%%%%%%%%",mStation.toString());
            return mStation;
        }

        protected void onPostExecute(ArrayList<String> items) {

            /* gps 마커 추가 */
            /*for (int i=0;i<items.size();i++) {
                JSONObject jo= null;
                try {
                    jo = new JSONObject(items.get(i));String pos = jo.getString("map");
                    String[] poss = pos.split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(poss[0]),Double.parseDouble(poss[1]));
                    mPosition.add(latLng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
            mMapView.addMarkerItem("마커아이디",mapMarkerItem);*/


            TMapMarkerItem tourMarkerItem = new TMapMarkerItem();
            TMapPoint tpoint = new TMapPoint(35.795538,128.485107);
            TMapPoint tpoint2 = new TMapPoint( 37.530421,127.229919 );
            TMapPoint tpoint3 = new TMapPoint( 36.530421,127.229919 );
            tourMarkerItem.setTMapPoint(tpoint);
            tourMarkerItem.setTMapPoint(tpoint2);
            tourMarkerItem.setTMapPoint(tpoint3);
            tourMarkerItem.setIcon(bitmap);
            tourMarkerItem.setVisible(TMapMarkerItem.VISIBLE);
            //mMapView.setCenterPoint(37.566474,126.985022,false);
            mMapView.addMarkerItem("111",tourMarkerItem);

        }
    }
}
