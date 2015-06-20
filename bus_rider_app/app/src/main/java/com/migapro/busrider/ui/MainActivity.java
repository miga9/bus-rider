package com.migapro.busrider.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.migapro.busrider.BusRiderApplication;
import com.migapro.busrider.R;
import com.migapro.busrider.config.FeatureFlags;
import com.migapro.busrider.models.Bus;
import com.migapro.busrider.models.Time;
import com.migapro.busrider.parser.BusXmlPullParser;
import com.migapro.busrider.utility.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

	private BusXmlPullParser mParser;
	private ArrayList<String> mBusNames;
	private Bus mCurrentBus;

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;

    private int mBusIndex;
    private int mDeparturePointIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadBusNames();
            initBusDataSelections();

            checkRateMyApp();
        } else {
            restoreState(savedInstanceState);
        }

        loadCurrentBusData();

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

    private void initBusDataSelections() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        mBusIndex = sp.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = sp.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
    }

    private void restoreState(Bundle savedInstanceState) {
        mBusNames = savedInstanceState.getStringArrayList(Constants.BUS_NAMES_KEY);
        mBusIndex = savedInstanceState.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = savedInstanceState.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
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

        ArrayAdapter<String> titleSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_title, mBusNames);
        titleSpinnerAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_light);
        Spinner titleSpinner = (Spinner) findViewById(R.id.title_spinner);
        titleSpinner.setAdapter(titleSpinnerAdapter);
        titleSpinner.setSelection(mBusIndex);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mBusIndex != position) {
                    mBusIndex = position;
                    mDeparturePointIndex = 0;

                    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    editor.putInt(Constants.BUS_INDEX_KEY, mBusIndex);
                    editor.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
                    editor.commit();

                    loadCurrentBusData();
                    updateCurrentBusData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayout departsFromLayout = (LinearLayout) findViewById(R.id.depart_from_layout);
        departsFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartFromDialog();
            }
        });
        findViewById(R.id.depart_from_layout).setEnabled(scheduleExists());

        TextView departsFrom = (TextView) findViewById(R.id.depart_from);
        departsFrom.setText(getString(R.string.departs_from) + mCurrentBus.getDepartingPoint(mDeparturePointIndex));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        mViewPagerAdapter.setTitles(mCurrentBus.getDaysOfOperation(mDeparturePointIndex));
        mViewPager.setAdapter(mViewPagerAdapter);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertabstrip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.light_accent));
    }

    private void updateCurrentBusData() {
        findViewById(R.id.depart_from_layout).setEnabled(scheduleExists());
        ((TextView) findViewById(R.id.depart_from)).setText(getString(R.string.departs_from) + mCurrentBus.getDepartingPoint(mDeparturePointIndex));

        mViewPagerAdapter.setTitles(mCurrentBus.getDaysOfOperation(mDeparturePointIndex));
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);
    }

    private boolean scheduleExists() {
        return !mCurrentBus.getTimes(mDeparturePointIndex, 0).isEmpty();
    }

    private void showDepartFromDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.departs_from))
                .setSingleChoiceItems(mCurrentBus.getDepartingPointsStrings(mDeparturePointIndex), mDeparturePointIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDeparturePointIndex = which;

                        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                        editor.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
                        editor.commit();

                        updateCurrentBusData();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void checkRateMyApp() {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.BUS_NAMES_KEY, mBusNames);
        outState.putInt(Constants.BUS_INDEX_KEY, mBusIndex);
        outState.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
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
                processActionMapSelected();
                return true;
            case R.id.action_share:
                showShareAppChooser();
                return true;
            case R.id.action_version_info:
                showVersionInfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void processActionMapSelected() {
        if (FeatureFlags.MAPS) {
            startMapActivity();
        } else {
            Toast.makeText(this, "Map feature coming soon...", Toast.LENGTH_LONG).show();
        }
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(Constants.MAP_TITLE_KEY, mCurrentBus.getBusName());
        intent.putStringArrayListExtra(Constants.MAP_BUSSTOP_TITLES_KEY, mCurrentBus.getBusStopTitles());
        intent.putParcelableArrayListExtra(Constants.MAP_BUSSTOP_LATNLNGS_KEY, mCurrentBus.getBusStopLatLngs());
        startActivity(intent);
    }

    private void showShareAppChooser() {
        String shareMessage = getString(R.string.share_message) + getPackageName();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    private void showVersionInfoDialog() {
        final AlertDialog versionInfoDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.version_info_title) + getAppVersionNumber())
                .setMessage(R.string.version_info_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        versionInfoDialog.show();
        ((TextView)versionInfoDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getAppVersionNumber() {
        String appVersionNumber = "";
        try {
            appVersionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException nameNotFoundException) {
            nameNotFoundException.printStackTrace();
        }
        return appVersionNumber;
    }

    public ArrayList<Time> getTimeList(int scheduleIndex) {
        return mCurrentBus.getTimes(mDeparturePointIndex, scheduleIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker tracker = ((BusRiderApplication) getApplication()).getTracker();
        tracker.setScreenName(Constants.ANALYTICS_MAIN_SCREEN);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
