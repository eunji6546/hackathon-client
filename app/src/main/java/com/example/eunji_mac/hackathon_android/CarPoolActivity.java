package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CarPoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_pool);

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);

        new ShowListView().execute();

    }

    private class ShowListView extends AsyncTask<URL, Integer, ArrayList<String>> {

        UrlConnection urlconn = new UrlConnection();
        ArrayList<String> items = new ArrayList();

        protected ArrayList<String> doInBackground(URL... urls) {
            try {
                String Hostinglist = urlconn.GetHosting();
                try {
                    JSONArray mList = new JSONArray(Hostinglist);
                    Log.v("Car Sharing List", mList.toString());
                    int length = mList.length();
                    Log.v("Number Of Hosts", String.valueOf(length));
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = mList.getJSONObject(i);
                        String mId = jsonObject.getString("_id");
                        String mDeparture = jsonObject.getString("departure");
                        String mDest = jsonObject.getString("dest");
                        String mDate = jsonObject.getString("time");
                        String mFee = jsonObject.getString("fee");
                        String mPeople = jsonObject.getString("people");
                        String mFull = jsonObject.getString("full");
                        String item = "No." + Integer.toString(i+1) +
                                "\n Departure. " + mDeparture + " (" + mDate + ")" +
                                "\n Arrival. " + mDest +
                                "\n Fare. " + mFee + " won" +
                                "\n Available. "+"[" + mPeople + "/" + mFull +"] + "
                                + mId;
                        items.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return items;
        }

        protected void onPostExecute(ArrayList<String> items) {
            CarSharingAdapter adapter = new CarSharingAdapter(getBaseContext(), R.layout.items, items);
            ListView myListView = (ListView) findViewById(R.id.listView);
            myListView.setAdapter(adapter);
        }
    }

    // start hosting activity
    public void mClick1(View view) {
        Intent intent = new Intent(CarPoolActivity.this, HostingActivity.class);
        startActivity(intent);
    };

    protected void onResume() {
        super.onResume();
        new ShowListView().execute();
    }
}
