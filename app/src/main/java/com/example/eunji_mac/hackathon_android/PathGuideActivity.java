package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.skp.Tmap.TMapData;
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

public class PathGuideActivity extends AppCompatActivity {


    private TMapView mMapView = null;

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
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_pin_red);
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

        //TMapPolyLine tMapPolyLine = new TMapPolyLine();
        //tMapPolyLine.addLinePoint(new TMapPoint(37.538958, 127.028073));
        //tMapPolyLine.addLinePoint(new TMapPoint(36.369608, 127.364014));

        // mMapView.removeTMapPath

        Bitmap start = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_star);
        Bitmap end = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_star);
        mMapView.setTMapPathIcon(start, end);
        mMapView.setMarkerRotate(true);
        mMapView.setPathRotate(true);

        mMapView.setMapType(TMapView.POSITION_DEFAULT);//or NAVI

        TMapData tMapData = new TMapData();

        TMapPoint startpoint = new TMapPoint(Double.parseDouble(startX), Double.parseDouble(startY));
        TMapPoint endpoint = new TMapPoint(36.369608, 127.364014);
        tMapData.findPathData(startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                // 경로를 맵 위에 표시해준다
                mMapView.addTMapPath(tMapPolyLine);
            }
        });


        relativeLayout.addView(mMapView);
        setContentView(relativeLayout);


        /*
        // 마커들을 다 보이게하기 : 4.1.65

        TMapData tMapData = new TMapData();
        TMapPoint startpoint = new TMapPoint(37.538958, 127.028073);
        TMapPoint endpoint = new TMapPoint(36.369608, 127.364014);


        final String NODE_ROOT = "kml";
        final String NODE_DISTANCE = "tmap:totalDistance";
        final String NODE_TIME = "tmap:totalTime";
        final String NODE_FARE = "tmap:totalFare";
        final String NODE_TAXIFARE = "tmap:taxiFare";

        tMapData.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {

                XMLDOMParser parser = new XMLDOMParser();
                Document doc = document;
                // Get elements by name employee
                NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element e = (Element) nodeList.item(i);
                    Log.e("AAAAA", parser.getValue(e, NODE_DISTANCE));
                    Log.e("BBBB", parser.getValue(e, NODE_TIME));
                    Log.e("CCCC", parser.getValue(e, NODE_FARE));
                    Log.e("BBBB", parser.getValue(e, NODE_TAXIFARE));

                }

            }
        });

        // Log.e("DOCUMENT",doc.toString());

        TMapTapi tMapTapi = new TMapTapi(this);

        if (tMapTapi.isTmapApplicationInstalled()) {
            Log.e("ISSI", "INSTALLED");
        } else {
            Log.e("ISSI", "NONONONONO");

        }


        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI("SKT타워", 100, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                for (int i = 0; i < arrayList.size(); i++) {
                    TMapPOIItem item = arrayList.get(i);
                    Log.e("FUNCK", "POI NAMe: " + item.getPOIName().toString() + "," +
                            "ADDR : " + item.getPOIAddress().replace("null", "") + "," +
                            "Point : " + item.getPOIPoint().toString());
                }
            }
        });
        Log.e("F", "FINISH");

*/
    }
}
