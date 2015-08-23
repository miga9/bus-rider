package com.migapro.busrider.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static boolean shouldPromptRateMyApp(Activity activity) {
        SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
        int count = sp.getInt(Constants.RATE_MY_APP_COUNT_KEY, 0);

        if (count < Constants.NUM_OF_VISITS_TO_PROMPT_RATE_THIS_APP) {
            count++;
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Constants.RATE_MY_APP_COUNT_KEY, count);
            editor.commit();

            if (count == Constants.NUM_OF_VISITS_TO_PROMPT_RATE_THIS_APP) {
                return true;
            }
        }
        return false;
    }

    public static void saveLastUpdatedDate(Context context) {
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPrefEditor.putBoolean(Constants.KEY_GCM_MSG_UPDATE_SCHEDULE_FILE, false);
        String lastUpdatedDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        sharedPrefEditor.putString(Constants.KEY_LAST_UPDATED_DATE, lastUpdatedDate);
        sharedPrefEditor.apply();
    }

    public static void saveBusData(Context context, String result) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(Constants.BUS_DATA_PATH, Context.MODE_PRIVATE);
            fileOutputStream.write(result.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
