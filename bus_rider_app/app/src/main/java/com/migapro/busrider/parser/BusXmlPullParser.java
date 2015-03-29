package com.migapro.busrider.parser;

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
			if (eventType == XmlPullParser.START_TAG && parser.getName().equals("bus"))
				busNames.add(parser.getAttributeValue(null, "name"));
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
		if (name.equals("bus")) {
			if (parser.getAttributeValue(null, "id").equals(mTargetBus)) {
				mBus = new Bus();
				mBus.setBusname(parser.getAttributeValue(null, "name"));
				
				mIsBusFound = true;
			} else
				skipToNextBusElement(parser);
			
		} else if (name.equals("depart_point")) {
			mDepartingPoint = new DepartingPoint();
			mDepartingPoint.setDepartFrom(parser.getAttributeValue(null, "from"));
			mBus.addDepartingPoint(mDepartingPoint);
			
		} else if (name.equals("schedule")) {
			mSchedule = new Schedule();
			mSchedule.setDaysOfOperation(parser.getAttributeValue(null, "days"));
			mDepartingPoint.addSchedule(mSchedule);
			
		} else if (name.equals("time")) {
            mTime = new Time();
            mTime.setHours(parser.getAttributeValue(null, "hours"));
            mSchedule.addTime(mTime);

        } else if (name.equals("minutes")) {
            mTime.addMinutes(parser.getAttributeValue(null, "min"));
			
		}
	}
	
	private void skipToNextBusElement(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType;
		boolean isBusEndTag = false;
		do {
			eventType = parser.next();
			if (eventType == XmlPullParser.END_TAG)
				isBusEndTag = parser.getName().equals("bus");
		} while (!isBusEndTag);
	}
	
	private boolean isBusFound(int eventType, String name) {
		return (eventType == XmlPullParser.END_TAG && name.equals("bus") && mIsBusFound);
	}
	
}
