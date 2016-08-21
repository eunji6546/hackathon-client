package com.example.eunji_mac.hackathon_android;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jisoo Lee (jisu0123@kaist.ac.kr)
 */
public class StationAdapter extends ArrayAdapter {

    private ArrayList<String> items;

    public StationAdapter(Context context, int resourceid, ArrayList<String> objects) {
        super(context, resourceid, objects);
        this.items = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.stationitems, null);
        }

        TextView mText1 = (TextView) v.findViewById(R.id.itemtext);
        TextView mText2 = (TextView) v.findViewById(R.id.idtext);
        mText1.setText(items.get(position).split("\\+")[0]);
        mText2.setText(items.get(position).split("\\+")[1]);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Stark.OTF");

        mText1.setTypeface(tf);
        Log.v("ID : ", mText2.getText().toString());
        return v;
    }
}
