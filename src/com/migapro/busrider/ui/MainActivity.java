package com.migapro.busrider.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.migapro.busrider.R;
import com.migapro.busrider.R.id;
import com.migapro.busrider.R.layout;
import com.migapro.busrider.R.menu;

public class MainActivity extends ActionBarActivity implements OnNavigationListener {

	ViewPager viewPager;
	ArrayList<String> entries; // dummy data
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// dummy data
		entries = new ArrayList<String>();
		entries.add("Centre Bus A");
		entries.add("Bus B Express");
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, entries);
		spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(spinnerAdapter, this);
		
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(viewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long timeId) {
		// TODO Auto-generated method stub
		return false;
	}

}
