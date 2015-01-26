package com.migapro.busrider.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.migapro.busrider.R;
import com.migapro.busrider.models.Bus;
import com.migapro.busrider.models.Time;
import com.migapro.busrider.parser.BusXmlPullParser;
import com.migapro.busrider.utility.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

	private BusXmlPullParser mParser;
	private ArrayList<String> mBusNames;
	private Bus mCurrentBus;

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;

    private int mBusIndex;
    private int mDeparturePointIndex;
    private int mScheduleIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadBusNames();

            mBusIndex = 0;
            mDeparturePointIndex = 0;
            mScheduleIndex = 0;
        } else {
            restoreState(savedInstanceState);
        }

        loadCurrentBusData();

        initViews();

        rateMyApp();
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

    private void loadCurrentBusData() {
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
        spinnerAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_light);
        Spinner titleSpinner = (Spinner) findViewById(R.id.title_spinner);
        titleSpinner.setAdapter(spinnerAdapter);
        titleSpinner.setOnItemSelectedListener(this);

        LinearLayout departsFromLayout = (LinearLayout) findViewById(R.id.depart_from_layout);
        departsFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartFromDialog();
            }
        });

        TextView departsFrom = (TextView) findViewById(R.id.depart_from);
        departsFrom.setText(getString(R.string.departs_from) + mCurrentBus.getDepartingPoint(mDeparturePointIndex));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        mViewPagerAdapter.setTitles(mCurrentBus.getDaysOfOperation(mDeparturePointIndex));
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void showDepartFromDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.departs_from))
                .setSingleChoiceItems(mCurrentBus.getDepartingPointsStrings(mDeparturePointIndex), mDeparturePointIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDeparturePointIndex = which;
                        mScheduleIndex = 0;

                        updateCurrentBusData();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void restoreState(Bundle savedInstanceState) {
        mBusNames = savedInstanceState.getStringArrayList(Constants.BUS_NAMES_KEY);
        mBusIndex = savedInstanceState.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = savedInstanceState.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
        mScheduleIndex = savedInstanceState.getInt(Constants.SCHEDULE_INDEX_KEY, 0);
    }

    private void rateMyApp() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        int count = sp.getInt(Constants.RATE_MY_APP_COUNT_KEY, 0);

        if (count < Constants.NUM_OF_VISITS_TO_PROMPT_RATE_THIS_APP) {
            count++;
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Constants.RATE_MY_APP_COUNT_KEY, count);
            editor.commit();

            if (count == Constants.NUM_OF_VISITS_TO_PROMPT_RATE_THIS_APP) {
                showRateMyAppDialog();
            }
        }
    }

    private void showRateMyAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.rate_this_app))
                .setMessage(getString(R.string.rate_this_app_msg))
                .setPositiveButton(getString(R.string.rate_this_app_positive),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + getPackageName())));
                            }
                        })
                .setNegativeButton(getString(R.string.rate_this_app_negative),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
    }

    private void updateCurrentBusData() {
        ((TextView) findViewById(R.id.depart_from)).setText(getString(R.string.departs_from) + mCurrentBus.getDepartingPoint(mDeparturePointIndex));
        mViewPagerAdapter.setTitles(mCurrentBus.getDaysOfOperation(mDeparturePointIndex));
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.BUS_NAMES_KEY, mBusNames);
        outState.putInt(Constants.BUS_INDEX_KEY, mBusIndex);
        outState.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
        outState.putInt(Constants.SCHEDULE_INDEX_KEY, mScheduleIndex);
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
        if (mBusIndex != position) {
            mBusIndex = position;
            mDeparturePointIndex = 0;
            mScheduleIndex = 0;

            loadCurrentBusData();
            updateCurrentBusData();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public ArrayList<Time> getTimeList(int scheduleIndex) {
        return mCurrentBus.getTimes(mDeparturePointIndex, scheduleIndex);
    }
}
