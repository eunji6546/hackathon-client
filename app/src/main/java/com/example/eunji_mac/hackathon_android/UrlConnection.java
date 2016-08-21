package com.example.eunji_mac.hackathon_android;

import android.util.Log;

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

        Log.v("sent URL(GET)", serverURL + addedURL + str);

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
    public static ArrayList<String> GetDropByStation(String lat1, String lat2, String lng1, String lng2, String type) throws IOException {

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

}