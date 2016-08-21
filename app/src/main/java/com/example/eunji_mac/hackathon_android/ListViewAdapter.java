package com.example.eunji_mac.hackathon_android;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<StationViewItem> listViewItemList = new ArrayList<StationViewItem>() ;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stationlistview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView addressTextView = (TextView) convertView.findViewById(R.id.address) ;
        TextView timeRequiredTextView = (TextView) convertView.findViewById(R.id.timeRequired) ;
        TextView pathLengthTextView = (TextView) convertView.findViewById(R.id.pathLength) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        StationViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        addressTextView.setText(listViewItem.getAddress());
        timeRequiredTextView.setText(listViewItem.getRequiredTime().toString());
        pathLengthTextView.setText(String.format("%.1f", listViewItem.getPathLength())+" km");

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void addItem(String address, Integer timeRequired, double pathLengh) {
        StationViewItem item = new StationViewItem();

        item.setAddress(address);
        item.setTime(timeRequired);
        item.setPathLength(pathLengh);

        listViewItemList.add(item);
    }
    public void clear() {
        listViewItemList.clear();
    }
}