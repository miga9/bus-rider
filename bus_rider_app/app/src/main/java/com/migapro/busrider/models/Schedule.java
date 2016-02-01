package com.migapro.busrider.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Schedule class represents a time table for bus stop.
 * @author Miga
 *
 */
public class Schedule implements Serializable {
	
	private String mDaysOfOperation;
	private String mBusMapId;
	private ArrayList<Time> mTimes;
	
	public Schedule() {
		mTimes = new ArrayList<>();
	}
	
	public String getDaysOfOperation() {
		return mDaysOfOperation;
	}

	public String getBusMapId() {
		return mBusMapId;
	}
	
	public ArrayList<Time> getTimes() {
		return mTimes;
	}
	
	public void setDaysOfOperation(String daysOfOperation) {
		mDaysOfOperation = daysOfOperation;
	}

	public void setBusMapId(String busMapId) {
		mBusMapId = busMapId;
	}
	
	public void addTime(Time time) {
		mTimes.add(time);
	}

}
