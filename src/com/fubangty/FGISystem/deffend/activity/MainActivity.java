package com.fubangty.FGISystem.deffend.activity;

import java.util.ArrayList;
import java.util.List;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.adapter.PagerAdapter;
import com.fubangty.FGISystem.deffend.fragment.HomeFragment;
import com.fubangty.FGISystem.deffend.fragment.LogFragment;
import com.fubangty.FGISystem.deffend.fragment.MapFragment;
import com.fubangty.FGISystem.deffend.fragment.SituationFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity {
	@ViewInject(value = R.id.vp_main_content)
	private ViewPager vpContent;
	@ViewInject(value = R.id.rg_main_control)
	private RadioGroup rgControl;
	@ViewInject(value = R.id.rb_main_home)
	private RadioButton rbHome;
	@ViewInject(value = R.id.rb_main_log)
	private RadioButton rbLog;
	@ViewInject(value = R.id.rb_main_situation)
	private RadioButton rbSituation;
	@ViewInject(value = R.id.rb_main_map)
	private RadioButton rbMap;
	private List<Fragment> fragments;
	protected int currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		rgControl.setVisibility(View.GONE);
		initViews();
		setListener();
		
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
	}
	
	private void setListener() {
		vpContent.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					rbHome.setChecked(true);
					break;
				case 1:
					rbLog.setChecked(true);
					break;
				case 2:
					rbSituation.setChecked(true);
					break;
				case 3:
					rbMap.setChecked(true);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		rgControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_main_home:
					vpContent.setCurrentItem(0);
					currentFragment = 0;
					break;
				case R.id.rb_main_log:
					vpContent.setCurrentItem(1);
					currentFragment = 1;
					break;
				case R.id.rb_main_situation:
					vpContent.setCurrentItem(2);
					currentFragment = 2;
					break;
				case R.id.rb_main_map:
					vpContent.setCurrentItem(3);
					currentFragment = 3;
					break;
				}
			}
		});
	}

	private void initViews() {
		fragments = new ArrayList<Fragment>();
		String DefendWhich=getIntent().getStringExtra("DefendWhich");
		if("log".equals(DefendWhich)){
			fragments.add(new LogFragment());
		}else if ("situation".equals(DefendWhich)) {
			fragments.add(new SituationFragment());
		}else if ("map".equals(DefendWhich)) {
			fragments.add(new MapFragment());
		}
//		fragments.add(new HomeFragment(vpContent));
		PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
		vpContent.setAdapter(adapter);
	}

	
	/**
	 * 监听返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//			if (rbHome.isChecked()) {
//				return super.onKeyDown(keyCode, event);
//			} else {
//				rbHome.setChecked(true);
//			}
			finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

}
