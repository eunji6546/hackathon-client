package com.example.eunji_mac.hackathon_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapPolyLineLayer;
import com.skp.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PathGuideActivity2 extends AppCompatActivity implements TMapView.OnClickListenerCallback {


    private TMapView mMapView = null;
    TMapPoint startpoint;
    TMapPoint tempstartpoint;
    TMapPoint endpoint;
    TMapPoint noticepoint;
    ArrayList<String> items;
    Bitmap bitmap = null;
    String directDistance, directTime, directFare;

    // 경유지 리스트
    ArrayList<TMapMarkerItem> passItemList= new ArrayList<TMapMarkerItem>();
    ArrayList<TMapPoint> passPointList = new ArrayList<TMapPoint>();
    Integer number = 0;
    Integer pDist=0;
    Integer pTime=0;
    Integer pFare=0;

    TextView mDistanceView;
    TextView mTimeView;
    TextView mFeeView;


    public static volatile String mTotalDist;
    public static volatile String mTotalTime;
    public static volatile String mTotalfee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_guide2);

        mDistanceView = (TextView)findViewById(R.id.distance);
        mTimeView = (TextView)findViewById(R.id.timerequired);
        mFeeView = (TextView)findViewById(R.id.fee);

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


        // 출발점과 도착점을 받아옴
        Intent intent = getIntent();
        String mCarType = intent.getStringExtra("CAR_TYPE");
        String startX = intent.getStringExtra("START_LAT");
        String startY = intent.getStringExtra("START_LNG");
        String endX = intent.getStringExtra("GOAL_LAT");
        String endY = intent.getStringExtra("GOAL_LNG");

        startpoint = new TMapPoint(Double.parseDouble(startX), Double.parseDouble(startY));
        endpoint = new TMapPoint(Double.parseDouble(endX), Double.parseDouble(endY));

        // 마커로 출발지와 도착지 정보 제시
        TMapMarkerItem startitem = new TMapMarkerItem();
        startitem.setTMapPoint(startpoint);
        startitem.setVisible(TMapMarkerItem.VISIBLE);
        startitem.setCalloutTitle("출발");
        startitem.setCalloutSubTitle("100km내 충전소를 선택하세요");
        startitem.setAutoCalloutVisible(true);
        mMapView.addMarkerItem("startitem",startitem);

        TMapMarkerItem enditem =new TMapMarkerItem();
        enditem.setTMapPoint(endpoint);
        enditem.setVisible(TMapMarkerItem.VISIBLE);
        enditem.setCalloutTitle("도착");
        enditem.setAutoCalloutVisible(true);

        mMapView.addMarkerItem("enditem",enditem);


        // 최초 출발점에 대한 100km반경 제공

        TMapCircle tcircle = new TMapCircle();
        tcircle.setCenterPoint(startpoint);
        tcircle.setRadius(100000f);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setRadiusVisible(true);
        tcircle.setCircleWidth(3);
        tcircle.setAreaAlpha(100);
        tcircle.setRadiusVisible(true);
        tcircle.setRadiusVisible(true);

        mMapView.addTMapCircle("circle",tcircle);


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
                noticepoint = center;

                // center에 소요 시간과 거리에 관한 풍선을 만들 것임, 일단
                // 이름을 "notice"로 하고 찍어두기

                TMapMarkerItem tItem = new TMapMarkerItem();
                tItem.setTMapPoint(center);
                tItem.setName("notice");
                tItem.setVisible(TMapMarkerItem.VISIBLE);

                Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.common_google_signin_btn_icon_dark);
                tItem.setIcon(bitmap);
                tItem.setCanShowCallout(true);
                tItem.setCalloutTitle(String.format("%d개의 주유소 경유시", number));
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


                tMapData.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {

                        XMLDOMParser parser = new XMLDOMParser();
                        Document doc = document;
                        // Get elements by name employee
                        NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element e = (Element) nodeList.item(i);
                            directDistance = parser.getValue(e, NODE_DISTANCE);
                            directTime = parser.getValue(e,NODE_TIME);
                            directFare = parser.getValue(e,NODE_FARE);

                        }

                        /*TMapMarkerItem tMapMarkerItem = mMapView.getMarkerItemFromID("notice");
                        tMapMarkerItem.setCalloutSubTitle("거리(m):"+directDistance+"\n"+"시간(sec)"+directTime+"\r"+"요금(원)"+directFare);
*/
                        Toast.makeText(PathGuideActivity2.this, "거리 이내에 충전소가 존재하지 않습니다.", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

        //충전소 정보 받아오기
        ShowStationPathOn stationPathOn = new ShowStationPathOn();
        stationPathOn.execute
                (Double.toString(Math.min(Double.parseDouble(startX),Double.parseDouble(endX))),
                        Double.toString(Math.max(Double.parseDouble(startX),Double.parseDouble(endX))),
                        Double.toString(Math.min(Double.parseDouble(startY),Double.parseDouble(endY))),
                        Double.toString(Math.max(Double.parseDouble(startY),Double.parseDouble(endY))),
                        "상");

    }

    @Override
    public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {

        if(arrayList.size()>0){

            if(arrayList.get(0).getTMapPoint().getLatitude() == noticepoint.getLatitude()
                    && arrayList.get(0).getTMapPoint().getLongitude() == noticepoint.getLongitude()){
                // notice point 일때는 클릭이벤트 놉
                return false;
            }

            final TMapMarkerItem tMapMarkerItem = arrayList.get(0);

            // 여기서 경로로 추가할껀지 물어보는 !!

            AlertDialog alert = new AlertDialog.Builder(PathGuideActivity2.this)
                    .setTitle("경유지로 추가하시겠습니까?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selection(tMapMarkerItem);
                            dialog.dismiss();
                        }
                    }).show();

        }


        return true;
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        //뭐하는앤지 모르겠음
        return false;
    }

    public void selection (TMapMarkerItem tMapMarkerItem){

        /* 선택한 주유소를 경유하는 경로를 보여주는 함수 */

        final TMapPoint selectedpoint = tMapMarkerItem.getTMapPoint();

        final String NODE_ROOT = "kml";
        final String NODE_DISTANCE = "tmap:totalDistance";
        final String NODE_TIME = "tmap:totalTime";
        final String NODE_FARE = "tmap:totalFare";


        passItemList.add(tMapMarkerItem);
        passPointList.add(selectedpoint);

        if (number == 0){
            // 첫번째 상황에서는
            tempstartpoint = startpoint;
        }

        // 이전 출발지 써클과 경로들 모두 제거
        mMapView.removeAllTMapCircle();
        mMapView.removeAllTMapPolyLine();

        // 선택한 경유지에 대해 순번 부여
        number++;
        tMapMarkerItem.setCalloutTitle(String.format("%d", number));
        mMapView.addMarkerItem(tMapMarkerItem.getID(),tMapMarkerItem);


        // 출발점에 대한 100km반경 제공
        TMapCircle tcircle = new TMapCircle();
        tcircle.setCenterPoint(selectedpoint);
        tcircle.setRadius(100000f);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setRadiusVisible(true);
        tcircle.setCircleWidth(3);
        tcircle.setAreaAlpha(100);
        tcircle.setRadiusVisible(true);
        tcircle.setRadiusVisible(true);

        mMapView.addTMapCircle("circle",tcircle);
        // 경유하는 경로 구하기

        // 걸리는 시간과 거리 계산
        // first , temp 출발지 -> 이번에 선택한 경유지
        final TMapData tMapData = new TMapData();
        tMapData.findPathDataAll(tempstartpoint, selectedpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {

                XMLDOMParser parser = new XMLDOMParser();
                Document doc = document;
                // Get elements by name employee
                NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element e = (Element) nodeList.item(i);
                    directDistance = parser.getValue(e, NODE_DISTANCE);
                    directTime = parser.getValue(e,NODE_TIME);
                    directFare = parser.getValue(e,NODE_FARE);
                }

                pDist = pDist+ Integer.parseInt(directDistance);
                pTime = pTime + Integer.parseInt(directTime);
                pFare = pFare + Integer.parseInt(directFare);



                // second, 선택한 경유지 -> 도착지까지의 거리 시간 비용 계산, 더해주고
                // notice 에 띄워주기

                tMapData.findPathDataAll(selectedpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {

                        XMLDOMParser parser = new XMLDOMParser();
                        Document doc = document;
                        // Get elements by name employee
                        NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element e = (Element) nodeList.item(i);
                            directDistance = parser.getValue(e, NODE_DISTANCE);
                            directTime = parser.getValue(e,NODE_TIME);
                            directFare = parser.getValue(e,NODE_FARE);
                        }

                        pDist = pDist+ Integer.parseInt(directDistance);
                        pTime = pTime + Integer.parseInt(directTime);
                        pFare = pFare + Integer.parseInt(directFare);

                        // notice 에 띄워주기

                        /*TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
                        TMapPoint temp;
                        temp = noticepoint;
                        tMapMarkerItem.setName("notice");
                        tMapMarkerItem.setVisible(TMapMarkerItem.VISIBLE);
                        tMapMarkerItem.setTMapPoint(temp);
                        tMapMarkerItem.setCalloutTitle(String.format("%d개의 주유소 경유시", number));
                        tMapMarkerItem.setCalloutSubTitle("거리(m):"+pDist+"\n"+"시간(sec)"+pTime+"\r"+"요금(원)"+pFare);
                        tMapMarkerItem.setCanShowCallout(true);
                        tMapMarkerItem.setAutoCalloutVisible(true);
*/

                        mTotalDist = pDist.toString();
                        mTotalfee = pFare.toString();
                        mTotalTime = pTime.toString();


                       // Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.common_google_signin_btn_icon_dark);
                       // tItem.setIcon(bitmap);


                        // tempstartpoint 를 선택했던 경유지로 갱신
                        tempstartpoint = selectedpoint;

                        tMapData.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, passPointList, 0,
                                new TMapData.FindPathDataListenerCallback() {
                                    @Override
                                    public void onFindPathData(TMapPolyLine tMapPolyLine) {

                                        new Thread() {
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mTimeView.setText(mTotalTime);
                                                        mDistanceView.setText(mTotalDist);
                                                        mFeeView.setText(mTotalfee);
                                                    }
                                                });
                                            }
                                        }.start();

                                        mMapView.addTMapPath(tMapPolyLine);
                                        // 경로 그어주기

                                    }
                                });


                    }
                });


            }
        });




        // 1) p 정보 +


        tMapData.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint, passPointList, 0, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {


                mMapView.addTMapPath(polyLine);
                /*TMapMarkerItem notice = mMapView.getMarkerItemFromID("notice");
                notice.setCalloutTitle(String.format("%d개의 주유소 경유시", number));
*/
                //걸리는 시간 계산
                // first , 출발지 -> 경유지
                tMapData.findPathDataAll(startpoint, passPointList.get(number-1), new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {

                        XMLDOMParser parser = new XMLDOMParser();
                        Document doc = document;
                        // Get elements by name employee
                        NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);

                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element e = (Element) nodeList.item(i);
                            directDistance = parser.getValue(e, NODE_DISTANCE);
                            directTime = parser.getValue(e,NODE_TIME);
                            directFare = parser.getValue(e,NODE_FARE);

                        }

                    }
                });

            }

        });



    }
    private class ShowStationPathOn extends AsyncTask<String, Void, ArrayList<String>> {

        /* 지역, 차종에 따른 검색 결과에 따른 충전소 보여주기 */

        ArrayList<String> mStation;
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            UrlConnection urlconn = new UrlConnection();

            try {
                mStation = urlconn.GetDropByStation(strings[0],strings[1],strings[2],strings[3],strings[4]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mStation;
        }

        protected void onPostExecute(ArrayList<String> items) {

            /* 충전소 마커 추가 */
            for (int i=0;i<items.size();i++) {
                JSONObject jo= null;
                try {
                    jo = new JSONObject(items.get(i));

                    //HERE

                    double lon = Double.parseDouble( jo.get("lon").toString());
                    double lat = Double.parseDouble(jo.get("lat").toString());

                    TMapMarkerItem tourMarkerItem = new TMapMarkerItem();
                    TMapPoint tpoint = new TMapPoint(lat,lon);
                    tourMarkerItem.setTMapPoint(tpoint);
                    bitmap =BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.flat_location_icon);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap,64, 64, true);
                    tourMarkerItem.setIcon(resized);
                    tourMarkerItem.setVisible(TMapMarkerItem.VISIBLE);

                    mMapView.addMarkerItem(String.valueOf(i),tourMarkerItem);
                    // 이 주유소에 대해서, 경로를 회색으로 그려줌
                    TMapData tMapData = new TMapData();

                    /*
                    tMapData.findPathData(startpoint, tpoint, new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine tMapPolyLine) {
                            // 경로를 맵 위에 표시해준다
                            TMapPolyLine newline ;
                            newline = tMapPolyLine;
                            TMapPolyLineLayer layer = new TMapPolyLineLayer();

                            //newline.setLineColor(Color.GRAY);
                            mMapView.addTMapPath(tMapPolyLine);
                            // 두 마커가 모두 보이게 설정
                        }
                    });
                    */


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
