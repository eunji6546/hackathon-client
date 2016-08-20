package com.example.eunji_mac.hackathon_android;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by youngjisu on 2016. 8. 20..
 */
public class CarSharingAdapter extends ArrayAdapter {

    private ArrayList<String> items;

    public CarSharingAdapter(Context context, int resourceid, ArrayList<String> objects) {
        super(context, resourceid, objects);
        this.items = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;


        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.items, null);
        }

        TextView textView = (TextView) v.findViewById(R.id.itemtext);
        textView.setText(items.get(position));

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Stark.OTF");

        textView.setTypeface(tf);
        return v;
    }
}
