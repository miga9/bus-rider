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

    public void setBusname(String busName) {
        mBusName = busName;
    }

    public ArrayList<DepartingPoint> getDepartingPoints() {
        return mDepartingPoints;
    }

    public String getDepartingPoint(int departingIndex) {
        return mDepartingPoints.get(departingIndex).getDepartFrom();
    }

    public CharSequence[] getDepartingPointsStrings(int departingIndex) {
        CharSequence[] departingPointsStrings = new CharSequence[mDepartingPoints.size()];
        for (int i = 0; i < departingPointsStrings.length; i++) {
            departingPointsStrings[i] = mDepartingPoints.get(i).getDepartFrom();
        }
        return departingPointsStrings;
    }

    public void addDepartingPoint(DepartingPoint departingPoint) {
        mDepartingPoints.add(departingPoint);
    }


    public ArrayList<Time> getTimes(int departureIndex, int scheduleIndex) {
        return mDepartingPoints.get(departureIndex).getSchedule(scheduleIndex).getTimes();
    }



}
