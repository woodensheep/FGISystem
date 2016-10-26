package com.fubangty.FGISystem.audiovideochat.activity;

import java.util.ArrayList;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.global.Global;
import com.fubangty.FGISystem.audiovideochat.utils.image.BitmapUtils;
import com.fubangty.FGISystem.audiovideochat.zoom.PhotoView;
import com.fubangty.FGISystem.audiovideochat.zoom.ViewPagerFixed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * <font color="green">预览选择图片的界面</font>
 * @ClassName GalleryActivity
 * @author 包宏燕
 * @date 2016年5月31日 下午3:39:27
 *
 * @version
 */
public class GalleryActivity extends Activity {
	private Intent intent;
	// 发送按钮
	private Button send_bt;
	//删除按钮
	private Button del_bt;
	//顶部显示预览图片位置的TextView
	private TextView positionTextView;
	//获取前一个activity传过来的position
	private int position;
	//当前的位置
	private int location = 0;
	
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;
	
	RelativeLayout photo_relativeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plugin_camera_gallery); //切屏到主界面
		
		send_bt = (Button) findViewById(R.id.send_button);
		del_bt = (Button)findViewById(R.id.gallery_del);
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		
		intent = getIntent();
		position = Integer.parseInt(intent.getStringExtra("position"));
		isShowOKBtn();
		//为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for(int i=0; i<BitmapUtils.tempSelectBitmap.size(); i++){
			initListViews(BitmapUtils.tempSelectBitmap.get(i).getBitmap());
		}
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin(10);
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			location = arg0;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	private void initListViews(Bitmap bm){
		if (listViews == null){
			listViews = new ArrayList<View>();
		}
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	
	//返回按钮添加的监听器
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			finish();
		}
	}
	
	//删除按钮添加的监听器
	private class DelListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(listViews.size() == 1){
				BitmapUtils.tempSelectBitmap.clear();
				BitmapUtils.max = 0;
				send_bt.setText("完成"+"(" + BitmapUtils.tempSelectBitmap.size()
						+ "/" + Global.MAX_NUM + ")");
				Intent intent = new Intent("data.broadcast.action");  
                sendBroadcast(intent);  
				finish();
			} else{
				BitmapUtils.tempSelectBitmap.remove(location);
				BitmapUtils.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				send_bt.setText("完成"+"(" + BitmapUtils.tempSelectBitmap.size()
						+ "/" + Global.MAX_NUM + ")");
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	//完成按钮的监听
	private class GallerySendListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			finish();
		}
	}
	
	public void isShowOKBtn(){
		if(BitmapUtils.tempSelectBitmap.size() > 0){
			send_bt.setText("完成"+"(" + BitmapUtils.tempSelectBitmap.size()
					+ "/"+Global.MAX_NUM+")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(Color.WHITE);
		} else{
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}
	
	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/*
	 * 内部类适配器
	 */
	class MyPageAdapter extends PagerAdapter{
		
		private ArrayList<View> listViews;
		private int size;
		
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}
		
		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}
		
		@Override
		public int getCount() {
			return size;
		}
		
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}
		
		public void finishUpdate(View arg0) {
		}
		
		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}
	
}
