package com.ipinpar.app.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.ipinpar.app.PPBaseActivity;
import com.ipinpar.app.R;
import com.ipinpar.app.adapter.MemberExperiListAdapter;
import com.ipinpar.app.adapter.StatementListAdapter;
import com.ipinpar.app.entity.AcImageEntity;
import com.ipinpar.app.entity.AcStatementEntity;
import com.ipinpar.app.entity.ActivityEntity;
import com.ipinpar.app.entity.ActivityStatementListEntity;
import com.ipinpar.app.manager.UserManager;
import com.ipinpar.app.network.api.ActivityDetailRequest;
import com.ipinpar.app.network.api.StatementListRequest;
import com.ipinpar.app.view.RollViewPager;

public class PastCompleteAcDetail extends PPBaseActivity {

	private Context mContext;
	
	//用于存放图片地址的集合
	private ArrayList<String> imageUrls=new ArrayList<String>();
	//用于存放滚动点的集合
	private ArrayList<View> dot_list=new ArrayList<View>();
	//轮播图片的布局
	private LinearLayout top_news_viewpager;
	//轮播图片指引(圆点)的布局
	private LinearLayout dots_ll;
	
	private ProgressDialog wattingDialog;
	
	private StatementListAdapter statementListAdapter;
	private MemberExperiListAdapter memberExperiListAdapter;
	
	//活动ID
	private int acid;
	
	//根据活动ID请求进行中的活动的详细信息
	private ActivityDetailRequest completeAcDetailRequest;
	//根据uid和acid获取最强宣言列表
	private StatementListRequest completeAcStatementListRequest;
	//根据type、uid和acid获取趴友体验列表
	private StatementListRequest completeAcMemberExperiListRequest;
	
	private ArrayList<AcImageEntity> acImageList = new ArrayList<AcImageEntity>();
	
	private ArrayList<AcStatementEntity> acStatementList = new ArrayList<AcStatementEntity>();
	private ArrayList<AcStatementEntity> acMemberExperiList = new ArrayList<AcStatementEntity>();
	
	private Button btnBack;
	private Button btnShare;
	
	private RelativeLayout rlActicityMap;
	private String latitude;
	private String longitude;
	private String shopName;
	
	private TextView tvAcName;
	private TextView tvAcShop;
	private TextView tvAcAddress;
	private TextView tvAcAddressCity;
	private TextView tvAcTimeBegin;
	private TextView tvAcTimeEnd;
	private TextView tvAcRegistEnd;
	private TextView tvAcAllowedNum;
	private TextView tvAcForm;
	private TextView tvAcDetail;
	private TextView tvAcContact;
	
	private ListView statementListView;
	private ListView memberExperiListView;
	private ScrollView statementScrollView;
	
	private View RL_view_memeber_experi;
	private View RL_view_more_statements;
	
	private String shareTitle;
	private String shareContent;
	private String shareImageUrl;
	
	private static String PAST_COMPLETE_ACTIVITY_STATEMENT = "1";
	private static String PAST_COMPLETE_ACTIVITY_MEMBEREXPERI = "2";
	
