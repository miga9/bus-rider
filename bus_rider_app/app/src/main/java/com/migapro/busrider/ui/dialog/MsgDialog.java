package com.migapro.busrider.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MsgDialog extends DialogFragment {

    private static final String KEY_ID = "msgDialogId";
    private static final String KEY_TITLE = "msgDialogTitle";
    private static final String KEY_MESSAGE = "msgDialogMessage";
    private static final String KEY_POSITIVE = "msgDialogPositive";
    private static final String KEY_NEGATIVE = "msgDialogNegative";
    public static final int NO_LISTENER = -1;
    public static final int NO_NEGATIVE_BUTTON = -1;

    private OnPositiveClickListener mListener;

    public interface OnPositiveClickListener {
        void onPositiveClick(int id);
    }

    public static MsgDialog newInstance(int id, int titleRes, int msgRes, int posRes, int negRes) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, id);
        bundle.putInt(KEY_TITLE, titleRes);
        bundle.putInt(KEY_MESSAGE, msgRes);
        bundle.putInt(KEY_POSITIVE, posRes);
        bundle.putInt(KEY_NEGATIVE, negRes);

        MsgDialog dialogFragment = new MsgDialog();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int id = getArguments().getInt(KEY_ID);
        int titleRes = getArguments().getInt(KEY_TITLE);
        int msgRes = getArguments().getInt(KEY_MESSAGE);
        int posRes = getArguments().getInt(KEY_POSITIVE);
        int negRes = getArguments().getInt(KEY_NEGATIVE);

        AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(getActivity())
                .setTitle(titleRes)
                .setMessage(msgRes)
                .setPositiveButton(posRes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onPositiveClick(id);
                        }
                        dialog.dismiss();
                    }
                });

        if (negRes != NO_NEGATIVE_BUTTON) {
            dialogBuilder.setNegativeButton(negRes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        return dialogBuilder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments().getInt(KEY_ID) != NO_LISTENER) {
            mListener = (OnPositiveClickListener) activity;
        }
    }
}
