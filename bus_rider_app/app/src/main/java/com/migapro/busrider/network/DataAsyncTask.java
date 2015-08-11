package com.migapro.busrider.network;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.migapro.busrider.utility.Constants;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataAsyncTask extends AsyncTask<Void, Void, Void> {

    private OnDataServiceListener mListener;
    private boolean mIsFileDownloaded;

    public interface OnDataServiceListener {
        void onDataServiceStart();
        void onDataServiceComplete(boolean isSuccess);
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
            URL url = new URL(Constants.BUS_DATA_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                saveDownloadedFile(urlConnection);
                mIsFileDownloaded = true;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveDownloadedFile(HttpURLConnection urlConnection) throws IOException {
        FileOutputStream fileOutputStream =
                ((Activity) mListener).openFileOutput(Constants.BUS_DATA_PATH, Context.MODE_PRIVATE);

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder xmlContentBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            xmlContentBuilder.append(line);
        }

        String xmlContent = xmlContentBuilder.toString();
        if (xmlContent != null && !xmlContent.isEmpty()) {
            fileOutputStream.write(xmlContent.getBytes());
        }

        fileOutputStream.close();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mListener != null) {
            mListener.onDataServiceComplete(mIsFileDownloaded);
        }
    }

    public void setOnDataServiceListener(OnDataServiceListener listener) {
        mListener = listener;
    }
}
