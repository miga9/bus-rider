package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.migapro.busrider.R;
import com.migapro.busrider.utility.Constants;
import com.migapro.busrider.utility.Util;

public class VersionInfoDialog extends AlertDialog.Builder {

    public VersionInfoDialog(Context context) {
        super(context);

        setTitle(context.getString(R.string.version_info_title) + Util.getAppVersionNumber(context));
        setMessage(context.getString(R.string.version_info_message) + getLastUpdatedDate(context));
        setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private String getLastUpdatedDate(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(Constants.KEY_LAST_UPDATED_DATE, "N/A");
    }

    public void showDialog() {
        AlertDialog versionInfoDialog = create();
        versionInfoDialog.show();

        ((TextView)versionInfoDialog.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}
