package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.migapro.busrider.R;

public class RateMyAppDialog extends AlertDialog.Builder {

    private Context mContext;

    public RateMyAppDialog(Context context) {
        super(context);
        mContext = context;

        setTitle(mContext.getString(R.string.rate_this_app));
        setMessage(mContext.getString(R.string.rate_this_app_msg));
        setPositiveButton(mContext.getString(R.string.rate_this_app_positive),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + mContext.getPackageName())));
                    }
                });
        setNegativeButton(mContext.getString(R.string.rate_this_app_negative),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    public void showDialog() {
        create().show();
    }
}
