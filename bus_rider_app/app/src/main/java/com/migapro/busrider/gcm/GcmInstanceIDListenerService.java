package com.migapro.busrider.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
