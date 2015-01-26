package com.migapro.busrider.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.migapro.busrider.utility.Constants;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.FRAGMENT_SCHEDULE_INDEX_KEY, position);

        Fragment fragment = new ScheduleFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
    }
}
