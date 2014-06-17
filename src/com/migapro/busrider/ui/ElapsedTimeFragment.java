package com.migapro.busrider.ui;

import com.migapro.busrider.R;
import com.migapro.busrider.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ElapsedTimeFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_elapsed_time, container, false);
		
		return rootView;
	}

}
