package com.migapro.busrider.models;

import java.util.ArrayList;


/**
 * Bus class represents a route its buses take.
 * @author Miga
 *
 */
public class Bus {
	
	private String mBusName;
	private ArrayList<DepartingPoint> mDepartingPoints;
	
	public Bus() {
		mDepartingPoints = new ArrayList<DepartingPoint>();
	}
	
	public String getBusName() {
		return mBusName;
	}

    public ArrayList<DepartingPoint> getDepartingPoints() {
        return mDepartingPoints;
    }

    public ArrayList<Time> getTimes(int departureIndex, int scheduleIndex) {
        return mDepartingPoints.get(departureIndex).getSchedule(scheduleIndex).getTimes();
    }
	
	public void setBusname(String busName) {
		mBusName = busName;
	}
	
	public void addDepartingPoint(DepartingPoint departingPoint) {
		mDepartingPoints.add(departingPoint);
	}

}
