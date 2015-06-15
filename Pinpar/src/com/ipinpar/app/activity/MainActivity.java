package com.ipinpar.app.activity;

import org.json.JSONObject;

import android.os.Bundle;

import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.ipinpar.app.PPBaseActivity;
import com.ipinpar.app.R;
import com.ipinpar.app.entity.UserEntity;
import com.ipinpar.app.network.api.LoginRequest;

public class MainActivity extends PPBaseActivity {
	private LoginRequest request ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		request = new LoginRequest("15001225725", "chzf7229", new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				UserEntity user = gson.fromJson(response.toString(), UserEntity.class);
				if (user != null && user.getResult() == 1) {
					//登录成功
				}
				else {
					//登录失败
				}
			}
		});
		request.setTag(TAG);
		apiQueue.add(request);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

}
