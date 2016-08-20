package com.example.eunji_mac.hackathon_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MyCashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cash);

        TextView mCash = (TextView)findViewById(R.id.cashText);
        Button chargeBtn = (Button)findViewById(R.id.chargeBtn);
        Button settlementBtn = (Button)findViewById(R.id.settlementBtn);

        /*
            mCash server에서 받아오기~~!!

            ~~~~~~
         */


        chargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText cashInput = new EditText(MyCashActivity.this);
                AlertDialog alert = new AlertDialog.Builder(MyCashActivity.this)
                        //.setIcon(R.drawable.money)
                        .setTitle("주유 캐시 충전하기")
                        .setMessage("충전할 캐시를 입력하세요")
                        .setView(cashInput)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*
                                    서버에 충전한 금액 보내고, 디비 업데이트
                                 */


                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        settlementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    결제 버튼 클릭 이벤트~~!!
                 */

            }
        });
    }
}
