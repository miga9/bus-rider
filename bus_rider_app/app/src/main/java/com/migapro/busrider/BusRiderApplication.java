package com.migapro.busrider;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class BusRiderApplication extends Application {

    public BusRiderApplication() {
        super();
    }

    public synchronized Tracker getTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        Tracker tracker = analytics.newTracker(getString(R.string.analytics_api_key));
        return tracker;
    }

}
