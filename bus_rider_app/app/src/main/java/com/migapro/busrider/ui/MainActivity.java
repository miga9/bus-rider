package com.migapro.busrider.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.migapro.busrider.R;
import com.migapro.busrider.models.Bus;
import com.migapro.busrider.parser.BusXmlPullParser;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

	private BusXmlPullParser mParser;
	private ArrayList<String> mBusNames;
	private Bus mCurrentBus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

		try {
			mParser = new BusXmlPullParser();
			mBusNames = mParser.readBusNames(getAssets().open("data/bus_data.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_title, mBusNames);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        Spinner titleSpinner = (Spinner) findViewById(R.id.title_spinner);
        titleSpinner.setAdapter(spinnerAdapter);
        titleSpinner.setOnItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new ScheduleFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            mCurrentBus = mParser.readABusData(getAssets().open("data/bus_data.xml"), 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
