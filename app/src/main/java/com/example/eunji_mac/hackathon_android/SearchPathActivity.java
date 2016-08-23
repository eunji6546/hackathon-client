package com.example.eunji_mac.hackathon_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;

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

public class SearchPathActivity extends AppCompatActivity {

    String mCarType;
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

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY =
            new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_path);

        // 폰트 변경
        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mText1 = (TextView) findViewById(R.id.text1);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Stark.OTF");
        mTitle.setTypeface(tf);
        mText1.setTypeface(tf);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        builder = new PlacePicker.IntentBuilder();
        myStartLocation = (TextView) findViewById(R.id.myStartLocation);
        myGoalLocation = (TextView) findViewById(R.id.myGoalLocation);
        mPlacesAdapter = new PlacesAutoCompleteAdapter(this,
                android.R.layout.simple_list_item_1, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);



        /*
            최단시간 경로
         */
        /*
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
*/

        /*
            충전소 경유 경로 검색학기 버튼 클릭 이벤트
         */
        dropByStationBtn = (Button) findViewById(R.id.dropByPathBtn);
        dropByStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    출발지&도착지 입력 완성 여부 확인
                */
                if (!(selectStartBool && selectGoalBool)) {
                    Toast.makeText(SearchPathActivity.this, "출발지와 목적지를 바르게 입력하세요", Toast.LENGTH_SHORT).show();
                }

                /*
                    운전자가 자신의 차량 정보를 입력하지 않은 경우 입력하도록 한다.
                 */
                else if (AccountActivity.mUserType == 0) {

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
                                        Intent intent2 = new Intent(SearchPathActivity.this, PathGuideActivity2.class);

                                        intent2.putExtra("START_LAT",String.valueOf(mStartLatLag.latitude));
                                        intent2.putExtra("START_LNG",String.valueOf(mStartLatLag.longitude));
                                        intent2.putExtra("GOAL_LAT",String.valueOf(mGoalLatLag.latitude));
                                        intent2.putExtra("GOAL_LNG",String.valueOf(mGoalLatLag.longitude));

                                        startActivity(intent2);
                                    }
                                }
                            }).show();

                }

                /* 로그인 한 사용자일때, 차량은 어카운트에서 가져옴 */
                else {

                    Intent intent3 = new Intent(SearchPathActivity.this, PathGuideActivity2.class);

                    intent3.putExtra("CAR_TYPE",AccountActivity.mCarType);
                    intent3.putExtra("START_LAT",String.valueOf(mStartLatLag.latitude));
                    intent3.putExtra("START_LNG",String.valueOf(mStartLatLag.longitude));
                    intent3.putExtra("GOAL_LAT",String.valueOf(mGoalLatLag.latitude));
                    intent3.putExtra("GOAL_LNG",String.valueOf(mGoalLatLag.longitude));

                    startActivity(intent3);
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
                    myStartLocation.setText(place1.getName() + "\n" + place1.getAddress());
                    mStartLatLag = place1.getLatLng();
                    selectStartBool = true;
                    break;
                case PLACE_PICKER_GOAL_FLAG:
                    Place place2 = PlacePicker.getPlace(data, this);
                    myGoalLocation.setText(place2.getName() + "\n" + place2.getAddress());
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

    // 출발지 찾기 클릭
    public void mClick1(View v) {
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

    // 도착지 찾기 클릭
    public void mClick2(View v) {
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
}