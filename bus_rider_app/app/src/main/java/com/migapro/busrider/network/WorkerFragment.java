package com.migapro.busrider.network;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.migapro.busrider.R;

public class WorkerFragment extends Fragment implements DataAsyncTask.OnDataServiceListener {

    private WorkerListener mWorkerListener;
    private DataAsyncTask mDataAsyncTask;
    private ProgressDialog mProgressDialog;

    public interface WorkerListener {
        void onPostExecute(String result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDataAsyncTask != null && mDataAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            showProgressDialog();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        }

        mProgressDialog.setMessage(getString(R.string.progress_msg_downloading));
        mProgressDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void startDataAsyncTask() {
        mDataAsyncTask = new DataAsyncTask();
        mDataAsyncTask.setOnDataServiceListener(this);
        mDataAsyncTask.execute();
    }

    @Override
    public void onDataServiceStart() {
        showProgressDialog();
    }

    @Override
    public void onDataServiceComplete(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mWorkerListener.onPostExecute(result);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mWorkerListener = (WorkerListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWorkerListener = null;
    }
}
