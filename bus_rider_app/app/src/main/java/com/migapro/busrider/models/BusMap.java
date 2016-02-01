package com.migapro.busrider.models;

import java.io.Serializable;
import java.util.ArrayList;

public class BusMap implements Serializable {

    private ArrayList<BusStop> mBusStops;
    private ArrayList<LatLngData> mWaypoints;

    public BusMap() {
        mBusStops = new ArrayList<BusStop>();
        mWaypoints = new ArrayList<LatLngData>();
    }

    public ArrayList<BusStop> getBusStops() {
        return mBusStops;
    }

    public void addBusStop(BusStop busStop) {
        mBusStops.add(busStop);
    }

    public ArrayList<LatLngData> getWaypoints() {
        return mWaypoints;
    }

    public void addWaypoint(LatLngData waypoint) {
        mWaypoints.add(waypoint);
    }
}
