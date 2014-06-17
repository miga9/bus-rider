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
	
	public DepartingPoint(String departFrom, ArrayList<Schedule> schedules,
			ArrayList<BusStopLocation> busStops) {
		mDepartFrom = departFrom;
		mSchedules = schedules;
		mBusStops = busStops;
	}

}
