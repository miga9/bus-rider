package com.migapro.busrider.parser.reader;

import com.migapro.busrider.models.Bus;
import com.migapro.busrider.models.DepartingPoint;
import com.migapro.busrider.models.Schedule;
import com.migapro.busrider.models.Time;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class BusReader {

    private XmlPullParser mParser;
    private String mTargetBus;
    private boolean mIsBusFound;

    private Bus mBus;
    private DepartingPoint mDepartingPoint;
    private Schedule mSchedule;
    private Time mTime;

    public BusReader(XmlPullParser parser, String targetBus) {
        mParser = parser;
        mTargetBus = targetBus;
    }

    public Bus parseForABusData() throws XmlPullParserException, IOException {
        int eventType = mParser.next();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG)
                processStartTag(mParser.getName());
            else if (isBusFound(eventType, mParser.getName()))
                break;

            eventType = mParser.next();
        }

        return mBus;
    }

    private void processStartTag(String name)
            throws XmlPullParserException, IOException {
        if (name.equals(BusXmlConstants.TAG_BUS)) {
            if (mParser.getAttributeValue(null, BusXmlConstants.ATTR_ID).equals(mTargetBus)) {
                mBus = new Bus();
                mBus.setBusName(mParser.getAttributeValue(null, BusXmlConstants.ATTR_NAME));

                mIsBusFound = true;
            }
            else
                skipToNextBusElement();
        }
        else if (name.equals(BusXmlConstants.TAG_DEPART_POINT)) {
            mDepartingPoint = new DepartingPoint();
            mDepartingPoint.setDepartFrom(mParser.getAttributeValue(null, BusXmlConstants.ATTR_FROM));
            mBus.addDepartingPoint(mDepartingPoint);
        }
        else if (name.equals(BusXmlConstants.TAG_SCHEDULE)) {
            mSchedule = new Schedule();
            mSchedule.setDaysOfOperation(mParser.getAttributeValue(null, BusXmlConstants.ATTR_DAYS));
            mSchedule.setBusMapId(mParser.getAttributeValue(null, BusXmlConstants.ATTR_SCHEDULE_MAP_ID));
            mDepartingPoint.addSchedule(mSchedule);
        }
        else if (name.equals(BusXmlConstants.TAG_TIME)) {
            mTime = new Time();
            mTime.setHours(mParser.getAttributeValue(null, BusXmlConstants.ATTR_HOURS));
            mSchedule.addTime(mTime);
        }
        else if (name.equals(BusXmlConstants.TAG_MINUTES)) {
            mTime.addMinutes(mParser.getAttributeValue(null, BusXmlConstants.ATTR_MIN));
        }
    }

    private void skipToNextBusElement() throws XmlPullParserException, IOException {
        int eventType;
        boolean isBusEndTag = false;
        do {
            eventType = mParser.next();
            if (eventType == XmlPullParser.END_TAG)
                isBusEndTag = mParser.getName().equals(BusXmlConstants.TAG_BUS);
        } while (!isBusEndTag);
    }

    private boolean isBusFound(int eventType, String name) {
        return (eventType == XmlPullParser.END_TAG && name.equals(BusXmlConstants.TAG_BUS) && mIsBusFound);
    }
}