	private static String PAGENUM = "1";
	private static String OFFSET = "40";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.past_complete_ac_detail);
		mContext = this;
		
		acid = getIntent().getIntExtra("activityID",1);
		
		initView();
		setView();
		
		handlerCompleteAcDetailRequest.sendEmptyMessage(0);
		handlerCompleteAcMemberExperiListRequest.sendEmptyMessage(0);
		handlerCompleteAcStatementListRequest.sendEmptyMessage(0);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void initView() {
		
		//等待进度条
		wattingDialog = new ProgressDialog(mContext);
		//用于将轮播图添加进去
		top_news_viewpager = (LinearLayout) findViewById(R.id.top_sliding_viewpager);
		dots_ll = (LinearLayout) findViewById(R.id.dots_ll_pastcomplete);
		
		btnBack = (Button) findViewById(R.id.btn_back);
		btnShare = (Button) findViewById(R.id.btn_share);
		
		rlActicityMap = (RelativeLayout) findViewById(R.id.RL_pastcomplete_acAddress);
		
		tvAcName = (TextView) findViewById(R.id.tv_acName);
		tvAcShop = (TextView) findViewById(R.id.tv_acShop);
		tvAcAddress = (TextView) findViewById(R.id.tv_acAddress);
		tvAcAddressCity = (TextView) findViewById(R.id.tv_acAddress_city);
		tvAcTimeBegin = (TextView) findViewById(R.id.tv_acTimeBegin);
		tvAcTimeEnd = (TextView) findViewById(R.id.tv_acTimeEnd);
		tvAcRegistEnd = (TextView) findViewById(R.id.tv_acRegistEnd);
		tvAcAllowedNum = (TextView) findViewById(R.id.tv_acAllowedNum);
		tvAcForm = (TextView) findViewById(R.id.tv_acForm);
		tvAcDetail = (TextView) findViewById(R.id.tv_acDetail);
		tvAcContact = (TextView) findViewById(R.id.tv_acContact);
		
		statementListView = (ListView) findViewById(R.id.lv_complete_detail_the_statement);
		memberExperiListView = (ListView) findViewById(R.id.lv_complete_detail_luck_member);
		statementScrollView = (ScrollView) findViewById(R.id.sv_activity_detail_desc);
		
		RL_view_memeber_experi = findViewById(R.id.RL_view_memeber_experi);
		RL_view_more_statements = findViewById(R.id.RL_complete_view_more_statement);
	}
	
	public void setView(){
		rlActicityMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				intent.putExtra("shopname", shopName);
				intent.setClass(mContext, MarkerActivity.class);
				startActivity(intent);
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("onClick", "");
				onBackPressed();
			}
		});
		
		btnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showShare();
			}
		});
		
		RL_view_memeber_experi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(CheckDiaryListActivity.getIntent2Me(mContext, acid));
			}
		});
		
		RL_view_more_statements.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("activityID", acid);
				intent.setClass(mContext, StatementsListActivity.class);
				startActivity(intent);
			}
		});
		statementListView.setFooterDividersEnabled(false);
		statementListView.setOnTouchListener(new View.OnTouchListener() {  
            
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                if(event.getAction() == MotionEvent.ACTION_UP){  
                	statementScrollView.requestDisallowInterceptTouchEvent(false);  
                }else{  
                	statementScrollView.requestDisallowInterceptTouchEvent(false);  
                }  
                return true;   
            }  
        });  
		memberExperiListView.setFooterDividersEnabled(false);
		memberExperiListView.setOnTouchListener(new View.OnTouchListener() {  
    
		    @Override  
		    public boolean onTouch(View v, MotionEvent event) {  
		        if(event.getAction() == MotionEvent.ACTION_UP){  
		        	statementScrollView.requestDisallowInterceptTouchEvent(false);  
		        }else{  
		        	statementScrollView.requestDisallowInterceptTouchEvent(false);  
		        }  
		        return true;  
		    }  
		});  
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
	
	private void initDot() {
		//滚动的个数应该和图片的个数相等
		//清空点所在集合
		dot_list.clear();
		dots_ll.removeAllViews();
		for(int i=0;i<imageUrls.size();i++){
			View view = new View(mContext);
			if(i == 0){
				//红色
				view.setBackgroundResource(R.drawable.dot_focus);
			}else{
				view.setBackgroundResource(R.drawable.dot_blur);
			}
			//指定点的大小
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					10,10);
			//指定点的间距
			layoutParams.setMargins(8, 0, 8, 0);
			//添加到线性布局中
			dots_ll.addView(view,layoutParams);			
			//添加到集合中去
			dot_list.add(view);
		}
	}
	
	Handler slideHandler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			switch (msg.what) {
			case 0:
				//初始划滚动点	
				 initDot();
				 //创建轮播图
				 RollViewPager rollViewPager=new RollViewPager(mContext, dot_list, 
						 new RollViewPager.OnViewClickListener(
						 ) {
					 //用于处理点击图片的逻辑
					@Override
					public void viewClick(String url) {
						Log.e("RollViewPager", "RollViewPager is pressed");
					}
				});
				 //将图片地址添加到轮播图中
				 rollViewPager.initImgUrl(imageUrls);
				 rollViewPager.startRoll();
				 top_news_viewpager.removeAllViews();
			     top_news_viewpager.addView(rollViewPager);
				break;

			default:
				break;
			}
		}
		
	};
	
	public void initActicityDetail(ActivityEntity acticityEntity){
		tvAcName.setText(acticityEntity.getAcname());
		tvAcShop.setText(acticityEntity.getSname());
		shopName = acticityEntity.getSname();
		tvAcAddress.setText(acticityEntity.getAddressdetail());
		tvAcAddressCity.setText(acticityEntity.getAddress2()+acticityEntity.getAddress3());
		
		long timeBegin = Long.parseLong(acticityEntity.getActivebegintime())*1000;
		long timeEnd = Long.parseLong(acticityEntity.getActiveendtime())*1000;
		tvAcTimeBegin.setText(DateFormat.format("yyyy.MM.dd kk:mm", timeBegin));
		tvAcTimeEnd.setText(DateFormat.format("kk:mm", timeEnd));
		
		long timeRegistedEnd = Long.parseLong(acticityEntity.getCreatetime())*1000;
		tvAcRegistEnd.setText(DateFormat.format("yyyy.MM.dd", timeRegistedEnd));
		
		tvAcAllowedNum.setText(acticityEntity.getExperiencecount()+"");
		tvAcForm.setText(acticityEntity.getDescription());
		tvAcDetail.setText(acticityEntity.getDetail());
		tvAcContact.setText(acticityEntity.getPhone());
		
	}
	
	/*
	 * 将ImageURL中的“_bcut”或者“_b”字符串替换成“_s”
	 * */
	public String changeShareImageUrl(String source){
		if((source.indexOf("_bcut") != -1)){
			return source.replace("_bcut", "_s");
		}else{
			return source.replace("_b", "_s");
		}
	}
	
	private void showShare() {
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(shareTitle);
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl("http://www.ipinpar.com");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText(shareContent);
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl("http://m.ipinpar.com/active.jsp?acid="+acid);
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("品趴——给生活加点糖！");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl("http://m.ipinpar.com/active.jsp?acid="+acid);
		 //图片的网络路径，新浪微博、人人、QQ空间和Linked-in
		 oks.setImageUrl(shareImageUrl);
		// 启动分享GUI
		 oks.show(this);
	}
	
	Handler handlerScrollTop = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			switch (msg.what) {
			case 0:
			    	statementScrollView.fullScroll(ScrollView.FOCUS_UP);
				break;

			default:
				break;
			}
		}
		
	};
	
	Handler acticityDetailInfoHandler = new Handler(){

		ActivityEntity acEntity = new ActivityEntity();
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			
			acEntity = (ActivityEntity) msg.obj;
			
			switch (msg.what) {
			case 0:
				initActicityDetail(acEntity);
				break;

			default:
				break;
			}
		}
		
	};
	
	
	Handler handlerCompleteAcDetailRequest = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				wattingDialog.show();
				completeAcDetailRequest = new ActivityDetailRequest(acid+"", new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						Gson gson = new Gson();
						
						//获取返回的活动
						ActivityEntity activity = gson.fromJson(response.toString(), ActivityEntity.class);
						
						latitude = activity.getLatitude();
						longitude = activity.getLongitude();
						
						//获取活动中的图片集合
						acImageList.clear();
						acImageList.addAll(activity.getImgs());
						
						//添加到轮播图对应的控件中
						for(int i=0;i<acImageList.size();i++){
							imageUrls.add(acImageList.get(i).getImg());
						}
						
						shareTitle = activity.getSname() + activity.getAcname();
						shareContent = activity.getDetail();
						shareImageUrl = changeShareImageUrl(acImageList.get(0).getImg());
						
						slideHandler.sendEmptyMessage(0);
						
						Message msg = new Message();
						msg.what = 0;
						msg.obj = activity;
						acticityDetailInfoHandler.sendMessage(msg);
						wattingDialog.dismiss();
					}
					
				});
				completeAcDetailRequest.setTag(TAG);
				apiQueue.add(completeAcDetailRequest);
			break;
			
			default:
				
				break;
			}
		}
		
	};
	
	Handler handlerCompleteAcMemberExperiListRequest = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:

				completeAcMemberExperiListRequest = new StatementListRequest(
						acid+"",
						PAST_COMPLETE_ACTIVITY_MEMBEREXPERI, new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						Gson gson = new Gson();
						
						//获取返回的活动
						ActivityStatementListEntity acMemberExperiListEntity = gson.fromJson(response.toString(), ActivityStatementListEntity.class);
						
						if(acMemberExperiListEntity.getResult().equals("1")){
							acMemberExperiList.clear();
							acMemberExperiList.addAll(acMemberExperiListEntity.getDeclarations());
							
							handlerCompleteAcMemberExperiListRequest.sendEmptyMessage(1);
						}
					}
					
				});
				completeAcMemberExperiListRequest.setTag(TAG);
				apiQueue.add(completeAcMemberExperiListRequest);
			break;
			
			case 1:
				
				memberExperiListAdapter = new MemberExperiListAdapter(mContext,acMemberExperiList,apiQueue);
				memberExperiListView.setAdapter(memberExperiListAdapter);
				
				setListViewHeightBasedOnChildren(memberExperiListView);
				break;
			
			default:
				
				break;
			}
		}
		
	};
	
	
	Handler handlerCompleteAcStatementListRequest = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:

				completeAcStatementListRequest = new StatementListRequest(
						acid+"", 
						PAST_COMPLETE_ACTIVITY_STATEMENT,
//						PAGENUM,
//						OFFSET,
						new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						
						Gson gson = new Gson();
						
						//获取返回的活动
						ActivityStatementListEntity acStatementListEntity = gson.fromJson(response.toString(), ActivityStatementListEntity.class);
						
						if(acStatementListEntity.getResult().equals("1")){
							acStatementList.clear();
							acStatementList.addAll(acStatementListEntity.getDeclarations());
							
							handlerCompleteAcStatementListRequest.sendEmptyMessage(1);
							
							if(acStatementList.size() < 3){
								RL_view_more_statements.setVisibility(View.INVISIBLE);
							}
							
							if(acStatementList.size() > 2){
								Message msg = new Message();
								msg.obj = acStatementList;
								msg.what = 0;
								handlerStateChanged.sendMessage(msg);
							}
						}
					}
					
				});
				completeAcStatementListRequest.setTag(TAG);
				apiQueue.add(completeAcStatementListRequest);
			break;
			
			case 1:
				
				statementListAdapter = new StatementListAdapter(mContext,acStatementList,apiQueue);
				statementListView.setAdapter(statementListAdapter);
				
				setListViewHeightBasedOnChildren(statementListView);
				handlerScrollTop.sendEmptyMessageDelayed(0,500);
				break;
			
			default:
				
				break;
			}
		}
		
	};
	
	Handler handlerStateChanged = new Handler(){

		ArrayList<AcStatementEntity> tempAcStatementList = new ArrayList<AcStatementEntity>();
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				tempAcStatementList.addAll((ArrayList<AcStatementEntity>)msg.obj);
				acStatementList.clear();
				acStatementList.add(tempAcStatementList.get(0));
				acStatementList.add(tempAcStatementList.get(1));
				
				setListViewHeightBasedOnChildren(statementListView);
				
				handlerStateChanged.sendEmptyMessage(1);
				break;
			case 1:
				statementListAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

}
