package com.migapro.busrider.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SingleChoiceDialog extends DialogFragment {

    public static final String KEY_ID = "singleChoiceId";
    public static final String KEY_TITLE = "singleChoiceTitle";
    public static final String KEY_CHOICES = "singleChoiceChoices";
    public static final String KEY_DEFAULT_SELECTION = "singleChoiceDefault";

    private OnDialogItemSelectedListener mListener;

    public interface OnDialogItemSelectedListener {
        void onItemSelected(int id, int which);
    }

    public static SingleChoiceDialog newInstance(int id, int titleRes, CharSequence[] choices, int defaultSelection) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, id);
        bundle.putInt(KEY_TITLE, titleRes);
        bundle.putCharSequenceArray(KEY_CHOICES, choices);
        bundle.putInt(KEY_DEFAULT_SELECTION, defaultSelection);

        SingleChoiceDialog dialogFragment = new SingleChoiceDialog();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int id = getArguments().getInt(KEY_ID);
        int titleRes = getArguments().getInt(KEY_TITLE);
        CharSequence[] choices = getArguments().getCharSequenceArray(KEY_CHOICES);
        int defaultSelection = getArguments().getInt(KEY_DEFAULT_SELECTION);

        return new AlertDialog.Builder(getActivity())
                .setTitle(titleRes)
                .setSingleChoiceItems(
                        choices,
                        defaultSelection, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onItemSelected(id, which);
                                }
                                dialog.dismiss();
                            }
                        })
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnDialogItemSelectedListener) activity;
    }
}
