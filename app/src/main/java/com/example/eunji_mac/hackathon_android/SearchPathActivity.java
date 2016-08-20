package com.example.eunji_mac.hackathon_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    String mUserType; // 1 for driver, 0 for walker
    String mCarType;
    String mCarNumber;
    String mCash;


    private PlacePicker.IntentBuilder builder;
    private PlacesAutoCompleteAdapter mPlacesAdapter;

    //출발지 관련 변수
    private TextView myStartLocation; //입력창
    private LatLng mStartLatLag; //위도, 경도
    private static final int PLACE_PICKER_START_FLAG = 1;


    //도착지 관련 변수
    private TextView myGoalLocation; //입력창
    private LatLng mGoalLatLag; //위도, 경도
    private static final int PLACE_PICKER_GOAL_FLAG = 2;

    private Button directPathBtn;
    private Button dropByStationBtn;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds( new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
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
        mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);

        /*
            출발지 입력 설정
        */
        //myStartLocation.setOnItemClickListener(mAutocompleteClickListener);
        //myStartLocation.setAdapter(mPlacesAdapter);
        Button myStartLocationBtn = (Button)findViewById(R.id.myStartLocationBtn);
        myStartLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(SearchPathActivity.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_START_FLAG);

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(),SearchPathActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(SearchPathActivity.this, "Google Play Services is not available.",Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
            도착지 입력 설정
        */
       // myGoalLocation.setOnItemClickListener(mAutocompleteClickListener);
        //myGoalLocation.setAdapter(mPlacesAdapter);
        Button myGoalLocationBtn = (Button)findViewById(R.id.myGoalLocationBtn);
        myGoalLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(SearchPathActivity.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_GOAL_FLAG);

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(),SearchPathActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(SearchPathActivity.this, "Google Play Services is not available.",Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
            최단시간 결로 검색학기 버튼 클릭 이벤트
         */
        directPathBtn = (Button)findViewById(R.id.directPathBtn);
        directPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tmap 으로 찾아주기~!! 'mStartLatLag'는 출발지 위도 경도 && 'mGoalLatLag'는 도착지 위도 경도





            }
        });


        /*
            충전소 경유 경로 검색학기 버튼 클릭 이벤트
         */
        dropByStationBtn = (Button)findViewById(R.id.dropByPathBtn);
        dropByStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리스트뷰에 도착지까지 소요시간 & 충전소까지 걸리는 시간 입력
                // 리스트뷰 클릭시 해당 경로 안내해주는 기능~!!





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
                    break;
                case PLACE_PICKER_GOAL_FLAG:
                    Place place2 = PlacePicker.getPlace(data, this);
                    myGoalLocation.setText(place2.getName() + ", " + place2.getAddress());
                    mGoalLatLag = place2.getLatLng();
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
}