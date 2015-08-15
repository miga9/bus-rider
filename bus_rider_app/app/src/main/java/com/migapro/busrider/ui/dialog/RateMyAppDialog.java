package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.migapro.busrider.R;

public class RateMyAppDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.rate_this_app)
                .setMessage(R.string.rate_this_app_msg)
                .setPositiveButton(R.string.rate_this_app_positive,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + getActivity().getPackageName())));
                            }
                        })
                .setNegativeButton(R.string.rate_this_app_negative,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .create();
    }
}
