package com.migapro.busrider.network.webservice;

import com.migapro.busrider.utility.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BusDataWebService {

    public static String fetchBusData() {
        StringBuilder xmlContentBuilder = new StringBuilder();

        try {
            URL url = new URL(Constants.BUS_SERVER_URL + Constants.BUS_DATA_PATH);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    xmlContentBuilder.append(line);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return xmlContentBuilder.toString();
    }
}
