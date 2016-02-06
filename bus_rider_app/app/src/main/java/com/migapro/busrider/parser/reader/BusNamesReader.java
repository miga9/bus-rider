package com.migapro.busrider.parser.reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class BusNamesReader {

    private XmlPullParser mParser;

    public BusNamesReader(XmlPullParser parser) {
        mParser = parser;
    }

    public ArrayList<String> parseForBusNames() throws XmlPullParserException, IOException {
        ArrayList<String> busNames = new ArrayList<String>();

        int eventType = mParser.next();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && mParser.getName().equals(BusXmlConstants.TAG_BUS))
                busNames.add(mParser.getAttributeValue(null, BusXmlConstants.ATTR_NAME));
            eventType = mParser.next();
        }

        return busNames;
    }
}