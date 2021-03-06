package com.example.eunji_mac.hackathon_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.*;

public class EVActivity extends AppCompatActivity {

    String mUserType; // 1 for driver, 0 for walker
    String mCarType, mCarNumber, mCash;

    RadioButton option1, option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ev);

        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        TextView mText2 = (TextView) findViewById(R.id.text2);
        TextView mText3 = (TextView) findViewById(R.id.text3);
        TextView mText4 = (TextView) findViewById(R.id.text4);
        TextView mText5 = (TextView) findViewById(R.id.text5);
        TextView mText6 = (TextView) findViewById(R.id.text6);
        TextView mText7 = (TextView) findViewById(R.id.text7);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");

        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);
        mText2.setTypeface(tf);
        mText3.setTypeface(tf);
        mText4.setTypeface(tf);
        mText5.setTypeface(tf);
        mText6.setTypeface(tf);
        mText7.setTypeface(tf);
    }

    // 검색 시작 클릭 이벤트
    public void mClick1(View view) {
        option1 = (RadioButton) findViewById(R.id.text2);
        option2 = (RadioButton) findViewById(R.id.text3);

        // 내 위치로 검색하기
        if (option1.isChecked()) {
            final Intent intent1 = new Intent(EVActivity.this, SearchNearStationActivity.class);

            if (AccountActivity.mUserType == 1) { // for driver
                startActivity(intent1);
            }
            else {
                String[] car_str = getResources().getStringArray(R.array.carSpinnerArray);
                final ArrayAdapter<String> mCarAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, car_str);
                final Spinner mCarSpinner = new Spinner(EVActivity.this);
                mCarSpinner.setAdapter(mCarAdapter);
                int  position;

                mCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?>  parent, View view, int position, long id) {
                        position = mCarSpinner.getSelectedItemPosition();
                        if (position == 0) {
                            AccountActivity.mCarType = "선택안함";
                        } else if (position == 4) {
                            AccountActivity.mCarType = "상";
                        } else if ((position == 1) || (position == 6)) {
                            AccountActivity.mCarType = "콤보";
                        } else {
                            AccountActivity.mCarType= "차데모";
                        }
                    }
                    public void onNothingSelected(AdapterView<?>  parent) {
                    }
                });

                AlertDialog alert = new AlertDialog.Builder(EVActivity.this)
                        .setTitle("CHARGE CASH")
                        .setMessage("충전할 차종을 선택하여 주세요.")
                        .setView(mCarSpinner)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(intent1);
                            }
                        }).show();
            }

        }
        // 내 주소로 검색하기
        else {
            Intent intent2 = new Intent(EVActivity.this, SelectRegionActivity.class);
            startActivity(intent2);
        }

    }

    //충전소 신청 클릭 이벡트
    public void mClick2(View view) {
        Intent intent = new Intent(EVActivity.this, RequestStationActivity.class);
        intent.putExtra("usertype", mUserType);

        if (mUserType.equals("1")) { // for driver
            intent.putExtra("carnumber", mCarNumber);
            intent.putExtra("cartype", mCarType);
            intent.putExtra("cash", mCash);
        }
        startActivity(intent);

    }


    //충전소 예약 클릭 이벡트
    public void mClick3(View view) {
        Intent intent = new Intent(EVActivity.this, ReservationStation.class);
        intent.putExtra("usertype", mUserType);

        if (mUserType.equals("1")) { // for driver
            intent.putExtra("carnumber", mCarNumber);
            intent.putExtra("cartype", mCarType);
            intent.putExtra("cash", mCash);
        }
        startActivity(intent);

    }
}
