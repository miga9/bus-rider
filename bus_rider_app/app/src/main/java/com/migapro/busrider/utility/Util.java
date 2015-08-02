package com.migapro.busrider.utility;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

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
}
