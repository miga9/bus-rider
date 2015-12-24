package com.migapro.busrider.network.webservice;

import com.migapro.busrider.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterGcmIdWebService {

    public static JSONObject registerGcmId(String token) throws IOException, JSONException {
        URL url = new URL(Constants.BUS_SERVER_URL + Constants.REGISTER_GCM_PATH);

        String body = "regId=" + token;
        byte[] bytes = body.getBytes();
        HttpURLConnection urlConnection = null;
        JSONObject jsonResponse = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setFixedLengthStreamingMode(bytes.length);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            OutputStream out = urlConnection.getOutputStream();
            out.write(bytes);
            out.close();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException();
            }

            jsonResponse = readResponse(urlConnection);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    private static JSONObject readResponse(HttpURLConnection urlConnection) throws IOException, JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return new JSONObject(stringBuilder.toString());
    }
}
