package com.migapro.busrider.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static boolean doesFileExist(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public static void saveLastUpdatedDate(Context context) {
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPrefEditor.putBoolean(Constants.KEY_NEED_TO_UPDATE_FILE, false);
        String lastUpdatedDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        sharedPrefEditor.putString(Constants.KEY_LAST_UPDATED_DATE, lastUpdatedDate);
        sharedPrefEditor.apply();
    }
}
