package com.example.eunji_mac.hackathon_android;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.skp.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.skp.Tmap.BizCategory;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.BizCategoryListenerCallback;
import com.skp.Tmap.TMapData.ConvertGPSToAddressListenerCallback;
import com.skp.Tmap.TMapData.FindAllPOIListenerCallback;
import com.skp.Tmap.TMapData.FindAroundNamePOIListenerCallback;
import com.skp.Tmap.TMapData.FindPathDataAllListenerCallback;
import com.skp.Tmap.TMapData.FindPathDataListenerCallback;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapPolygon;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapView.MapCaptureImageListenerCallback;
import com.skp.Tmap.TMapView.TMapLogoPositon;
import com.example.eunji_mac.hackathon_android.LogManager;
import com.example.eunji_mac.hackathon_android.R;

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
    /*
        private static final int[] mArrayMapButton = {
                R.id.btnOverlay,
                R.id.btnAnimateTo,
                R.id.btnZoomIn,
                R.id.btnZoomOut,
                R.id.btnGetZoomLevel,
                R.id.btnSetZoomLevel,
                R.id.btnSetMapType,
                R.id.btnGetLocationPoint,
                R.id.btnSetLocationPoint,
                R.id.btnSetIcon,
                R.id.btnSetCompassMode,
                R.id.btnGetIsCompass,
                R.id.btnSetTrafficInfo,
                R.id.btnGetIsTrafficeInfo,
                R.id.btnSetSightVisible,
                R.id.btnSetTrackIngMode,
                R.id.btnGetIsTracking,
                R.id.btnAddTMapCircle,
                R.id.btnRemoveTMapCircle,
                R.id.btnMarkerPoint,
                R.id.btnRemoveMarker,
                R.id.btnMoveFrontMarker,
                R.id.btnMoveBackMarker,
                R.id.btnDrawPolyLine,
                R.id.btnErasePolyLine,
                R.id.btnDrawPolygon,
                R.id.btnErasePolygon,
                R.id.btnBicycle,
                R.id.btnBicycleFacility,
                R.id.btnMapPath,
                R.id.btnRemoveMapPath,
                R.id.btnDisplayMapInfo,
                R.id.btnNaviGuide,
                R.id.btnCarPath,
                R.id.btnPedestrian_Path,
                R.id.btnBicycle_Path,
                R.id.btnGetCenterPoint,
                R.id.btnFindAllPoi,
                R.id.btnConvertToAddress,
                R.id.btnGetBizCategory,
                R.id.btnGetAroundBizPoi,
                R.id.btnTileType,
                R.id.btnCapture,
                R.id.btnDisalbeZoom,
                R.id.btnInvokeRoute,
                R.id.btnInvokeSetLocation,
                R.id.btnInvokeSearchPortal,
                R.id.btnTimeMachine,
                R.id.btnTMapInstall,
                R.id.btnMarkerPoint2,
        };

    */
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




    }
}
