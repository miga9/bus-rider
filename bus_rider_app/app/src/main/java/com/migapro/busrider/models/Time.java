package com.migapro.busrider.models;

import java.util.ArrayList;

public class Time {
	private String mHours;
	private ArrayList<String> mMinutes;

    public Time() {
        mMinutes = new ArrayList<String>();
    }
	
	public String getHours() {
		return mHours;
	}
	
	public ArrayList<String> getMinutes() {
		return mMinutes;
	}
	
	public void setHours(String hours) {
		mHours = hours;
	}

    public void addMinutes(String minutes) {
        mMinutes.add(minutes);
    }
	
}