package com.migapro.busrider.parser;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BusXmlPullParserTest extends TestCase {

    private BusXmlPullParser parser;
    private InputStream xmlDataFile;

    public BusXmlPullParserTest(String name) {
        super(name);
        try {
            parser = new BusXmlPullParser();
        } catch (XmlPullParserException parserException) {
            parserException.printStackTrace();
        }

        xmlDataFile = getClass().getClassLoader().getResourceAsStream("assets/data/bus_data.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanPullBusNames() {
        ArrayList<String> busNames = null;
        try {
            busNames = parser.readBusNames(xmlDataFile);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (XmlPullParserException parserException) {
            parserException.printStackTrace();
        }

        assertEquals("Mean Green", busNames.get(0));
        assertEquals("North Texan", busNames.get(1));
        assertEquals("Discovery Park", busNames.get(5));
    }
}
