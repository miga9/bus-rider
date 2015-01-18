package com.migapro.busrider.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.migapro.busrider.R;
import com.migapro.busrider.models.Bus;
import com.migapro.busrider.parser.BusXmlPullParser;
import com.migapro.busrider.utility.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

	private BusXmlPullParser mParser;
	private ArrayList<String> mBusNames;
	private Bus mCurrentBus;

    private ScheduleAdapter scheduleAdapter;
    private int mBusIndex;
    private int mDeparturePointIndex;
    private int mScheduleIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadBusNames();
        } else {
            restoreState(savedInstanceState);
        }

        mBusIndex = 0;
        mDeparturePointIndex = 0;
        mScheduleIndex = 0;

        loadBusData();

        initViews();
	}

    private void loadBusNames() {
        try {
            mParser = new BusXmlPullParser();
            mBusNames = mParser.readBusNames(getAssets().open(Constants.BUS_DATA_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void loadBusData() {
        try {
            if (mParser == null) {
                mParser = new BusXmlPullParser();
            }
            mCurrentBus = mParser.readABusData(getAssets().open(Constants.BUS_DATA_PATH), mBusIndex);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_title, mBusNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        Spinner titleSpinner = (Spinner) findViewById(R.id.title_spinner);
        titleSpinner.setAdapter(spinnerAdapter);
        titleSpinner.setOnItemSelectedListener(this);

        ListView listView = (ListView) findViewById(R.id.schedule_listview);
        scheduleAdapter = new ScheduleAdapter(this, mCurrentBus.getTimes(mDeparturePointIndex, mScheduleIndex));
        listView.setAdapter(scheduleAdapter);
    }

    private void restoreState(Bundle savedInstanceState) {
        mBusNames = savedInstanceState.getStringArrayList(Constants.BUS_NAMES_KEY);
    }

    private void updateBusData() {
        loadBusData();
        scheduleAdapter.setData(mCurrentBus.getTimes(mDeparturePointIndex, mScheduleIndex));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.BUS_NAMES_KEY, mBusNames);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(Constants.MAP_TITLE_KEY, mCurrentBus.getBusName());
                startActivity(intent);
                return true;
            case R.id.action_share:
                String shareMessage = Constants.SHARE_MESSAGE + getPackageName();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share this app"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBusIndex = position;
        updateBusData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public Bus getCurrentBus() {
        return mCurrentBus;
    }
}
