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
	
	public Bus(String busName, ArrayList<DepartingPoint> departingPoints) {
		mBusName = busName;
		mDepartingPoints = departingPoints;
	}

}
