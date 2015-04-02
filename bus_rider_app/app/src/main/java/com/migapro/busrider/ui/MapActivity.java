package com.migapro.busrider.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.migapro.busrider.BusRiderApplication;
import com.migapro.busrider.R;
import com.migapro.busrider.utility.Constants;

import java.util.ArrayList;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();
    }

    private void initViews() {
        String title = getIntent().getStringExtra(Constants.MAP_TITLE_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker tracker = ((BusRiderApplication) getApplication()).getTracker();
        tracker.setScreenName(Constants.ANALYTICS_MAP_SCREEN);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<String> busStopTitles =
                getIntent().getStringArrayListExtra(Constants.MAP_BUSSTOP_TITLES_KEY);
        ArrayList<LatLng> busStopLatLngs =
                getIntent().getParcelableArrayListExtra(Constants.MAP_BUSSTOP_LATNLNGS_KEY);
    }
}
