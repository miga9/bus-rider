package com.migapro.busrider.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * BusStopLocation class represents bus stop location in Google Maps.
 * @author Miga
 *
 */
public class BusStopLocation {
	
	private String mTitle;
	private LatLng mLatLng;
	
	public BusStopLocation(String title, int latitude, int longtitude) {
		mTitle = title;
		mLatLng = new LatLng(latitude, longtitude);
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public LatLng getLatLng() {
		return mLatLng;
	}

}
