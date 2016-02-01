package com.migapro.busrider.models;

import android.content.Context;
import android.util.Log;

import com.migapro.busrider.parser.BusXmlPullParser;
import com.migapro.busrider.utility.Constants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BusDataManager {

    private BusXmlPullParser mParser;
    private ArrayList<String> mBusNames;
    private Bus mCurrentBus;

    public BusDataManager() {
        mBusNames = new ArrayList<String>();
    }

    public void loadBusNames(Context context) {
        try {
            mParser = new BusXmlPullParser();
            FileInputStream fileInputStream = context.openFileInput(Constants.BUS_DATA_PATH);
            mBusNames.clear();
            mBusNames.addAll(mParser.readBusNames(fileInputStream));
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void loadCurrentBusData(Context context, int busIndex) {
        try {
            if (mParser == null) {
                mParser = new BusXmlPullParser();
            }
            FileInputStream fileInputStream = context.openFileInput(Constants.BUS_DATA_PATH);
            mCurrentBus = mParser.readABusData(fileInputStream, busIndex);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public BusMap retrieveBusMapData(Context context, int busIndex, String busMapIndex) {
        BusMap busMap = null;
        try {
            if (mParser == null) {
                mParser = new BusXmlPullParser();
            }
            FileInputStream fileInputStream = context.openFileInput(Constants.BUS_DATA_PATH);
            long start = System.currentTimeMillis();
            busMap = mParser.readBusMapData(fileInputStream, busIndex, busMapIndex);
            Log.d("TAAG", "map " + (System.currentTimeMillis() - start));
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return busMap;
    }

    public void setCurrentBus(Bus bus) {
        mCurrentBus = bus;
    }

    public boolean scheduleExists(int departurePointIndex) {
        return !mCurrentBus.getTimes(departurePointIndex, 0).isEmpty();
    }

    public String getDepartingPoint(int departurePointIndex) {
        return mCurrentBus.getDepartingPoint(departurePointIndex);
    }

    public CharSequence[] getDepartingPointsStrings() {
        return mCurrentBus.getDepartingPointsStrings();
    }

    public String[] getDaysOfOperation(int departurePointIndex) {
        return mCurrentBus.getDaysOfOperation(departurePointIndex);
    }

    public ArrayList<Time> getTimes(int departurePointIndex, int scheduleIndex) {
        return mCurrentBus.getTimes(departurePointIndex, scheduleIndex);
    }

    public String getBusName() {
        return mCurrentBus.getBusName();
    }

    public Schedule getSchedule(int departurePointIndex, int scheduleIndex) {
        return mCurrentBus.getSchedule(departurePointIndex, scheduleIndex);
    }

    public ArrayList<String> getBusNames() {
        return mBusNames;
    }

    public void setBusNames(ArrayList<String> busNames) {
        mBusNames = busNames;
    }

    public Bus getCurrentBus() {
        return mCurrentBus;
    }
}
