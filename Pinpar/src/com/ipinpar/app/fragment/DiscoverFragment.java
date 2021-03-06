package com.ipinpar.app.fragment;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.ipinpar.app.PPBaseFragment;
import com.ipinpar.app.R;
import com.ipinpar.app.activity.LoginActivity;
import com.ipinpar.app.activity.MainActivity;
import com.ipinpar.app.activity.OngoingAcDetail;
import com.ipinpar.app.activity.PartyHomeVenueActivity;
import com.ipinpar.app.activity.PartyLaunchActivity;
import com.ipinpar.app.adapter.OngoingActivityListAdapter;
import com.ipinpar.app.entity.ActivityEntity;
import com.ipinpar.app.entity.ActivityListEntity;
import com.ipinpar.app.entity.RoleIsSelectedEntity;
import com.ipinpar.app.manager.UserManager;
import com.ipinpar.app.network.api.ActivityListRequest;
import com.ipinpar.app.network.api.PartyGetIdentityIsSelectedRequest;
import com.ipinpar.app.util.NetWorkState;
import com.ipinpar.app.widget.PullToRefreshListView;
import com.ipinpar.app.widget.PullToRefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class DiscoverFragment extends PPBaseFragment{

	
	private Context mContext;
	private View backView;
	private View view;
	
	private LinearLayout llPastActivities;
	
	//请求进行中的活动
	private ActivityListRequest ongoingAcsRequest;
	//判断8.8的活动是否已选择角色
	private PartyGetIdentityIsSelectedRequest getIdentityIsSelectedRequest;
	private String roleCount = "-1";
	
	//保存服务器返回的正在进行中的活动
	private ArrayList<ActivityEntity> activityList = new ArrayList<ActivityEntity>();
	
	private PullToRefreshListView ongoingActicitiesListView;
	private OngoingActivityListAdapter activityListAdapter;
	
	//分页相关
	private static String ONGOING_ACLIST_STATUS = "1";
	
	private static String PAGENUM = "1";
	private static String OFFSET = "20";
	private String maxAcId;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		if(mContext == null){
			mContext = (Context)activity;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.discover_fragment, null);
		
		initView(view);
		setView();
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//请求进行中的活动
		handlerOngoingAcsRequest.sendEmptyMessage(0);
		
		if (UserManager.getInstance().isLogin()) {
			handlerRoleIsSelectedRequest.sendEmptyMessage(0);
		}
		
	}
	
	public void initView(View view) {
		backView = view.findViewById(R.id.backlayout);
		backView.setBackgroundColor(Color.WHITE);
		
		activityListAdapter = new OngoingActivityListAdapter(mContext,activityList,apiQueue);
		
		llPastActivities= (LinearLayout) view.findViewById(R.id.LL_title_past);
		
		ongoingActicitiesListView = (PullToRefreshListView) view.findViewById(R.id.ongoing_activities_list);
		if(activityListAdapter!=null){
			ongoingActicitiesListView.setAdapter(activityListAdapter);
		}
	}
	
	public void setView(){
		ongoingActicitiesListView.setOnScrollListener(onScrollListener);
		ongoingActicitiesListView.setOnRefreshListener(onRefreshListener);
		ongoingActicitiesListView.setOnItemClickListener(onItemClickListener);
		
		llPastActivities.setOnClickListener(onPastActivityClickListener);
		
		
	}


	private OnClickListener onPastActivityClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			MainActivity activity = (MainActivity) getActivity();
			FragmentManager fm = activity.getSupportFragmentManager();
			activity.container.setCurrentItem(4, false);
		}
	};
	
	private OnScrollListener onScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
				// 判断滚动到底部
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
					
					handlerOngoingAcsRequest.sendEmptyMessage(1);
					
				}
				break;
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("activityID", activityList.get(position-1).getAcid());
			
			if(activityList.get(position-1).getFlag() == 5){
//			if(position == 1){
				if (UserManager.getInstance().isLogin()) {
					handlerRoleIsSelectedRequest.sendEmptyMessage(0);
					
					if(roleCount.equals("0")){
						intent.setClass(mContext, PartyLaunchActivity.class);
						startActivity(intent);
					}else if(roleCount.equals("1")){
						intent.setClass(mContext, PartyHomeVenueActivity.class);
						startActivity(intent);
					}else{
						Toast.makeText(mContext, "获取角色信息失败！", 1000).show();
					}
				}
				else {
					intent.setClass(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}else{
				intent.setClass(mContext, OngoingAcDetail.class);
				startActivity(intent);
			}
			
		}
	};
	
	private OnRefreshListener onRefreshListener = new OnRefreshListener() {
		public void onRefresh() {
			// Do work to refresh the list here.
			
			if(NetWorkState.isConnectingToInternet()){
				handlerOngoingAcsRequest.sendEmptyMessage(0);
			}
			
		}
	};
	
	Handler handlerOngoingAcsRequest = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				ongoingAcsRequest = new ActivityListRequest(
						ONGOING_ACLIST_STATUS,
						PAGENUM,
						OFFSET, new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						Gson gson = new Gson();
						
						ActivityListEntity acList = gson.fromJson(response.toString(), ActivityListEntity.class);
						
						if(activityList.size()>0){
							maxAcId = activityList.get(activityList.size()-1).getAcid()+"";
						}else{
							maxAcId = "0";
						}
						
						Message msg = new Message();
						msg.obj = acList.getActives();
						msg.what = 2;
						handlerStateChanged.sendMessage(msg);
						
						handlerStateChanged.sendEmptyMessage(0);
						handlerStateChanged.sendEmptyMessage(1);
					}
					
				});
				ongoingAcsRequest.setTag(TAG);
				apiQueue.add(ongoingAcsRequest);
			break;
			
			case 1:
				ongoingAcsRequest = new ActivityListRequest(
						ONGOING_ACLIST_STATUS,
						maxAcId,
						PAGENUM,
						OFFSET, new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						Gson gson = new Gson();
						
						ActivityListEntity acList = gson.fromJson(response.toString(), ActivityListEntity.class);
						
						activityList.addAll(acList.getActives());
						if(activityList.size()>0){
							maxAcId = activityList.get(activityList.size()-1).getAcid()+"";
						}else{
							maxAcId = "0";
						}
						handlerStateChanged.sendEmptyMessage(0);
						handlerStateChanged.sendEmptyMessage(1);
					}
					
				});
				ongoingAcsRequest.setTag(TAG);
				apiQueue.add(ongoingAcsRequest);
				break;
			
			default:
				
				break;
			}
		}
		
	};
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("PinparActivityListFragment"); //统计页面
	    
	    if (UserManager.getInstance().isLogin()) {
			handlerRoleIsSelectedRequest.sendEmptyMessage(0);
		}
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("PinparActivityListFragment"); 
	}
	
	Handler handlerStateChanged = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				activityListAdapter.notifyDataSetChanged();
				break;
			case 1:
				ongoingActicitiesListView.onRefreshComplete();
				break;
			case 2:
				activityList.clear();
				activityList.addAll((ArrayList<ActivityEntity>)msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	
	Handler handlerRoleIsSelectedRequest = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				getIdentityIsSelectedRequest = new PartyGetIdentityIsSelectedRequest(
						UserManager.getInstance().getUserInfo().getUid()+"",
						new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						Gson gson = new Gson();
						
						RoleIsSelectedEntity roleEntity = gson.fromJson(response.toString(), RoleIsSelectedEntity.class);
						
						roleCount = roleEntity.getRolecount();
					}
					
				});
				getIdentityIsSelectedRequest.setTag(TAG);
				apiQueue.add(getIdentityIsSelectedRequest);
			break;
			
			default:
				
				break;
			}
		}
		
	};
	
}