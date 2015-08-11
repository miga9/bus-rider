package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.migapro.busrider.R;

public class MsgDialog extends AlertDialog.Builder {

    private OnPositiveClickListener mListener;

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public MsgDialog(Context context, int titleRes, int msgRes, int posRes) {
        super(context);

        setTitle(titleRes);
        setMessage(msgRes);
        setPositiveButton(posRes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onPositiveClick();
                        }
                        dialog.dismiss();
                    }
                });
        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    public void showDialog() {
        create().show();
    }

    public void setOnPositiveClickListener(OnPositiveClickListener listener) {
        mListener = listener;
    }
}
