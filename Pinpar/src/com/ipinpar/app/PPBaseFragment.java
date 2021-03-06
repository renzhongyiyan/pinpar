package com.ipinpar.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class PPBaseFragment extends Fragment {
	public final String TAG = this.getClass().getSimpleName();
	public RequestQueue apiQueue;
	public Context mContext;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = (Context)activity;
	}

	@Override
	public void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		apiQueue = Volley.newRequestQueue(mContext);
		apiQueue.start();
	}
	
	public void showProgressDialog(){
		((PPBaseActivity)mContext).showProgressDialog();
	}
	public void dissmissProgressDialog(){
		((PPBaseActivity)mContext).dissmissProgressDialog();

	} 
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		apiQueue.cancelAll(TAG);
		apiQueue.stop();
	}
}
