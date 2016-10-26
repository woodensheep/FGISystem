package com.fubangty.FGISystem.deffend.fragment;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.activity.ToolActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("InflateParams")
public class HomeFragment extends Fragment implements OnClickListener{
	private ImageView ivMap;
	private ImageView ivLog;
	private ImageView ivReport;
	private ImageView ivTool;
	private ViewPager vpContent;
	
	public HomeFragment(ViewPager vpContent) {
		super();
		this.vpContent = vpContent;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_home, null);
		ivMap=(ImageView) v.findViewById(R.id.iv_home_map);
		ivLog=(ImageView) v.findViewById(R.id.iv_home_log);
		ivReport=(ImageView) v.findViewById(R.id.iv_home_subao);
		ivTool=(ImageView) v.findViewById(R.id.iv_home_tool);
		setistener();
		return v;
	}
	private void setistener() {
		ivMap.setOnClickListener(this);
		ivLog.setOnClickListener(this);
		ivReport.setOnClickListener(this);
		ivTool.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_home_map:
			vpContent.setCurrentItem(3);
			break;
		case R.id.iv_home_log:
			vpContent.setCurrentItem(1);
			
			break;
		case R.id.iv_home_subao:
			vpContent.setCurrentItem(2);
			break;
		case R.id.iv_home_tool:
			Intent intent=new Intent(getActivity(),ToolActivity.class);
			startActivity(intent);
			break;
		}
	}
}
