package com.example.eunji_mac.hackathon_android;

import android.graphics.drawable.Drawable;
/**
 * Created by juhee on 2016. 8. 20..
 */
public class StationViewItem {
    private String addressStr ;
    private Integer requiredTimeStr ;
    private Integer pathlengthStr;

    public void setAddress(String title) {
        addressStr = title ;
    }
    public void setTime(Integer time) {
        requiredTimeStr = time ;
    }

    public void setPathLength(Integer length) {
        pathlengthStr = length ;
    }

    public String getAddress() {
        return this.addressStr ;
    }
    public Integer getRequiredTime() {
        return this.requiredTimeStr ;
    }
    public Integer getPathLength () {
        return this.pathlengthStr;
    }
}

