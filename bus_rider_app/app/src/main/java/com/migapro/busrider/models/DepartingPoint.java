package com.migapro.busrider.models;

import java.util.ArrayList;


/**
 * DepartingPoint represents a bus stop where it contains departing times.
 * @author Miga
 *
 */
public class DepartingPoint {
	
	private String mDepartFrom;
	private ArrayList<Schedule> mSchedules;
	private ArrayList<BusStopLocation> mBusStops;
	
	public DepartingPoint() {
		mSchedules = new ArrayList<Schedule>();
		mBusStops = new ArrayList<BusStopLocation>();
	}
	
	public String getDepartFrom() {
		return mDepartFrom;
	}
	
	public ArrayList<Schedule> getSchedules() {
		return mSchedules;
	}

    public Schedule getSchedule(int index) {
        return mSchedules.get(index);
    }
	
	public ArrayList<BusStopLocation> getBusStops() {
		return mBusStops;
	}
	
	public void setDepartFrom(String departFrom) {
		mDepartFrom = departFrom;
	}
	
	public void addSchedule(Schedule schedule) {
		mSchedules.add(schedule);
	}
	
	public void addBusStop(BusStopLocation busStop) {
		mBusStops.add(busStop);
	}

}
