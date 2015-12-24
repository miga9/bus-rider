package com.migapro.busrider.network;

import android.os.AsyncTask;

import com.migapro.busrider.network.webservice.BusDataWebService;

public class DataAsyncTask extends AsyncTask<Void, Void, String> {

    private OnDataServiceListener mListener;

    public interface OnDataServiceListener {
        void onDataServiceStart();
        void onDataServiceComplete(String result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener != null) {
            mListener.onDataServiceStart();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        return BusDataWebService.fetchBusData();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mListener != null) {
            mListener.onDataServiceComplete(result);
        }
    }

    public void setOnDataServiceListener(OnDataServiceListener listener) {
        mListener = listener;
    }
}
