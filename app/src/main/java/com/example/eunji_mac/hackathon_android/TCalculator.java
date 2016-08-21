package com.example.eunji_mac.hackathon_android;

import android.location.Location;
import android.util.Log;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by eunji_mac on 2016. 8. 21..
 */
public class TCalculator {

//    public static ArrayList<TMapPoint> TCalculator(TMapPoint start, TMapPoint end){
//        ArrayList<TMapPoint> array = new ArrayList<>();
//
//        Log.e("Calculator",start.toString()+"%%"+end.toString());
//        //startpoint = new TMapPoint(start.getLatitude(),start.getLongitude());
//        //endpoint = new TMapPoint(end.getLatitude(),end.getLongitude());
//        TMapPoint startpoint = start;
//        TMapPoint endpoint = end;
//        array.add(startpoint);
//        array.add(endpoint);
//        return array;
//    }

    public static String calculate(TMapPoint startpoint, TMapPoint endpoint){
        Log.e("Cal","START CALCULATING");
        final String result = null;
        TMapData tMapData = new TMapData();
        final String NODE_ROOT = "kml";
        final String NODE_DISTANCE = "tmap:totalDistance";
        final String NODE_TIME = "tmap:totalTime";
        final String NODE_FARE = "tmap:totalFare";
        final String NODE_TAXIFARE = "tmap:taxiFare";

        Log.e("Cal","before CALCULATING");

        tMapData.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Log.e("Cal","CALCULATING");
                String result = "";
                XMLDOMParser parser = new XMLDOMParser();
                Document doc = document;
                // Get elements by name employee
                NodeList nodeList = doc.getElementsByTagName(NODE_ROOT);


                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element e = (Element) nodeList.item(i);
                    result += parser.getValue(e, NODE_DISTANCE);
                    result += "&" + parser.getValue(e, NODE_TIME);

                }
        });
        return result;
    }


}
