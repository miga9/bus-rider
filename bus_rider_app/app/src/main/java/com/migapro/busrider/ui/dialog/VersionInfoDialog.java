package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.migapro.busrider.R;
import com.migapro.busrider.utility.Constants;

public class VersionInfoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.version_info_title) + getLastUpdatedDate())
                .setMessage(R.string.version_info_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private String getLastUpdatedDate() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPref.getString(Constants.KEY_LAST_UPDATED_DATE, "N/A");
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView)getDialog().findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}
