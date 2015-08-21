package com.migapro.busrider.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.migapro.busrider.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("GCM");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRegisIdSent = false;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(Constants.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("Token", "token= " + token);

            sendRegisIdToAppServer(token);
            isRegisIdSent = true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sharedPref.edit().putBoolean(Constants.KEY_GCM_REGIS_ID_SENT, isRegisIdSent).apply();
    }

    private void sendRegisIdToAppServer(String token) throws IOException, JSONException {
        URL url = null;
        try {
            url = new URL(Constants.BUS_SERVER_URL + Constants.REGISTER_GCM_PATH);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String body = "regId=" + token;
        byte[] bytes = body.getBytes();
        HttpURLConnection urlConnection = null;
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

            JSONObject json = readResponse(urlConnection);
            if (!json.getString("result").equals("success")) {
                throw new IOException();
            }

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private JSONObject readResponse(HttpURLConnection urlConnection) throws IOException, JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return new JSONObject(stringBuilder.toString());
    }
}
