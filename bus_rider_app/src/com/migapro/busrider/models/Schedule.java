package com.migapro.busrider.models;

import java.util.ArrayList;

/**
 * Schedule class represents a time table for bus stop.
 * @author Miga
 *
 */
public class Schedule {
	
	private String mDaysOfOperation;
	private ArrayList<Time> mTimes;
	
	public Schedule() {
		mTimes = new ArrayList<Time>();
	}
	
	public String getDaysOfOperation() {
		return mDaysOfOperation;
	}
	
	public ArrayList<Time> getTimes() {
		return mTimes;
	}
	
	public void setDaysOfOperation(String daysOfOperation) {
		mDaysOfOperation = daysOfOperation;
	}
	
	public void addTime(Time time) {
		mTimes.add(time);
	}

}
