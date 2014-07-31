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
	
	public BusStopLocation() {

	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public LatLng getLatLng() {
		return mLatLng;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public void setLatLng(int latitude, int longtitude) {
		mLatLng = new LatLng(latitude, longtitude);
	}

}
