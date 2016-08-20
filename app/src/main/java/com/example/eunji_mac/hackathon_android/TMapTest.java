package com.example.eunji_mac.hackathon_android;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import com.skp.Tmap.TMapTapi;

public class TMapTest extends Activity {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap_test);

        //new TMapRequest().execute("36.369617", "127.363993", "36.361400", "127.371651", "30");
    }
   */

    private TMapView mMapView = null;

    private Context mContext;
    private ArrayList<Bitmap> mOverlayList;
    //private ImageOverlay mOverlay;

    public static String mApiKey; // 발급받은 appKey
    public static String mBizAppID; // 발급받은 BizAppID (TMapTapi로 TMap앱 연동을 할 때 BizAppID 꼭 필요)

    private int m_nCurrentZoomLevel = 0;
    private double m_Latitude = 0;
    private double m_Longitude = 0;
    private boolean m_bShowMapIcon = false;

    private boolean m_bTrafficeMode = false;
    private boolean m_bSightVisible = false;
    private boolean m_bTrackingMode = false;

    private boolean m_bOverlayMode = false;

    ArrayList<String> mArrayID;

    ArrayList<String> mArrayCircleID;
    private static int mCircleID;

    ArrayList<String> mArrayLineID;
    private static int mLineID;

    ArrayList<String> mArrayPolygonID;
    private static int mPolygonID;

    ArrayList<String> mArrayMarkerID;
    private static int mMarkerID;

    TMapGpsManager gps = null;


    /**
     * onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap_test);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        //mContext = this;

        mMapView = new TMapView(this);
        mMapView.setSKPMapApiKey("d6e4f98c-755e-3a31-aa8d-8b2dc176be1a");
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mMapView.setIconVisibility(true);
        mMapView.setZoomLevel(10);
        mMapView.setMapType(TMapView.MAPTYPE_TRAFFIC);

        mMapView.setCompassMode(true);
        mMapView.setTrackingMode(true);
        mMapView.setLocationPoint(126.985022,37.566474);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.map_pin_red);
        mMapView.setIcon(bitmap);
        mMapView.setSightVisible(true);


        relativeLayout.addView(mMapView);
        setContentView(relativeLayout);



        /*
        마커 추가하는 법

        TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
        mMapView.addMarkerItem("마커아이디",mapMarkerItem);

        해당 마커 제거
        mMapView.removeMarkerItem("마커아이디");

        마커 전부 제거
        mMapView.removeAllMarkerItem();

         */

        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        mMapView.addTMapPath(tMapPolyLine);
        // mMapView.removeTMapPath

        Bitmap start = BitmapFactory.decodeResource(this.getResources(),R.drawable.poi_star);
        Bitmap end = BitmapFactory.decodeResource(this.getResources(),R.drawable.poi_star);
        mMapView.setTMapPathIcon(start,end);
        mMapView.setMarkerRotate(true);
        mMapView.setPathRotate(true);

        mMapView.setMapType(TMapView.POSITION_DEFAULT);//or NAVI

        // 마커들을 다 보이게하기 : 4.1.65

        TMapData tMapData = new TMapData();
        TMapPoint startpoint = new TMapPoint(37.538958,127.028073);
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
                            Log.e("AAAAA",parser.getValue(e, NODE_DISTANCE));
                            Log.e("BBBB",parser.getValue(e, NODE_TIME));
                            Log.e("CCCC",parser.getValue(e, NODE_FARE));
                            Log.e("BBBB",parser.getValue(e, NODE_TAXIFARE));

                        }

                }
            });

           // Log.e("DOCUMENT",doc.toString());

        TMapTapi tMapTapi = new TMapTapi(this);

        if (tMapTapi.isTmapApplicationInstalled()){
            Log.e("ISSI","INSTALLED");
        }else {
            Log.e("ISSI","NONONONONO");

        }


        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI("SKT타워", 100, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                for(int i=0; i<arrayList.size(); i++){
                    TMapPOIItem item = arrayList.get(i);
                    Log.e("FUNCK","POI NAMe: "+ item.getPOIName().toString()+","+
                    "ADDR : "+ item.getPOIAddress().replace("null","")+"," +
                    "Point : "+ item.getPOIPoint().toString());
                }
            }
        });
        Log.e("F","FINISH");


    }

}
