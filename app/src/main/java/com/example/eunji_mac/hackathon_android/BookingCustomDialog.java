package com.example.eunji_mac.hackathon_android;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.eunji_mac.hackathon_android.R;
import com.google.android.gms.maps.GoogleMap;

import org.w3c.dom.Text;

public class BookingCustomDialog extends Dialog{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.book_custom_dialog);
        setLayout();
        setPlugInfoText(mPluggerInfo);
        //setClickListener(mLeftClickListener , mRightClickListener);
    }

    public BookingCustomDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }

    public BookingCustomDialog(Context context ,
                               String stationreport ,
                               View.OnClickListener leftListener ,
                               View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mPluggerInfo = stationreport;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }
    private void setPlugInfoText(String mPluggerInfo){
        mPluggerInfoView.setText("성공이다 이년아");
    }

    private void setClickListener(View.OnClickListener left , View.OnClickListener right){
        if(left!=null && right!=null){
            mLeftButton.setOnClickListener(left);
            mRightButton.setOnClickListener(right);
        }else if(left!=null && right==null){
            mLeftButton.setOnClickListener(left);
        }else {

        }
    }

    private String mPluggerInfo;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    private TextView mPluggerInfoView;
    private Button mLeftButton;
    private Button mRightButton;

    private void setLayout(){
        TextView mPluggerInfoView = (TextView) findViewById(R.id.pluggedtext);
        Button mLeftButton = (Button) findViewById(R.id.bookBtn);
        Button mRightButton = (Button) findViewById(R.id.cancelBtn);
    }
}
