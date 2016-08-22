package com.example.eunji_mac.hackathon_android;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class UrlConnection {

    private static String serverURL = "http://project3-jisu0123.c9users.io";

    public static void Post(String str, String addedURL) throws IOException {
        URL url = new URL(serverURL + addedURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(100000);
        conn.setConnectTimeout(150000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.connect();

        Log.v("sent URL(POST)", serverURL + addedURL);
        Log.v("sent string", str);

        OutputStream os = conn.getOutputStream();
        os.write(str.getBytes("utf-8"));

        Log.v("Server Response(POST)", conn.getResponseMessage());
        os.close();

        // read the response
        conn.disconnect();
    }

    static public String Put(String str, String addedURL) throws IOException {

        String ret = "";
        URL url = new URL(serverURL + addedURL + URLEncoder.encode(str, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        Log.v("sent URL(PUT)", serverURL + addedURL + str);

        if (conn != null) {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(0);
            conn.setUseCaches(false);
            conn.setRequestMethod("PUT");
            int resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                for (; ; ) {
                    String line = br.readLine();
                    if (line == null) break;
                    ret += line;
                }
                br.close();
            } else
                Log.i("UrlConnection", "else");
            Log.v("Server Response(GET)", conn.getResponseMessage());
        }
        conn.disconnect();

        return ret;
    }

    static public String Get(String str, String addedURL) throws IOException {

        String ret = "";
        URL url = new URL(serverURL + addedURL + URLEncoder.encode(str, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        Log.v("sent URL(GET)", serverURL + addedURL + str);

        if (conn != null) {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(0);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            int resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                for (; ; ) {
                    String line = br.readLine();
                    if (line == null) break;
                    ret += line;
                }
                br.close();
            } else
                Log.i("UrlConnection", "else");
            Log.v("Server Response(GET)", conn.getResponseMessage());
        }
        conn.disconnect();

        return ret;
    }

    public static String Save(String carnumber, String cartype, String cash) throws IOException {

        String mCarCash = "";

        try {
            String ret = Put(carnumber + "+" + cartype + "+" + cash, "/update/user/");
            JSONObject jsonObject = new JSONObject(ret);
            mCarCash = jsonObject.getString("carcash");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mCarCash;
    }

    public static void Pay(String carnumber, String cartype, String cash, String lat, String lon) throws IOException {

        String ret = Put(carnumber + "+" + cartype + "+" + cash + "+" + lat + "+" + lon , "/update/pay/");

    }



    public static String GetHosting() throws IOException {
        return Get("","/get/carsharing");
    }

    public static ArrayList<String> GetTown(String province) throws IOException {

        ArrayList<String> Townlist = new ArrayList();

        try {
            JSONArray mJsonArr = new JSONArray(Get(province, "/get/address/"));
            for (int i=0; i < mJsonArr.length(); i++) {
                JSONObject mJsonObj = mJsonArr.getJSONObject(i);
                String mTown = mJsonObj.getString("town");
                Townlist.add(mTown);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Townlist;
    }

    public static void Reportnow(String lat, String lon) throws IOException {
        Put(lat + "+" + lon + "+", "/report/");
        return ;
    }

    public static void AddHosting(String mStart, String mEnd, String mDateString,
                                  String mFareString, String mAvString) throws IOException {
        Put(mStart + "+" + mEnd + "+" + mFareString + "+" +
                "0+" + mAvString + "+" + mDateString, "/update/host/");
        return;
    }

    public static boolean Getalert(String lat, String lon) throws IOException {
        try {
            JSONObject mJsonObj = new JSONObject(Get(lat + "+" + lon + "+", "/get/report/"));
            String mExist = mJsonObj.getString("result");
            Log.v("mExist", mExist);
            if (mExist.equals("true")) {
                Log.v("existence :" , "true");
                return true;
            }
            Log.v("existence :" , "false");
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<String> GetSupply(String province, String town, String type) throws IOException {

        ArrayList<String> Addrlist = new ArrayList();

        try {
            JSONArray mJsonArr = new JSONArray(Get(province + "+" + town + "+" + type, "/get/custom/"));

            for (int i=0; i < mJsonArr.length(); i++) {
                JSONObject mJsonObj = mJsonArr.getJSONObject(i);
                Addrlist.add(mJsonObj.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Addrlist;
    }
    public static ArrayList<String> GetDropByStation(String lat1, String lat2, String lng1,
                                                     String lng2, String type) throws IOException {

        ArrayList<String> Addrlist = new ArrayList();

        try {
            JSONArray mJsonArr = new JSONArray(Get(lat1 + "+" + lat2 + "+" + lng1 + "+" +lng2 + "+" + type, "/get/station/"));

            for (int i=0; i < mJsonArr.length(); i++) {
                JSONObject mJsonObj = mJsonArr.getJSONObject(i);
                Addrlist.add(mJsonObj.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Addrlist;
    }
    public static Object GetStationInfo(LatLng stationLatlng) {
        JSONObject jo = null;
        try {
            // 총 2개의 충전기 중 하나가 사용 중인 경우 & 각 충전소의 예약 현황
            // return Value : { "Availabliltyw" : 1(사용 중인 충전기 수), "total plugger" : 2(전체 충전기 수),"Booked_Condition" : {[9,9.5, ... 35], [11,11.5]}}
            jo = new JSONObject(Get(stationLatlng.toString(),"/get/stationreservation/"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
    public static void PostBookingStation(LatLng stationLatlng, ArrayList<Double> time) {
        try {
            // argument Example >> stationLatlng : Latlng, time : [9,9.5]}
            Post(stationLatlng.toString(),"/put/reservatestation/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}