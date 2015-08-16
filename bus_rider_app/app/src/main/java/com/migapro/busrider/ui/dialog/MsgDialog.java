package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.migapro.busrider.R;

public class MsgDialog extends DialogFragment {

    private static final String KEY_TITLE = "msgDialogTitle";
    private static final String KEY_MESSAGE = "msgDialogMessage";
    private static final String KEY_POSITIVE = "msgDialogPositive";

    private OnPositiveClickListener mListener;

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public static MsgDialog newInstance(int titleRes, int msgRes, int posRes) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE, titleRes);
        bundle.putInt(KEY_MESSAGE, msgRes);
        bundle.putInt(KEY_POSITIVE, posRes);

        MsgDialog dialogFragment = new MsgDialog();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int titleRes = getArguments().getInt(KEY_TITLE);
        int msgRes = getArguments().getInt(KEY_MESSAGE);
        int posRes = getArguments().getInt(KEY_POSITIVE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(titleRes)
                .setMessage(msgRes)
                .setPositiveButton(posRes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onPositiveClick();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
    }

    public void setOnPositiveClickListener(OnPositiveClickListener listener) {
        mListener = listener;
    }
}
