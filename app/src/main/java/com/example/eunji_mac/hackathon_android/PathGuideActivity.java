package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class PathGuideActivity extends AppCompatActivity implements LocationListener{


    private TMapView mMapView = null;
    public  TMapPoint myLocation;
    TMapGpsManager gps;
    String directDistance, directTime, directFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_guide);

        // 출발점과 도착점을 받아옴
        Intent intent = getIntent();
        String startX = intent.getStringExtra("START_LAT");
        String startY = intent.getStringExtra("START_LNG");
        String endX = intent.getStringExtra("GOAL_LAT");
        String endY = intent.getStringExtra("GOAL_LNG");

        final TMapPoint startpoint = new TMapPoint(Double.parseDouble(startX), Double.parseDouble(startY));
        final TMapPoint endpoint = new TMapPoint(Double.parseDouble(endX), Double.parseDouble(endY));


        //GPS
        gps= new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();

        myLocation = gps.getLocation();

        TMapGpsManager tmapgps = new TMapGpsManager(this);
        tmapgps.setLocationCallback();

        Log.e("GPS",myLocation.toString());
        //mMapView.setLocationPoint(myLocation.getLongitude(),myLocation.getLatitude());
        // 안나옴....


        // 맵 뷰 상태 설정
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mMapView);

        mMapView = new TMapView(this);

        frameLayout.addView(mMapView);

        mMapView.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mMapView.setIconVisibility(true);
        mMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mMapView.setTrackingMode(true);
        //mMapView.setMarkerRotate(true);
        //mMapView.setPathRotate(true);

        // 경로 아이콘 설정
        Bitmap start = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_star);
        Bitmap end = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_dot);
        mMapView.setTMapPathIcon(start, end);


        final TMapData tMapData = new TMapData();

        tMapData.findPathData(startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                // 경로를 맵 위에 표시해준다
                mMapView.addTMapPath(tMapPolyLine);
                // 두 마커가 모두 보이게 설정
                ArrayList<TMapPoint> arrayList = new ArrayList<TMapPoint>();
                arrayList.add(startpoint);
                arrayList.add(endpoint);
                TMapInfo tMapInfo = mMapView.getDisplayTMapInfo(arrayList);
                TMapPoint center = tMapInfo.getTMapPoint();

                Log.e("Center",center.toString());
                Log.e("Cenger", String.valueOf(tMapInfo.getTMapZoomLevel()));

                // center에 소요 시간과 거리에 관한 풍선을 만들 것임, 일단
                // 이름을 "notice"로 하고 찍어두기

                TMapMarkerItem tItem = new TMapMarkerItem();
                tItem.setTMapPoint(center);
                tItem.setName("notice");
                tItem.setVisible(TMapMarkerItem.VISIBLE);

                Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.common_google_signin_btn_icon_dark);
                tItem.setIcon(bitmap);
                tItem.setCanShowCallout(true);
                tItem.setCalloutTitle("경로 정보");
                tItem.setAutoCalloutVisible(true);

                // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
                tItem.setPosition(0.5f,1.0f);
                // 마커의 중심점을 하단, 중앙으로 설정
                mMapView.addMarkerItem("notice",tItem);

                mMapView.setLocationPoint(center.getLongitude(),center.getLatitude());
                mMapView.setCenterPoint(center.getLongitude(),center.getLatitude());
                mMapView.setZoomLevel(tMapInfo.getTMapZoomLevel());

                // 경로 소요 시간을 체크하는 !!
                final String NODE_ROOT = "kml";
                final String NODE_DISTANCE = "tmap:totalDistance";
                final String NODE_TIME = "tmap:totalTime";
                final String NODE_FARE = "tmap:totalFare";

                Log.e("**","***********");

                tMapData.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {

                        XMLDOMParser parser = new XMLDOMParser();
                        Document doc = document;
                        // Get elements by name employee
                        NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element e = (Element) nodeList.item(i);
                            Log.e("AAAAA",parser.getValue(e, NODE_DISTANCE));
                            Log.e("BBBB",parser.getValue(e, NODE_TIME));
                            Log.e("CCCC",parser.getValue(e, NODE_FARE));
                            directDistance = parser.getValue(e, NODE_DISTANCE);
                            directTime = parser.getValue(e,NODE_TIME);
                            directFare = parser.getValue(e,NODE_FARE);

                        }

                        Log.e("direct",directDistance);
                        Log.e("directt",directTime);
                        //distanceView.setText("Distance(m) :"+directDistance);
                        //timeView.setText("Time(sec) :"+directTime.toString());

                        TMapMarkerItem tMapMarkerItem = mMapView.getMarkerItemFromID("notice");
                        tMapMarkerItem.setCalloutSubTitle("거리(m):"+directDistance+"\n"+"시간(sec)"+directTime+"\r"+"요금(원)"+directFare);
                        Log.e("directt","DONE");

                    }
                });
                Log.e("&&","&&&&&&&&&&");
            }
        });




//TMapPoint tpoint1 = new TMapPoint(37.570841, 126.985302)
        //mMapView.setLocationPoint(Double.parseDouble(startY),Double.parseDouble(startX));

        //mMapView.setCenterPoint(Double.parseDouble(startX),Double.parseDouble(startY));

        //4.1.46 클릭이벤트 4.1.61
//4.1.65 TMapPoint 개체를 담은 ArrayList 를 입력으로 받아서 화면에 최적화된 상태로 보일 수 있는 ZoomLevel(int)와 중심점(TMapPoint)를 담은 TMapInfo 개체를 반환한다.

        //4.1.67 풍선뷰 클릭시 호출되는 Event Listener 등록함수 추가

        //4.1.94 지도를 주어진 넓이와 높이에 맞게 줌레벨을 조정한다.

        /*
        // 마커 달기 연습
        TMapPoint tpoint = new TMapPoint(37.570841, 126.985302);
        TMapMarkerItem tItem = new TMapMarkerItem();
        tItem.setTMapPoint(tpoint);
        tItem.setName("marker name");
        tItem.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.common_google_signin_btn_icon_dark);
        tItem.setIcon(bitmap);
        tItem.setCanShowCallout(true);
        tItem.setCalloutTitle("CALLOUT");
        tItem.setAutoCalloutVisible(true);

    // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
         tItem.setPosition(0.5f,1.0f);
    // 마커의 중심점을 하단, 중앙으로 설정
        mMapView.addMarkerItem("hi",tItem);

        // 이 마커를 중심으로 써클 그리기

        TMapCircle tcircle = new TMapCircle();
        tcircle.setCenterPoint(tpoint);
        tcircle.setRadius(100000f);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setRadiusVisible(true);
        tcircle.setCircleWidth(3);
        tcircle.setAreaAlpha(100);
        tcircle.setRadiusVisible(true);
        tcircle.setRadiusVisible(true);
        mMapView.addTMapCircle("circle",tcircle);
        */

    }




    @Override
    public void onLocationChanged(Location location) {

        Log.e("LOCATION","CHANGED:");
        myLocation = new TMapPoint(location.getLatitude(),location.getLongitude());
        mMapView.setLocationPoint(location.getLongitude(),location.getLatitude());
    }
}
