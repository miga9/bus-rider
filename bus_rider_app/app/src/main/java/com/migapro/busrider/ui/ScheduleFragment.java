package com.migapro.busrider.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.migapro.busrider.R;
import com.migapro.busrider.utility.Constants;

public class ScheduleFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int scheduleIndex = getArguments().getInt(Constants.FRAGMENT_SCHEDULE_INDEX_KEY);
        setListAdapter(new ScheduleAdapter(getActivity(), ((MainActivity) getActivity()).getTimeList(scheduleIndex)));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getActivity().getString(R.string.schedule_empty_text));
    }
}
