package com.migapro.busrider.parser;

import com.google.android.gms.maps.model.LatLng;
import com.migapro.busrider.config.FeatureFlags;
import com.migapro.busrider.models.Bus;
import com.migapro.busrider.models.DepartingPoint;
import com.migapro.busrider.models.Schedule;
import com.migapro.busrider.models.Time;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class BusXmlPullParser {

    private static final String TAG_BUS = "bus";
    private static final String ATTR_ID = "id";
    private static final String ATTR_NAME = "name";
    private static final String TAG_DEPART_POINT = "depart_point";
    private static final String ATTR_FROM = "from";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String ATTR_DAYS = "days";
    private static final String TAG_TIME = "time";
    private static final String ATTR_HOURS = "hours";
    private static final String TAG_MINUTES = "minutes";
    private static final String ATTR_MIN = "min";
    private static final String TAG_BUS_STOP = "bus_stop";
    private static final String ATTR_LAT = "lat";
    private static final String ATTR_LNG = "lng";
	
	private XmlPullParserFactory factory;
	private String mTargetBus;
	private boolean mIsBusFound;
	
	private Bus mBus;
	private DepartingPoint mDepartingPoint;
	private Schedule mSchedule;
    private Time mTime;
	
	public BusXmlPullParser() throws XmlPullParserException {
		factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}
	
	public ArrayList<String> readBusNames(InputStream inputStream) throws XmlPullParserException, IOException {
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, "utf-8");
		
		return parseForBusNames(parser);
	}
	
	private ArrayList<String> parseForBusNames(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<String> busNames = new ArrayList<String>();
		
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG && parser.getName().equals(TAG_BUS))
				busNames.add(parser.getAttributeValue(null, ATTR_NAME));
			eventType = parser.next();
		}
		
		return busNames;
	}
	
	public Bus readABusData(InputStream inputStream, int targetBus) throws XmlPullParserException, IOException {
		mTargetBus = String.valueOf(targetBus);
		
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, "utf-8");
		
		return parseForABusData(parser);
	}
	
	private Bus parseForABusData(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG)
				processStartTag(parser.getPrefix(), parser.getName(), parser);
			else if (isBusFound(eventType, parser.getName()))
				break;
			
			eventType = parser.next();
		}
		
		return mBus;
	}
	
	private void processStartTag(String prefix, String name, XmlPullParser parser) 
			throws XmlPullParserException, IOException {
		if (name.equals(TAG_BUS)) {
			if (parser.getAttributeValue(null, ATTR_ID).equals(mTargetBus)) {
				mBus = new Bus();
				mBus.setBusname(parser.getAttributeValue(null, ATTR_NAME));
				
				mIsBusFound = true;
			} else
				skipToNextBusElement(parser);
			
		} else if (name.equals(TAG_DEPART_POINT)) {
			mDepartingPoint = new DepartingPoint();
			mDepartingPoint.setDepartFrom(parser.getAttributeValue(null, ATTR_FROM));
			mBus.addDepartingPoint(mDepartingPoint);
			
		} else if (name.equals(TAG_SCHEDULE)) {
			mSchedule = new Schedule();
			mSchedule.setDaysOfOperation(parser.getAttributeValue(null, ATTR_DAYS));
			mDepartingPoint.addSchedule(mSchedule);
			
		} else if (name.equals(TAG_TIME)) {
            mTime = new Time();
            mTime.setHours(parser.getAttributeValue(null, ATTR_HOURS));
            mSchedule.addTime(mTime);

        } else if (name.equals(TAG_MINUTES)) {
            mTime.addMinutes(parser.getAttributeValue(null, ATTR_MIN));
			
		} else if(FeatureFlags.MAPS) {
            if (name.equals(TAG_BUS_STOP)) {
                mBus.addBusStopTitle(parser.getAttributeValue(null, ATTR_NAME));
                LatLng latLng = new LatLng(
                        Double.parseDouble(parser.getAttributeValue(null, ATTR_LAT)),
                        Double.parseDouble(parser.getAttributeValue(null, ATTR_LNG)));
                mBus.addBusStopLatLng(latLng);
            }
		}
	}
	
	private void skipToNextBusElement(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType;
		boolean isBusEndTag = false;
		do {
			eventType = parser.next();
			if (eventType == XmlPullParser.END_TAG)
				isBusEndTag = parser.getName().equals(TAG_BUS);
		} while (!isBusEndTag);
	}
	
	private boolean isBusFound(int eventType, String name) {
		return (eventType == XmlPullParser.END_TAG && name.equals(TAG_BUS) && mIsBusFound);
	}
	
}
