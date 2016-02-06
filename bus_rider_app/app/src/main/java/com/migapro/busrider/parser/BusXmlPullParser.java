package com.migapro.busrider.parser;

import com.migapro.busrider.models.Bus;
import com.migapro.busrider.models.BusMap;
import com.migapro.busrider.parser.reader.BusMapReader;
import com.migapro.busrider.parser.reader.BusNamesReader;
import com.migapro.busrider.parser.reader.BusReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class BusXmlPullParser {

	private static final String CHAR_SET = "utf-8";
	
	private XmlPullParserFactory factory;
	
	public BusXmlPullParser() throws XmlPullParserException {
		factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}
	
	public ArrayList<String> readBusNames(InputStream inputStream) throws XmlPullParserException, IOException {
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, CHAR_SET);

		BusNamesReader busNamesReader = new BusNamesReader(parser);
		return busNamesReader.parseForBusNames();
	}
	
	public Bus readABusData(InputStream inputStream, int targetBus) throws XmlPullParserException, IOException {
		String targetBusId = String.valueOf(targetBus);

		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, CHAR_SET);

		BusReader busReader = new BusReader(parser, targetBusId);
		return busReader.parseForABusData();
	}

	public BusMap readBusMapData(InputStream inputStream, int targeBus, String targetBusMapId)
			throws XmlPullParserException, IOException {
		String targetBusId = String.valueOf(targeBus);

		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, CHAR_SET);

		BusMapReader busMapReader = new BusMapReader(parser, targetBusId, targetBusMapId);
		return busMapReader.parseForBusMapData();
	}
}