package com.ipinpar.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipinpar.app.R;
import com.ipinpar.app.entity.ActivityEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PastActivityListAdapter extends BaseAdapter{
	
	private Context mContext;
	private ArrayList<ActivityEntity> aList = new ArrayList<ActivityEntity>();

	
	
	public PastActivityListAdapter(Context mContext) {
		this.mContext = mContext;
	}
	
	

	public PastActivityListAdapter(Context mContext, ArrayList<ActivityEntity> aList) {
		this.mContext = mContext;
		this.aList = aList;
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return aList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return aList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addList(ArrayList<ActivityEntity> activityList){
		aList.addAll(activityList);
	}
	
	public void clearList(){
		aList.clear();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ActivityEntity acEntity = aList.get(position);
		ViewHolder viewHolder;
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater) mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.past_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.ivBackground = (ImageView) convertView.findViewById(R.id.iv_activity_desc);
			viewHolder.ivBackgroundCover = (ImageView) convertView.findViewById(R.id.iv_activity_desc_cover);
			viewHolder.ivActivityStateInviting = (ImageView) convertView.findViewById(R.id.iv_activity_state_inviting);
			viewHolder.ivActivityStateComplete = (ImageView) convertView.findViewById(R.id.iv_activity_state_complete);
			viewHolder.tvAcShortName =  (TextView) convertView.findViewById(R.id.tv_first_text_desc);
			viewHolder.tvAcName = (TextView) convertView.findViewById(R.id.tv_second_text_desc);
			viewHolder.tvAcDynamicTitle =  (TextView) convertView.findViewById(R.id.tv_dynamic_title);
			viewHolder.ivEssentialStatement = (ImageView) convertView.findViewById(R.id.iv_essential_statement);
			viewHolder.tvAcDynamicDescription = (TextView) convertView.findViewById(R.id.tv_dynamic_description);
			
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		ImageLoader.getInstance().displayImage(acEntity.getImgs().get(0).getImg(), viewHolder.ivBackground);
		ImageLoader.getInstance().displayImage("drawable://"+R.drawable.activity_img_cover, viewHolder.ivBackgroundCover);
		if(acEntity.getStatus() == 4){
			viewHolder.ivActivityStateInviting.setVisibility(View.INVISIBLE);
			viewHolder.ivActivityStateComplete.setVisibility(View.VISIBLE);
			viewHolder.ivEssentialStatement.setVisibility(View.INVISIBLE);
		}else if(acEntity.getStatus() == 3){
			viewHolder.ivActivityStateInviting.setVisibility(View.VISIBLE);
			viewHolder.ivActivityStateComplete.setVisibility(View.INVISIBLE);
			viewHolder.ivEssentialStatement.setVisibility(View.VISIBLE);
		}else{
			viewHolder.ivActivityStateInviting.setVisibility(View.INVISIBLE);
			viewHolder.ivActivityStateComplete.setVisibility(View.INVISIBLE);
			viewHolder.ivEssentialStatement.setVisibility(View.INVISIBLE);
		}
		viewHolder.tvAcShortName.setText(acEntity.getSname());
		viewHolder.tvAcName.setText(acEntity.getAcname());
		viewHolder.tvAcDynamicTitle.setText(acEntity.getDynamic().get(0).getTitle());
		viewHolder.tvAcDynamicDescription.setText(acEntity.getDynamic().get(0).getDescription());
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView ivBackground;
		ImageView ivBackgroundCover;
		ImageView ivActivityStateInviting;
		ImageView ivActivityStateComplete;
		TextView tvAcShortName;
		TextView tvAcName;
		TextView tvAcDynamicTitle;
		ImageView ivEssentialStatement;
		TextView tvAcDynamicDescription;
	}

}