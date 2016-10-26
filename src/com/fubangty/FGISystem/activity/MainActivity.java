package com.fubangty.FGISystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.activity.SpeakLoginActivity;
import com.fubangty.FGISystem.audiovideochat.activity.VideoLoginActivity;
import com.fubangty.FGISystem.deffend.activity.WeeklyActivity;
import com.fubangty.FGISystem.deffend.util.ServiceUtil;
import com.fubangty.FGISystem.gdims.view.ReportActivity;
import com.fubangty.FGISystem.gdims.view.TreeMenuActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <font color="green">用户主界面</font>
 * @ClassName LoginActivity
 * @author Administrator
 * @date 2016年9月19日 下午2:19:26
 *
 * @version
 */
public class MainActivity extends Activity {

	private SharedPreferences sp;
	ExpandableListView mainlistview = null;
	List<Integer> parent = null;
	Map<Integer, List<String>> map = null;
	MyAdapter adapter;
	private int roleType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_all);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		roleType = sp.getInt("roletype", -1);
		System.out.println("roleType:"+roleType);
		mainlistview = (ExpandableListView) this.findViewById(R.id.lv_main_list);
		mainlistview.setGroupIndicator(null);
		initData();
		adapter = new MyAdapter();
		mainlistview.setAdapter(adapter);
		//子item的点击事件
		mainlistview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Intent intent=null;
				if(groupPosition==0&&childPosition==0){
					//视频通话中心
					intent = new Intent(MainActivity.this,VideoLoginActivity.class);
				}else if(groupPosition==0&&childPosition==1){
					//语音通话中心
					intent = new Intent(MainActivity.this,SpeakLoginActivity.class);
				}else if(groupPosition==1&&childPosition==0){
					if(roleType==0){
						//灾害点监测
						intent=new Intent(MainActivity.this,TreeMenuActivity.class);
					}else if(roleType==1){
						//片区工作周报
						intent=new Intent(MainActivity.this,WeeklyActivity.class);
					}else{
						//驻守工作日志
						intent=new Intent(MainActivity.this,com.fubangty.FGISystem.deffend.activity.MainActivity.class);
						intent.putExtra("DefendWhich", "log");
					}
				}else if(groupPosition==1&&childPosition==1){
					if(roleType==0){
						//灾险情速报
						intent=new Intent(MainActivity.this,ReportActivity.class);
					}else if(roleType==2){
						//驻守灾情速报
						intent=new Intent(MainActivity.this,com.fubangty.FGISystem.deffend.activity.MainActivity.class);
						intent.putExtra("DefendWhich", "situation");
					}
				}else if(groupPosition==1&&childPosition==2){
					//驻守地图
					intent=new Intent(MainActivity.this,com.fubangty.FGISystem.deffend.activity.MainActivity.class);
					intent.putExtra("DefendWhich", "map");
				}
				startActivity(intent);
				return true;
			}
		});
		//开启定位
		getOpen();
	}

	// 初始化数据
	public void initData() {
		parent = new ArrayList<Integer>();
		map = new HashMap<Integer, List<String>>();
		if(roleType==0){
			parent.add(R.drawable.logo1);
			parent.add(R.drawable.logo3);
			//应急会商
			List<String> list1 = new ArrayList<String>();
			list1.add("视频通话中心");
			list1.add("语音通话中心");
			map.put(parent.get(0), list1);
			//地灾群测群防
			List<String> list2 = new ArrayList<String>();
			list2.add("灾害点监测");
			list2.add("灾险情速报");
			map.put(parent.get(1), list2);
		}else if(roleType==1){
			parent.add(R.drawable.logo1);
			parent.add(R.drawable.logo4);
			//应急会商
			List<String> list1 = new ArrayList<String>();
			list1.add("视频通话中心");
			list1.add("语音通话中心");
			map.put(parent.get(0), list1);
			//片区专管员
			List<String> list2 = new ArrayList<String>();
			list2.add("周报填写");
			map.put(parent.get(1), list2);
		}else if(roleType==2){
			parent.add(R.drawable.logo1);
			parent.add(R.drawable.logo2);
			//应急会商
			List<String> list1 = new ArrayList<String>();
			list1.add("视频通话中心");
			list1.add("语音通话中心");
			map.put(parent.get(0), list1);
			//驻守人员管理
			List<String> list2 = new ArrayList<String>();
			list2.add("日志填报");
			list2.add("灾情速报");
			list2.add("地图定位");
			map.put(parent.get(1), list2);
		}
	}
	
	/**
	 * 注册闹钟定时器
	 */
	public void getOpen() {
		if (!ServiceUtil.isServiceRunning(getApplicationContext(), "com.fubangty.FGISystem.deffend.service.BackGroundService")) {
			ServiceUtil.invokeTimerPOIService(getApplicationContext());// 启动定时器
			Toast.makeText(this, "定位开启中...", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "定位已经开启！", Toast.LENGTH_SHORT).show();
			System.out.println("service正在在运行...");
		}
	}
	
	/*
	 * 适配器
	 */
	class MyAdapter extends BaseExpandableListAdapter {
      
		//得到子item需要关联的数据
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			int key = parent.get(groupPosition);
			return (map.get(key).get(childPosition));
		}

		//得到子item的ID
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		//设置子item的组件
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			int key = MainActivity.this.parent.get(groupPosition);
			String info = map.get(key).get(childPosition);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MainActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.layout_children, null);
			}
			TextView tv = (TextView) convertView
					.findViewById(R.id.second_textview);
			tv.setText(info);
			return tv;
		}

		//获取当前父item下的子item的个数
		@Override
		public int getChildrenCount(int groupPosition) {
			int key = parent.get(groupPosition);
			int size = map.get(key).size();
			return size;
		}
		
		//获取当前父item的数据
		@Override
		public Object getGroup(int groupPosition) {
			return parent.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return parent.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		//设置父item组件
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MainActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.layout_parent, null);
			}
			ImageView tv = (ImageView) convertView
					.findViewById(R.id.iv_image);
//			tv.setImageResource(MainActivity.this.parent.get(groupPosition));
			tv.setBackgroundResource(MainActivity.this.parent.get(groupPosition));
			return tv;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
}
