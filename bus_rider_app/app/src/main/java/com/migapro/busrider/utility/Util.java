package com.migapro.busrider.utility;

import android.content.Context;
import android.content.pm.PackageManager;

public class Util {

    public static String getAppVersionNumber(Context context) {
        String appVersionNumber = "";
        try {
            appVersionNumber =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException nameNotFoundException) {
            nameNotFoundException.printStackTrace();
        }
        return appVersionNumber;
    }
}
