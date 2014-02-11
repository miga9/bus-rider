package com.migapro.busrider;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnItemSelectedListener {

	// dummy data
	ArrayList<String> entries;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.addTab(actionBar.newTab().setText("Schedule")
				.setTabListener(new ActionBarTabListener(new ScheduleFragment())));
		actionBar.addTab(actionBar.newTab().setText("Route")
				.setTabListener(new ActionBarTabListener(new RouteFragment())));
		actionBar.addTab(actionBar.newTab().setText("Elapsed\nTime")
				.setTabListener(new ActionBarTabListener(new ElapsedTimeFragment())));

		attachActionBarSpinner();
	}
	
	private void attachActionBarSpinner() {
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		if (titleId == 0)
			titleId = R.id.action_bar_title;
		View titleView = findViewById(titleId);
		Spinner spinner = initActionBarSpinner();

		ViewGroup parent = (ViewGroup) titleView.getParent();
		int index = parent.indexOfChild(titleView);
		parent.removeView(titleView);
		parent.removeView(spinner);
		parent.addView(spinner, index);
	}
	
	private Spinner initActionBarSpinner() {
		Spinner spinner = (Spinner) getLayoutInflater().inflate(R.layout.actionbar_spinner, null);
		spinner.setOnItemSelectedListener(this);
		
		// dummy data
		entries = new ArrayList<String>();
		entries.add("Centre Bus A");
		entries.add("Bus B Express");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, entries);
		adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		return spinner;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class ActionBarTabListener implements ActionBar.TabListener {
		
		private Fragment fragment;
		
		public ActionBarTabListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
		Toast.makeText(this, entries.get(position) + " loaded", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
