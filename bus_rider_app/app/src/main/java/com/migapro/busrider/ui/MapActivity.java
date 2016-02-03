package com.migapro.busrider.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.migapro.busrider.BusRiderApplication;
import com.migapro.busrider.R;
import com.migapro.busrider.models.BusMap;
import com.migapro.busrider.models.BusStop;
import com.migapro.busrider.models.LatLngData;
import com.migapro.busrider.utility.Constants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.days) TextView daysTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra(Constants.MAP_TITLE_KEY);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String days = getIntent().getStringExtra(Constants.KEY_MAP_DAYS);
        if (days != null && !days.isEmpty()) {
            daysTextView.setVisibility(View.VISIBLE);
            daysTextView.setText(getString(R.string.s_route, days));
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
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
        Bundle bundle = getIntent().getBundleExtra(Constants.KEY_MAP_DATA);

        BusMap busMap = (BusMap) bundle.getSerializable(Constants.KEY_BUS_MAP);

        if (googleMap != null) {
            initMarkers(googleMap, busMap);
            initRoutePolyline(googleMap, busMap);

            BusStop firstStop = busMap.getBusStops().get(0);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(firstStop.getLatitude(), firstStop.getLongitude()))
                    .zoom(15)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void initMarkers(GoogleMap googleMap, BusMap busMap) {
        for (BusStop busStop : busMap.getBusStops()) {
            googleMap.addMarker(new MarkerOptions()
                    .title(busStop.getTitle())
                    .position(new LatLng(busStop.getLatitude(), busStop.getLongitude())));
        }
    }

    private void initRoutePolyline(GoogleMap googleMap, BusMap busMap) {
        ArrayList<LatLng> latLngs = convertToLatLngArrayList(busMap.getWaypoints());
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(latLngs)
                .geodesic(false)
                .color(ContextCompat.getColor(this, R.color.accent))
                .width(6);

        googleMap.addPolyline(polylineOptions);
    }

    private ArrayList<LatLng> convertToLatLngArrayList(ArrayList<LatLngData> latLngDataArrayyList) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (LatLngData latLngData : latLngDataArrayyList) {
            latLngs.add(new LatLng(latLngData.getLatitude(), latLngData.getLongitude()));
        }
        return latLngs;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
