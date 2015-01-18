package com.migapro.busrider.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.migapro.busrider.R;
import com.migapro.busrider.models.Bus;
import com.migapro.busrider.parser.BusXmlPullParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

	private BusXmlPullParser mParser;
	private ArrayList<String> mBusNames;
	private Bus mCurrentBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadBusNames();
        } else {
            restoreState(savedInstanceState);
        }

        initViews();

        setFragment();
	}

    private void loadBusNames() {
        try {
            mParser = new BusXmlPullParser();
            mBusNames = mParser.readBusNames(getAssets().open("data/bus_data.xml"));
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
    }

    private void setFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new ScheduleFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        mBusNames = savedInstanceState.getStringArrayList("busNames");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("busNames", mBusNames);
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
                intent.putExtra("title", mCurrentBus.getBusName());
                startActivity(intent);
                return true;
            case R.id.action_share:
                String shareMessage = "Bus Rider https://play.google.com/store/apps/details?id=" + getPackageName();

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
        try {
            if (mParser == null) {
                mParser = new BusXmlPullParser();
            }
            mCurrentBus = mParser.readABusData(getAssets().open("data/bus_data.xml"), position);
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
