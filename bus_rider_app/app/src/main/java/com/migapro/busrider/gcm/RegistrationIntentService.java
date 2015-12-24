package com.migapro.busrider.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.migapro.busrider.R;
import com.migapro.busrider.network.webservice.RegisterGcmIdWebService;
import com.migapro.busrider.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
            String token = instanceID.getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
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
        JSONObject jsonResponse = RegisterGcmIdWebService.registerGcmId(token);
        if (jsonResponse == null || !jsonResponse.getString("result").equals("success")) {
            throw new IOException();
        }
    }
}
