package com.example.eunji_mac.hackathon_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jisoo Lee (jisu0123@kaist.ac.kr)
 */
public class CarSharingAdapter extends ArrayAdapter {

    private ArrayList<String> items;
    static TextView mText1, mText2;
    String mRet;

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

        mText1 = (TextView) v.findViewById(R.id.itemtext);
        mText2 = (TextView) v.findViewById(R.id.idtext);

        TextView mRide = (TextView) v.findViewById(R.id.button);

        mText1.setText(items.get(position).split("\\+")[0]);
        mText2.setText(items.get(position).split("\\+")[1]);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Stark.OTF");

        mText1.setTypeface(tf);
        mRide.setTypeface(tf);

        Log.v("ID : ", mText2.getText().toString());

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "탑승이 신청되었습니다!", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UrlConnection urlconn = new UrlConnection();
                            String mId = mText2.getText().toString().split(" ")[1];

                            mRet = urlconn.UpdateHosting(mId);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getContext(), CarPoolActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }
                }).start();

                //Log.v("UpdateHosting : ", mRet);

//                String mAvailable = mText1.getText().toString().split("\n")[4];
//                String mParse1 = mAvailable.split("\\[")[1];
//                String current = mParse1.split("/")[0];
//                String full = mParse1.split("/")[1].split("\\]")[0];
//                if (current.equals(full)) {
//                    Toast.makeText(getContext(), "탑승이 마감되었습니다!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Toast.makeText(getContext(), "탑승이 신청되었습니다!", Toast.LENGTH_SHORT).show();
//                int mPeople = Integer.parseInt(current) + 1;
//                int mFull = Integer.parseInt(full);
//                String restinfo = mText1.getText().toString().split("Av")[0];
//                mText1.setText(restinfo + "Available. "+"[" + mPeople + "/" + mFull +"]");
            }
        });

        return v;
    }
}
