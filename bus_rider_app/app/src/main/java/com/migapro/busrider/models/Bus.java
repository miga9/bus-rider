package com.migapro.busrider.models;


import java.io.Serializable;
import java.util.ArrayList;


/**
 * Bus class represents a route its buses take.
 * @author Miga
 *
 */
public class Bus implements Serializable {
	
	private String mBusName;
	private ArrayList<DepartingPoint> mDepartingPoints;

	public Bus() {
		mDepartingPoints = new ArrayList<>();
	}
	
	public String getBusName() {
		return mBusName;
	}

    public void setBusName(String busName) {
        mBusName = busName;
    }

    public ArrayList<DepartingPoint> getDepartingPoints() {
        return mDepartingPoints;
    }

    public String getDepartingPoint(int departingIndex) {
        return mDepartingPoints.get(departingIndex).getDepartFrom();
    }

    public CharSequence[] getDepartingPointsStrings() {
        CharSequence[] departingPointsStrings = new CharSequence[mDepartingPoints.size()];
        for (int i = 0; i < departingPointsStrings.length; i++) {
            departingPointsStrings[i] = mDepartingPoints.get(i).getDepartFrom();
        }
        return departingPointsStrings;
    }

    public void addDepartingPoint(DepartingPoint departingPoint) {
        mDepartingPoints.add(departingPoint);
    }

    public String[] getDaysOfOperation(int departureIndex) {
        ArrayList<Schedule> schedules = mDepartingPoints.get(departureIndex).getSchedules();

        String[] daysOfOperation = new String[schedules.size()];
        for (int i = 0; i < daysOfOperation.length; i++) {
            daysOfOperation[i] = schedules.get(i).getDaysOfOperation();
        }
        return daysOfOperation;
    }

    public ArrayList<Time> getTimes(int departureIndex, int scheduleIndex) {
        return mDepartingPoints.get(departureIndex).getSchedule(scheduleIndex).getTimes();
    }

    public Schedule getSchedule(int departureIndex, int scheduleIndex) {
        return mDepartingPoints.get(departureIndex).getSchedule(scheduleIndex);
    }
}
