package com.migapro.busrider.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.migapro.busrider.BusRiderApplication;
import com.migapro.busrider.R;
import com.migapro.busrider.utility.Constants;

public class MapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String title = getIntent().getStringExtra(Constants.MAP_TITLE_KEY);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Tracker tracker = ((BusRiderApplication) getApplication()).getTracker();
        tracker.setScreenName(Constants.ANALYTICS_MAP_SCREEN);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
