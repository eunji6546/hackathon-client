package com.example.eunji_mac.hackathon_android;

import android.widget.Button;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    //Get userinfo by intent
    String mUserType; // 1 for driver, 0 for walker
    String mCarType;
    String mCarNumber;
    String mCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // userinfo 받기
        Intent intent = getIntent();
        mUserType = intent.getExtras().getString("usertype");

        if (mUserType.equals("1")) { // for driver
            mCarType = intent.getExtras().getString("cartype");
            mCarNumber = intent.getExtras().getString("carnumber");
            mCash = intent.getExtras().getString("cash");
        }

        /* 주변 주유소 찾기 */
        Button btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this,SearchNearStationActivity.class);
                intent1.putExtra("usertype", mUserType);

                if (mUserType.equals("1")) { // for driver
                    intent1.putExtra("carnumber", mCarNumber);
                    intent1.putExtra("cartype", mCarType);
                    intent1.putExtra("cash", mCash);
                }
                startActivity(intent1);
            }
        });

        /* 지역 주유소 검색 */
        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this,SelectRegionActivity.class);
                intent2.putExtra("usertype", mUserType);

                if (mUserType.equals("1")) { // for driver
                    intent2.putExtra("carnumber", mCarNumber);
                    intent2.putExtra("cartype", mCarType);
                    intent2.putExtra("cash", mCash);
                }
                startActivity(intent2);
            }
        });
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
