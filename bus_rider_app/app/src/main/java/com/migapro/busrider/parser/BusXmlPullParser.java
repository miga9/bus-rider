package com.migapro.busrider.parser;

import com.migapro.busrider.models.Bus;
import com.migapro.busrider.models.BusStop;
import com.migapro.busrider.models.DepartingPoint;
import com.migapro.busrider.models.LatLngData;
import com.migapro.busrider.models.BusMap;
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
	private static final String ATTR_SCHEDULE_MAP_ID = "map_id";
    private static final String TAG_TIME = "time";
    private static final String ATTR_HOURS = "hours";
    private static final String TAG_MINUTES = "minutes";
    private static final String ATTR_MIN = "min";
	private static final String TAG_BUS_MAP = "bus_map";
    private static final String TAG_BUS_STOP = "bus_stop";
	private static final String TAG_BUS_WAYPOINT = "bus_waypoint";
    private static final String ATTR_LAT = "lat";
    private static final String ATTR_LNG = "lng";

	private static final String CHAR_SET = "utf-8";
	
	private XmlPullParserFactory factory;
	private String mTargetBus, mTargetBusMap;
	private boolean mIsBusFound, mIsBusMapFound;
	
	private Bus mBus;
	private DepartingPoint mDepartingPoint;
	private Schedule mSchedule;
    private Time mTime;

	private BusMap mBusMap;
	
	public BusXmlPullParser() throws XmlPullParserException {
		factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}
	
	public ArrayList<String> readBusNames(InputStream inputStream) throws XmlPullParserException, IOException {
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, CHAR_SET);
		
		return parseForBusNames(parser);
	}
	
	private ArrayList<String> parseForBusNames(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<String> busNames = new ArrayList<>();
		
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
		parser.setInput(inputStream, CHAR_SET);

		return parseForABusData(parser);
	}

	private Bus parseForABusData(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG)
				processStartTagForBus(parser.getName(), parser);
			else if (isBusFound(eventType, parser.getName()))
				break;

			eventType = parser.next();
		}

		return mBus;
	}

	private void processStartTagForBus(String name, XmlPullParser parser)
			throws XmlPullParserException, IOException {
		if (name.equals(TAG_BUS)) {
			if (parser.getAttributeValue(null, ATTR_ID).equals(mTargetBus)) {
				mBus = new Bus();
				mBus.setBusName(parser.getAttributeValue(null, ATTR_NAME));

				mIsBusFound = true;
			}
			else
				skipToNextBusElement(parser);
		}
		else if (name.equals(TAG_DEPART_POINT)) {
			mDepartingPoint = new DepartingPoint();
			mDepartingPoint.setDepartFrom(parser.getAttributeValue(null, ATTR_FROM));
			mBus.addDepartingPoint(mDepartingPoint);
		}
		else if (name.equals(TAG_SCHEDULE)) {
			mSchedule = new Schedule();
			mSchedule.setDaysOfOperation(parser.getAttributeValue(null, ATTR_DAYS));
			mSchedule.setBusMapId(parser.getAttributeValue(null, ATTR_SCHEDULE_MAP_ID));
			mDepartingPoint.addSchedule(mSchedule);
		}
		else if (name.equals(TAG_TIME)) {
            mTime = new Time();
            mTime.setHours(parser.getAttributeValue(null, ATTR_HOURS));
            mSchedule.addTime(mTime);
        }
		else if (name.equals(TAG_MINUTES)) {
            mTime.addMinutes(parser.getAttributeValue(null, ATTR_MIN));
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

	public BusMap readBusMapData(InputStream inputStream, int targeBus, String targetBusMap) throws XmlPullParserException, IOException {
		mTargetBus = String.valueOf(targeBus);
		mTargetBusMap = targetBusMap;

		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, CHAR_SET);

		return parseForBusMapData(parser);
	}

	private BusMap parseForBusMapData(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG)
				processStartTagForBusMap(parser.getName(), parser);
			else if (isBusMapFound(eventType, parser.getName()))
				break;

			eventType = parser.next();
		}

		return mBusMap;
	}

	private void processStartTagForBusMap(String name, XmlPullParser parser)
			throws XmlPullParserException, IOException {
		if (name.equals(TAG_BUS)) {
			if (!parser.getAttributeValue(null, ATTR_ID).equals(mTargetBus)) {
				skipToNextBusElement(parser);
			}
		}
		else if (name.equals(TAG_BUS_MAP)) {
			if (parser.getAttributeValue(null, ATTR_ID).equals(mTargetBusMap)) {
				mBusMap = new BusMap();
				mIsBusMapFound = true;
			}
			else {
				skipToNextBusMapElement(parser);
			}
		}
		else if (name.equals(TAG_BUS_STOP)) {
			BusStop busStop = new BusStop();
			busStop.setTitle(parser.getAttributeValue(null, ATTR_NAME));
			busStop.setLatitude(Double.parseDouble(parser.getAttributeValue(null, ATTR_LAT)));
			busStop.setLongitude(Double.parseDouble(parser.getAttributeValue(null, ATTR_LNG)));
			mBusMap.addBusStop(busStop);
		}
		else if (name.equals(TAG_BUS_WAYPOINT)) {
			LatLngData latLng = new LatLngData();
			latLng.setLatitude(Double.parseDouble(parser.getAttributeValue(null, ATTR_LAT)));
			latLng.setLongitude(Double.parseDouble(parser.getAttributeValue(null, ATTR_LNG)));
			mBusMap.addWaypoint(latLng);
		}
	}

	private void skipToNextBusMapElement(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType;
		boolean isBusMapEndTag = false;
		do {
			eventType = parser.next();
			if (eventType == XmlPullParser.END_TAG)
				isBusMapEndTag = parser.getName().equals(TAG_BUS_MAP);
		} while (!isBusMapEndTag);
	}

	private boolean isBusMapFound(int eventType, String name) {
		return (eventType == XmlPullParser.END_TAG && name.equals(TAG_BUS_MAP) && mIsBusMapFound);
	}
}