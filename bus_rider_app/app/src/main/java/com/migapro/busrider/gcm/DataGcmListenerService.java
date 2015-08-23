package com.migapro.busrider.gcm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GcmListenerService;
import com.migapro.busrider.utility.Constants;

public class DataGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        String message = data.getString(Constants.KEY_GCM_MESSAGE);
        if (Constants.KEY_GCM_MSG_UPDATE_SCHEDULE_FILE.equals(message)) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit().putBoolean(Constants.KEY_GCM_MSG_UPDATE_SCHEDULE_FILE, true).apply();
        }
    }
}
