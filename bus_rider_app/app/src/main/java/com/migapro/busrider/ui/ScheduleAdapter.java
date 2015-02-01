package com.migapro.busrider.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.migapro.busrider.R;
import com.migapro.busrider.models.Time;

import java.util.ArrayList;

public class ScheduleAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Time> mData;

    public ScheduleAdapter(Context context, ArrayList<Time> data) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
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
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.schedule_list_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.hours);
        tv.setText(mData.get(position).getHours());

        TextView  minutesTextView = (TextView) convertView.findViewById(R.id.minutes);
        minutesTextView.setText(concatenateMinutesString(mData.get(position).getMinutes()));

        return convertView;
    }

    private String concatenateMinutesString(ArrayList<String> minutesList) {
        StringBuilder strBuilder = new StringBuilder();
        for (String minutes : minutesList) {
            strBuilder.append(minutes).append("    ");
        }
        return strBuilder.toString();
    }

    public void setData(ArrayList<Time> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
