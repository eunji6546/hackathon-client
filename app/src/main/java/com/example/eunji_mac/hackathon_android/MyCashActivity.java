package com.example.eunji_mac.hackathon_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MyCashActivity extends AppCompatActivity {

    TextView mText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cash);

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        TextView mText2 = (TextView) findViewById(R.id.text2);
        TextView mText3 = (TextView) findViewById(R.id.text3);
        TextView mText4 = (TextView) findViewById(R.id.text4);
        TextView mText5 = (TextView) findViewById(R.id.text5);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf);
        mText4.setTypeface(tf);
        mText5.setTypeface(tf);

        mText2.setText("Car Number\n" + AccountActivity.mCarNumber);
        mText3.setText("Cash\n" + AccountActivity.mCarCash + " won");

    }


    // pay button click event
    public void mClick1(View view) {
        Intent intent = new Intent(MyCashActivity.this,PayActivity.class);
        startActivity(intent);
    }


    // charge button click event
    public void mClick2(View view) {
        final EditText cashInput = new EditText(MyCashActivity.this);
        cashInput.setHint("10000 (숫자만 입력하세요)");
        AlertDialog alert = new AlertDialog.Builder(MyCashActivity.this)
                .setTitle("CHARGE CASH")
                .setMessage("충전할 캐시를 입력하세요")
                .setView(cashInput)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String[] params = new String[3];
                        String mChargeCash = cashInput.getText().toString();
                        int mSumString =
                                Integer.parseInt(mChargeCash) + Integer.parseInt(AccountActivity.mCarCash);

                        Log.v("Car Cash is updated", AccountActivity.mCarCash);

                        AccountActivity.mCarCash = String.valueOf(mSumString);

                        params[0] = AccountActivity.mCarNumber;
                        params[1] = AccountActivity.mCarType;
                        params[2] = AccountActivity.mCarCash;
                        Log.v("Cash is send", params[2]);

                        ShowNewMoney mGetMoney = new ShowNewMoney();
                        mGetMoney.execute(params);
                    }
                }).show();

    }

    private class ShowNewMoney extends AsyncTask< String[], Void, String> {

        UrlConnection urlconn = new UrlConnection();


        @Override
        protected String doInBackground(String[]... params) {
            try {
                Log.v("first element", params[0][0]);
                Log.v("second element", params[0][1]);
                Log.v("third element", params[0][2]);
                urlconn.Save(params[0][0], params[0][1], params[0][2]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return params[0][2];

        }

        protected void onPostExecute(String mSumString) {
            mText3 = (TextView) findViewById(R.id.text3);
            mText3.setText("Cash : " + mSumString + " won");
        }
    }
    public void mClickHome(View view) {

    }

}
