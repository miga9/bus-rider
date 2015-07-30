package com.migapro.busrider.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("GCM");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("senderId", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("Token", "token= " + token);

            sharedPref.edit().putBoolean("regisIdSent", true).apply();
        } catch (IOException e) {
            e.printStackTrace();
            sharedPref.edit().putBoolean("regisIdSent", false).apply();
        }
    }
}
