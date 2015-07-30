package com.migapro.busrider.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataAsyncTask extends AsyncTask<Void, Void, Void> {

    private OnDataServiceListener mListener;

    public interface OnDataServiceListener {
        void onDataServiceStart();
        void onDataServiceComplete();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener != null) {
            mListener.onDataServiceStart();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL("http://www.migapro.com/busrider/bus_data.xml");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("data", line);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mListener != null) {
            mListener.onDataServiceComplete();
        }
    }

    public void setOnDataServiceListener(OnDataServiceListener listener) {
        mListener = listener;
    }
}
