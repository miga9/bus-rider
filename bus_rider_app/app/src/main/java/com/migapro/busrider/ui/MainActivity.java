package com.migapro.busrider.ui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.migapro.busrider.network.WorkerFragment;
import com.migapro.busrider.ui.dialog.MsgDialog;
import com.migapro.busrider.ui.dialog.RateMyAppDialog;
import com.migapro.busrider.ui.dialog.SingleChoiceDialog;
import com.migapro.busrider.ui.dialog.VersionInfoDialog;
import com.migapro.busrider.utility.Constants;
import com.migapro.busrider.utility.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MainActivity extends ActionBarActivity implements WorkerFragment.WorkerListener,
        SingleChoiceDialog.OnDialogItemSelectedListener,
        MsgDialog.OnPositiveClickListener {

    private static final int DIALOG_ITEM_ID_DEPART_FROM = 0;
    private static final int DIALOG_MSG_ID_UPDATE_FILE = 0;
    private static final int DIALOG_MSG_ID_FAILURE = 1;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title_spinner) Spinner titleSpinner;
    @Bind(R.id.depart_from) TextView departFrom;
    @Bind(R.id.depart_from_layout) LinearLayout departFromLayout;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.pagertabstrip) PagerTabStrip pagerTabStrip;

    private ArrayAdapter<String> mTitleSpinnerAdapter;
    private ViewPagerAdapter mViewPagerAdapter;
    private WorkerFragment mWorkerFragment;

    private BusDataManager mBusDataManager;
    private int mBusIndex;
    private int mDeparturePointIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBusDataManager = new BusDataManager();

        loadWorkerFragment();

        if (savedInstanceState == null) {
            startGcmRegistrationIfNecessary();

            initBusDataSelections();
            loadBusNames();
            loadCurrentBusData();

            showRateMyAppIfNecessary();
        } else {
            restoreState(savedInstanceState);
        }

        initViews();
    }

    private void loadWorkerFragment() {
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (WorkerFragment) fm.findFragmentByTag("worker");
        if (mWorkerFragment == null) {
            mWorkerFragment = new WorkerFragment();
            fm.beginTransaction().add(mWorkerFragment, "worker").commit();
        }
    }

    private void startGcmRegistrationIfNecessary() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPref.getBoolean(Constants.KEY_GCM_REGIS_ID_SENT, false)) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void initBusDataSelections() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        mBusIndex = sp.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = sp.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
    }

    private void loadBusNames() {
        mBusDataManager.loadBusNames(this);
    }

    private void loadCurrentBusData() {
        mBusDataManager.loadCurrentBusData(this, mBusIndex);
    }

    private void restoreState(Bundle savedInstanceState) {
        mBusDataManager.setBusNames(savedInstanceState.getStringArrayList(Constants.BUS_NAMES_KEY));
        mBusDataManager.setCurrentBus((com.migapro.busrider.models.Bus) savedInstanceState.getSerializable(Constants.KEY_CURRENT_BUS));

        mBusIndex = savedInstanceState.getInt(Constants.BUS_INDEX_KEY, 0);
        mDeparturePointIndex = savedInstanceState.getInt(Constants.DEPARTURE_INDEX_KEY, 0);
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
        // Workaround to update Spinner on unchanged selection when I want
        if (mBusIndex == 0) {
            updateSelectedBus(0);
        }
        titleSpinner.setSelection(0);
    }

    private void showDepartFromDialog() {
        SingleChoiceDialog showDepartFromDialog = SingleChoiceDialog.newInstance(DIALOG_ITEM_ID_DEPART_FROM, R.string.departs_from,
                mBusDataManager.getDepartingPointsStrings(mDeparturePointIndex), mDeparturePointIndex);
        showDepartFromDialog.show(getFragmentManager(), "showDepartFromDialog");
    }

    private void showRateMyAppIfNecessary() {
        if (Util.shouldPromptRateMyApp(this)) {
            showRateMyAppDialog();
        }
    }

    private void showRateMyAppDialog() {
        RateMyAppDialog rateMyAppDialog = new RateMyAppDialog();
        rateMyAppDialog.show(getFragmentManager(), "rateMyAppDialog");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.BUS_NAMES_KEY, mBusDataManager.getBusNames());
        outState.putSerializable(Constants.KEY_CURRENT_BUS, mBusDataManager.getCurrentBus());
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
                showDownloadDialog();
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
        VersionInfoDialog versionInfoDialog = new VersionInfoDialog();
        versionInfoDialog.show(getFragmentManager(), "versionInfoDialog");
    }

    private void showDownloadDialog() {
        MsgDialog msgDialog = MsgDialog.newInstance(DIALOG_MSG_ID_UPDATE_FILE,
                R.string.update_data_title,
                R.string.update_data_msg,
                R.string.update_data_positive,
                R.string.cancel);
        msgDialog.setCancelable(false);
        msgDialog.show(getFragmentManager(), "downloadDialog");
    }

    private void startDataAsyncTask() {
        mWorkerFragment.startDataAsyncTask();
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

        updateDataFileIfNecessary();
    }

    private void updateDataFileIfNecessary() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_NEED_TO_UPDATE_FILE, false)
                || !Util.doesFileExist(this, Constants.BUS_DATA_PATH)) {
            startDataAsyncTask();
        }
    }

    @OnItemSelected(R.id.title_spinner)
    public void onTitleSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mBusIndex != position) {
            updateSelectedBus(position);
        }
    }

    private void updateSelectedBus(int position) {
        mBusIndex = position;
        mDeparturePointIndex = 0;

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt(Constants.BUS_INDEX_KEY, mBusIndex);
        editor.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
        editor.commit();

        loadCurrentBusData();
        updateCurrentBusUI();
    }

    @OnClick(R.id.depart_from_layout)
    public void onDepartFromLayoutClick(View v) {
        showDepartFromDialog();
    }

    @Override
    public void onItemSelected(int id, int which) {
        switch (id) {
            case DIALOG_ITEM_ID_DEPART_FROM:
                mDeparturePointIndex = which;

                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putInt(Constants.DEPARTURE_INDEX_KEY, mDeparturePointIndex);
                editor.commit();

                updateCurrentBusUI();
                break;
        }
    }

    @Override
    public void onPositiveClick(int id) {
        switch (id) {
            case DIALOG_MSG_ID_UPDATE_FILE:
                startDataAsyncTask();
                break;
            case DIALOG_MSG_ID_FAILURE:
                startDataAsyncTask();
                break;
        }
    }

    @Override
    public void onPostExecute(String result) {
        boolean isSuccess = (result != null && !result.isEmpty()
                && result.charAt(0) == '<' && result.charAt(result.length() - 1) == '>');
        if (isSuccess) {
            Util.saveBusData(this, result);
            Util.saveLastUpdatedDate(this);

            loadBusNames();
            updateBusNamesUI();
        }

        showDownloadResultDialog(isSuccess);
    }

    private void showDownloadResultDialog(boolean isSuccess) {
        MsgDialog msgDialog;
        String fragmentTag;

        if (isSuccess) {
            msgDialog = MsgDialog.newInstance(MsgDialog.NO_LISTENER,
                    R.string.download_success_title, R.string.download_success_msg,
                    R.string.ok, MsgDialog.NO_NEGATIVE_BUTTON);
            fragmentTag = "successDialog";
        } else {
            msgDialog = MsgDialog.newInstance(DIALOG_MSG_ID_FAILURE,
                    R.string.download_failure_title, R.string.download_failure_msg,
                    R.string.download_failure_pos, R.string.cancel);
            msgDialog.setCancelable(false);
            fragmentTag = "failureDialog";
        }
        removeDuplicateDialogFragments();

        getFragmentManager().beginTransaction().add(msgDialog, fragmentTag).commitAllowingStateLoss();
    }

    private void removeDuplicateDialogFragments() {
        FragmentManager fm = getFragmentManager();
        DialogFragment duplicateFragmentToRemove = (DialogFragment) fm.findFragmentByTag("failureDialog");
        FragmentTransaction ft = fm.beginTransaction();
        boolean commitTransaction = false;

        if (duplicateFragmentToRemove != null) {
            ft.remove(duplicateFragmentToRemove);
            commitTransaction = true;
        }
        duplicateFragmentToRemove = (DialogFragment) fm.findFragmentByTag("successDialog");
        if (duplicateFragmentToRemove != null) {
            ft.remove(duplicateFragmentToRemove);
            commitTransaction = true;
        }

        if (commitTransaction) {
            ft.commit();
        }
    }
}