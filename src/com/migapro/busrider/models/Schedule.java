package com.migapro.busrider.models;

import java.util.ArrayList;

import android.text.format.Time;

/**
 * Schedule class represents a time table for bus stop.
 * @author Miga
 *
 */
public class Schedule {
	
	private String mDaysOfOperation;
	private ArrayList<Time> mTimes;
	
	public Schedule(String daysOfOperation, ArrayList<Time> times) {
		mDaysOfOperation = daysOfOperation;
		mTimes = times;
	}

}
