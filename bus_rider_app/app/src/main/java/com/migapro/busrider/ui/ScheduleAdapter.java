package com.migapro.busrider.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.migapro.busrider.models.Time;
import com.migapro.busrider.utility.Constants;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Time> mData;
    private Typeface mRobotoLight;

    public ScheduleAdapter(Context context, ArrayList<Time> data) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
        mRobotoLight = Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_LIGHT_PATH);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mData.get(position).getHours());

        return convertView;
    }

    public void setData(ArrayList<Time> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
