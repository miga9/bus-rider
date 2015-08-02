package com.migapro.busrider.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerTabStrip;
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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.migapro.busrider.BusRiderApplication;
import com.migapro.busrider.R;
import com.migapro.busrider.config.FeatureFlags;
import com.migapro.busrider.gcm.RegistrationIntentService;
import com.migapro.busrider.models.BusDataManager;
import com.migapro.busrider.models.Time;
import com.migapro.busrider.network.DataAsyncTask;
import com.migapro.busrider.ui.dialog.RateMyAppDialog;
import com.migapro.busrider.ui.dialog.VersionInfoDialog;
import com.migapro.busrider.utility.Constants;
import com.migapro.busrider.utility.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MainActivity extends ActionBarActivity implements DataAsyncTask.OnDataServiceListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title_spinner) Spinner titleSpinner;
    @Bind(R.id.depart_from) TextView departFrom;
    @Bind(R.id.depart_from_layout) LinearLayout departFromLayout;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.pagertabstrip) PagerTabStrip pagerTabStrip;

    private ArrayAdapter<String> mTitleSpinnerAdapter;
    private ViewPagerAdapter mViewPagerAdapter;
    private ProgressDialog mProgressDialog;

    private BusDataManager mBusDataManager;
    private int mBusIndex;
    private int mDeparturePointIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBusDataManager = new BusDataManager();

        if (!Util.doesFileExist(this, Constants.BUS_DATA_PATH)) {
            startDataAsyncTask();
            initViews();
            return;
        }

        if (savedInstanceState == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (!sharedPref.getBoolean(Constants.KEY_GCM_REGIS_ID_SENT, false)) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

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
        mBusDataManager.loadBusNames(this);
    }

    private void initBusDataSelections() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        mBusIndex = sp.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = sp.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
    }

    private void restoreState(Bundle savedInstanceState) {
        mBusDataManager.setBusNames(savedInstanceState.getStringArrayList(Constants.BUS_NAMES_KEY));

        mBusIndex = savedInstanceState.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = savedInstanceState.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
    }

    private void loadCurrentBusData() {
        mBusDataManager.loadCurrentBusData(this, mBusIndex);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitleSpinnerAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_title, mBusDataManager.getBusNames());
        mTitleSpinnerAdapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_light);

        titleSpinner.setAdapter(mTitleSpinnerAdapter);
        titleSpinner.setSelection(mBusIndex);

        boolean scheduleExists = false;
        String[] daysOfOperation = new String[0];

        if (mBusDataManager.getCurrentBus() != null) {
            departFrom.setText(getString(R.string.departs_from) + mBusDataManager.getDepartingPoint(mDeparturePointIndex));

            scheduleExists = mBusDataManager.scheduleExists(mDeparturePointIndex);
            daysOfOperation = mBusDataManager.getDaysOfOperation(mDeparturePointIndex);
        }

        departFromLayout.setEnabled(scheduleExists);

        mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        mViewPagerAdapter.setTitles(daysOfOperation);
        viewPager.setAdapter(mViewPagerAdapter);

        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.light_accent));
    }

    private void updateCurrentBusUI() {
        departFromLayout.setEnabled(mBusDataManager.scheduleExists(mDeparturePointIndex));
        departFrom.setText(getString(R.string.departs_from) + mBusDataManager.getDepartingPoint(mDeparturePointIndex));

        mViewPagerAdapter.setTitles(mBusDataManager.getDaysOfOperation(mDeparturePointIndex));
        mViewPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }

    private void updateBusNamesUI() {
        mTitleSpinnerAdapter.notifyDataSetChanged();
        titleSpinner.setSelection(0);
    }

    private void showDepartFromDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.departs_from))
                .setSingleChoiceItems(
                        mBusDataManager.getDepartingPointsStrings(mDeparturePointIndex),
                        mDeparturePointIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDeparturePointIndex = which;

                        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                        editor.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
                        editor.commit();

                        updateCurrentBusUI();
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
        RateMyAppDialog rateMyAppDialog = new RateMyAppDialog(this);
        rateMyAppDialog.showDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.BUS_NAMES_KEY, mBusDataManager.getBusNames());
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
            case R.id.action_download:
                startDataAsyncTask();
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
        intent.putExtra(Constants.MAP_TITLE_KEY, mBusDataManager.getBusName());
        intent.putStringArrayListExtra(Constants.MAP_BUSSTOP_TITLES_KEY, mBusDataManager.getBusStopTitles());
        // TODO Don't have this info, yet
        //intent.putParcelableArrayListExtra(Constants.MAP_BUSSTOP_LATNLNGS_KEY, mCurrentBus.getBusStopLatLngs());
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
        VersionInfoDialog versionInfoDialog = new VersionInfoDialog(this);
        versionInfoDialog.showDialog();
    }

    private void startDataAsyncTask() {
        DataAsyncTask dataAsyncTask = new DataAsyncTask();
        dataAsyncTask.setOnDataServiceListener(this);
        dataAsyncTask.execute();
    }

    public ArrayList<Time> getTimeList(int scheduleIndex) {
        return mBusDataManager.getTimes(mDeparturePointIndex, scheduleIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker tracker = ((BusRiderApplication) getApplication()).getTracker();
        tracker.setScreenName(Constants.ANALYTICS_MAIN_SCREEN);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @OnItemSelected(R.id.title_spinner)
    public void onTitleSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mBusIndex != position) {
            mBusIndex = position;
            mDeparturePointIndex = 0;

            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt(Constants.BUS_INDEX_KEY, mBusIndex);
            editor.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
            editor.commit();

            loadCurrentBusData();
            updateCurrentBusUI();
        }
    }

    @OnClick(R.id.depart_from_layout)
    public void onDepartFromLayoutClick(View v) {
        showDepartFromDialog();
    }

    @Override
    public void onDataServiceStart() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(getString(R.string.progress_msg_downloading));
        mProgressDialog.show();
    }

    @Override
    public void onDataServiceComplete() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        loadBusNames();
        loadCurrentBusData();
        updateBusNamesUI();
        updateCurrentBusUI();
    }
}