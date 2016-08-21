package com.example.eunji_mac.hackathon_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SearchPathActivity extends AppCompatActivity {
    String mUserType; // 1 for driver, 0 for walker
    String mCarType;
    String mCarNumber;
    String mCash;
    int mFlags = 0;


    private PlacePicker.IntentBuilder builder;
    private PlacesAutoCompleteAdapter mPlacesAdapter;

    //출발지 관련 변수
    private Boolean selectStartBool = false;
    private TextView myStartLocation; //입력창
    private LatLng mStartLatLag; //위도, 경도
    private static final int PLACE_PICKER_START_FLAG = 1;


    //도착지 관련 변수
    private Boolean selectGoalBool = false;
    private TextView myGoalLocation; //입력창
    private LatLng mGoalLatLag; //위도, 경도
    private static final int PLACE_PICKER_GOAL_FLAG = 2;

    private Button directPathBtn;
    private Button dropByStationBtn;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_path);

        Intent intent = getIntent();
        mUserType = intent.getExtras().getString("usertype");

        if (mUserType.equals("1")) { // for driver
            mCarType = intent.getExtras().getString("cartype");
            mCarNumber = intent.getExtras().getString("carnumber");
            mCash = intent.getExtras().getString("cash");
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        builder = new PlacePicker.IntentBuilder();
        myStartLocation = (TextView) findViewById(R.id.myStartLocation);
        myGoalLocation = (TextView) findViewById(R.id.myGoalLocation);
        mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);

        /*
            출발지 입력 설정
        */
        //myStartLocation.setOnItemClickListener(mAutocompleteClickListener);
        //myStartLocation.setAdapter(mPlacesAdapter);
        Button myStartLocationBtn = (Button) findViewById(R.id.myStartLocationBtn);
        myStartLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(SearchPathActivity.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_START_FLAG);

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), SearchPathActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(SearchPathActivity.this, "Google Play Services is not available.", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
            도착지 입력 설정
        */
        // myGoalLocation.setOnItemClickListener(mAutocompleteClickListener);
        //myGoalLocation.setAdapter(mPlacesAdapter);
        Button myGoalLocationBtn = (Button) findViewById(R.id.myGoalLocationBtn);
        myGoalLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(SearchPathActivity.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_GOAL_FLAG);

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), SearchPathActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(SearchPathActivity.this, "Google Play Services is not available.", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
            최단시간 결로 검색학기 버튼 클릭 이벤트
         */
        directPathBtn = (Button) findViewById(R.id.directPathBtn);
        directPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tmap 으로 찾아주기~!! 'mStartLatLag'는 출발지 위도 경도 && 'mGoalLatLag'는 도착지 위도 경도
                Intent intent = new Intent(SearchPathActivity.this, PathGuideActivity.class);

                intent.putExtra("START_LAT",String.valueOf(mStartLatLag.latitude));
                intent.putExtra("START_LNG",String.valueOf(mStartLatLag.longitude));
                intent.putExtra("GOAL_LAT",String.valueOf(mGoalLatLag.latitude));
                intent.putExtra("GOAL_LNG",String.valueOf(mGoalLatLag.longitude));

                startActivity(intent);


            }
        });


        /*
            충전소 경유 경로 검색학기 버튼 클릭 이벤트
         */
        dropByStationBtn = (Button) findViewById(R.id.dropByPathBtn);
        dropByStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    출발지&도차지 입력 완성 여부 확인
                */
                if (!(selectStartBool && selectGoalBool)) {
                    Toast.makeText(SearchPathActivity.this, "출발지와 목적지를 바르게 입력하세요", Toast.LENGTH_SHORT).show();
                }

                /*
                    운전자가 자신의 차량 정보를 입력하지 않은 경우 입력하도록 한다.
                 */
                else if (!mUserType.equals("1")) {
                    if (mFlags == 0) {
                        String[] car_str = getResources().getStringArray(R.array.carSpinnerArray);
                        ArrayAdapter<String> mCarAdapter = new ArrayAdapter<String>(SearchPathActivity.this, android.R.layout.simple_spinner_dropdown_item, car_str);
                        final Spinner mCarSpinner = new Spinner(SearchPathActivity.this);
                        mCarSpinner.setAdapter(mCarAdapter);
                        //차 종류 선택시 이벤트 (충전소 종류 식별자)
                        mCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    mCarType = "선택안함";
                                } else if (position == 4) {
                                    mCarType = "상";
                                } else if ((position == 1) || (position == 6)) {
                                    mCarType = "콤보";
                                } else {
                                    mCarType = "차데모";
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                mCarType = "선택안함";
                            }
                        });

                        AlertDialog alert = new AlertDialog.Builder(SearchPathActivity.this)
                                .setTitle("SELECT YOUR CAR")
                                .setMessage("충전소 검색을 위해 차종류를 입력하셔야 합니다.")
                                .setView(mCarSpinner)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (mCarType.equals("선택안함")) {
                                            Toast.makeText(SearchPathActivity.this, "차 종류를 입력해 주세요", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mFlags = 1;
                                        }
                                    }
                                }).show();
                    }

                    /*
                        차량 정보 및 출발지점, 목적지가 바르게 입력된 경우
                    */
                    else {
                        // 서버에 위도&경도범위&충전소type 보내고, 충전소 정보 받아오기
                        // 리스트뷰에 도착지까지 소요시간 & 충전소까지 걸리는 시간 입력
                        // 리스트뷰 클릭시 해당 경로 안내해주는 기능~!!


                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_START_FLAG:
                    Place place1 = PlacePicker.getPlace(data, this);
                    myStartLocation.setText(place1.getName() + ", " + place1.getAddress());
                    mStartLatLag = place1.getLatLng();
                    selectStartBool = true;
                    break;
                case PLACE_PICKER_GOAL_FLAG:
                    Place place2 = PlacePicker.getPlace(data, this);
                    myGoalLocation.setText(place2.getName() + ", " + place2.getAddress());
                    mGoalLatLag = place2.getLatLng();
                    selectGoalBool = true;
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };

    private class ShowSearchedStation extends AsyncTask<String, Void, ArrayList<String>> {
        /* 위도&경도 범위, 차종에 따른 검색 결과에 따른 충전소 보여주기 */
        ArrayList<String> mStation;
        ArrayList<LatLng> mPosition = new ArrayList<LatLng>();

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            UrlConnection urlconn = new UrlConnection();

            try {
                mStation = urlconn.GetDropByStation(strings[0], strings[1], strings[2], strings[3], strings[4]);
                for (int i = 0; i < mStation.size(); i++) {
                    JSONObject jo = new JSONObject(mStation.get(i));
                    String pos = jo.getString("map");
                    String[] poss = pos.split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(poss[0]), Double.parseDouble(poss[1]));
                    mPosition.add(latLng);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mStation;
        }

        /*
             주유소 정보 listview에 띄우기
        */
        protected void onPostExecute(ArrayList<String> items) {
            // 주유소 리스트뷰 갱신
            /*mAdapter.clear();
            try {
                for (int i=0;i<items.size();i++) {
                    JSONObject jo = new JSONObject(items.get(i));
                    location = getLocation();

                    //distance between station and my location[Km]
                    Calculate_Distance mDistance = new Calculate_Distance();
                    double distance =
                            mDistance.distance(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    Double.parseDouble(jo.getString("map").split(",")[0]),
                                    Double.parseDouble(jo.getString("map").split(",")[1]),"K");

                    mAdapter.addItem(jo.getString("address"),100,distance);

                }
                mAdapter.notifyDataSetChanged();
                e.printStackTrace();
            }*/

        }
    }

}