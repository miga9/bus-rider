package com.migapro.busrider;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	
	private static final String[] PAGE_TITLES = {"Schedule", "Map", "Elapsed Time"};

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new ScheduleFragment();
		case 1:
			return new MapFragment();
		case 2:
			return new ElapsedTimeFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return PAGE_TITLES.length;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return PAGE_TITLES[position];
	}
}
