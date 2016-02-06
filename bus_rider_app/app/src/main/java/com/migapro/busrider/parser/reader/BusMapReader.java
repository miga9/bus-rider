package com.migapro.busrider.parser.reader;

import com.migapro.busrider.models.BusMap;
import com.migapro.busrider.models.BusStop;
import com.migapro.busrider.models.LatLngData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class BusMapReader {

    private XmlPullParser mParser;

    private String mTargetBus, mTargetBusMap;
    private boolean mIsBusMapFound;
    private BusMap mBusMap;

    public BusMapReader(XmlPullParser parser, String targetBus, String targetBusMap) {
        mParser = parser;
        mTargetBus = targetBus;
        mTargetBusMap = targetBusMap;
    }

    public BusMap parseForBusMapData() throws XmlPullParserException, IOException {
        int eventType = mParser.next();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG)
                processStartTagForBusMap(mParser.getName());
            else if (isBusMapFound(eventType, mParser.getName()))
                break;

            eventType = mParser.next();
        }

        return mBusMap;
    }

    private void processStartTagForBusMap(String name)
            throws XmlPullParserException, IOException {
        if (name.equals(BusXmlConstants.TAG_BUS)) {
            if (!mParser.getAttributeValue(null, BusXmlConstants.ATTR_ID).equals(mTargetBus)) {
                skipToNextBusElement();
            }
        }
        else if (name.equals(BusXmlConstants.TAG_BUS_MAP)) {
            if (mParser.getAttributeValue(null, BusXmlConstants.ATTR_ID).equals(mTargetBusMap)) {
                mBusMap = new BusMap();
                mIsBusMapFound = true;
            }
            else {
                skipToNextBusMapElement();
            }
        }
        else if (name.equals(BusXmlConstants.TAG_BUS_STOP)) {
            BusStop busStop = new BusStop();
            busStop.setTitle(mParser.getAttributeValue(null, BusXmlConstants.ATTR_NAME));
            busStop.setLatitude(Double.parseDouble(mParser.getAttributeValue(null, BusXmlConstants.ATTR_LAT)));
            busStop.setLongitude(Double.parseDouble(mParser.getAttributeValue(null, BusXmlConstants.ATTR_LNG)));
            mBusMap.addBusStop(busStop);
        }
        else if (name.equals(BusXmlConstants.TAG_BUS_WAYPOINT)) {
            LatLngData latLng = new LatLngData();
            latLng.setLatitude(Double.parseDouble(mParser.getAttributeValue(null, BusXmlConstants.ATTR_LAT)));
            latLng.setLongitude(Double.parseDouble(mParser.getAttributeValue(null, BusXmlConstants.ATTR_LNG)));
            mBusMap.addWaypoint(latLng);
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

    private void skipToNextBusMapElement() throws XmlPullParserException, IOException {
        int eventType;
        boolean isBusMapEndTag = false;
        do {
            eventType = mParser.next();
            if (eventType == XmlPullParser.END_TAG)
                isBusMapEndTag = mParser.getName().equals(BusXmlConstants.TAG_BUS_MAP);
        } while (!isBusMapEndTag);
    }

    private boolean isBusMapFound(int eventType, String name) {
        return (eventType == XmlPullParser.END_TAG && name.equals(BusXmlConstants.TAG_BUS_MAP) && mIsBusMapFound);
    }
}