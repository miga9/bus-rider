package com.migapro.busrider.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.migapro.busrider.R;
import com.migapro.busrider.utility.Constants;

public class MsgDialog extends DialogFragment {

    private OnPositiveClickListener mListener;

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public static MsgDialog newInstance(int titleRes, int msgRes, int posRes) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_MSG_DIALOG_TITLE, titleRes);
        bundle.putInt(Constants.KEY_MSG_DIALOG_MESSAGE, msgRes);
        bundle.putInt(Constants.KEY_MSG_DIALOG_POSITIVE, posRes);

        MsgDialog dialogFragment = new MsgDialog();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int titleRes = getArguments().getInt(Constants.KEY_MSG_DIALOG_TITLE);
        int msgRes = getArguments().getInt(Constants.KEY_MSG_DIALOG_MESSAGE);
        int posRes = getArguments().getInt(Constants.KEY_MSG_DIALOG_POSITIVE);

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
