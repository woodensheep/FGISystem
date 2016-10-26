package com.fubangty.FGISystem.audiovideochat.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fubangty.FGISystem.R;

/**
 * <font color="green">公用标题栏</font>
 * @ClassName Topbar
 * @author 包宏燕
 * @date 2016年5月31日 上午10:46:05
 *
 * @version
 */
public class Topbar extends RelativeLayout{
	private Button leftButton, rightButton;
	public TextView title;
	
	//左侧Button的属性
	private int leftTextColor;
	private Drawable leftBackground;
	private String leftText;
	private float leftTextSize;
	
	//右侧Button属性
	private int rightTextColor;
	private Drawable rightBackground;
	private String rightText;
	private float rightTextSize;
	
	//title属性
	private int titleTextColor;
	private float titleTextSize;
	private String titleText;
	
	//布局属性
	private LayoutParams leftParams, rightParams, titleParams;
	
	//点击事件监听器接口
	public interface TopBarClickListener {
		public void leftclick();
		public void rightclick();
	}
	private TopBarClickListener listener;
	
	//设置监听器
	public void setOnTopBarClickListener(TopBarClickListener listener) {
		this.listener = listener;
	}
	
	/**
	 * 构造方法
	 */
	@SuppressLint({"Recycle", "NewApi"})
	public Topbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		//获取自定义属性和值的映射集合
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
		
		//取出自定义属性 -左侧
		leftBackground = ta.getDrawable(R.styleable.TopBar_leftBackground);
		leftText = ta.getString(R.styleable.TopBar_leftText);
		leftTextSize = ta.getDimension(R.styleable.TopBar_leftTextSize, 18);
		leftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, Color.WHITE);
		
		//取出自定义属性 -右侧
		rightBackground = ta.getDrawable(R.styleable.TopBar_rightBackground);
		rightText = ta.getString(R.styleable.TopBar_rightText);
		rightTextSize = ta.getDimension(R.styleable.TopBar_rightTextSize, 18);
		rightTextColor = ta.getColor(R.styleable.TopBar_rightTextColor, Color.WHITE);
		
		//取出自定义属性 - 标题
		titleText = ta.getString(R.styleable.TopBar_titleText);
		titleTextSize = ta.getDimension(R.styleable.TopBar_titleTextSize, 20);
		titleTextColor = ta.getColor(R.styleable.TopBar_titleTextColor, Color.WHITE);
		
		// 回收TypedArray（避免浪费资源，避免因为缓存导致的错误）
		ta.recycle();
		
		leftButton = new Button(context);
		rightButton = new Button(context);
		title = new TextView(context);
		
		//设置属性 - 左侧
		leftButton.setText(leftText);
		leftButton.setTextColor(leftTextColor);
		leftButton.setBackground(leftBackground);
		
		//设置属性 - 右侧
		rightButton.setText(rightText);
		rightButton.setTextColor(rightTextColor);
		rightButton.setBackground(rightBackground);
		
		//设置属性 - 标题
		title.setText(titleText);
		title.setTextSize(titleTextSize);
		title.setTextColor(titleTextColor);
		title.setGravity(Gravity.CENTER);
		
		//设置整体背景颜色
		setBackgroundColor(Color.rgb(57,62,63));
		
		//设置布局 - 左
		leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		leftParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
		addView(leftButton, leftParams);//将按钮添加进布局中
		
		//设置布局 - 右
		rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		rightParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
		addView(rightButton, rightParams);//将按钮添加进布局中
		
		//设置布局 - 标题
		titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		addView(title, titleParams);//将按钮添加进布局中
		
		//设置监听器
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				listener.leftclick();
			}
		});
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				listener.rightclick();
			}
		});
		
	}

	//设置左Button是否可见
	public void setLeftIsVisible(boolean visible) {
		if (visible) {
			leftButton.setVisibility(View.VISIBLE);
		} else {
			leftButton.setVisibility(View.GONE);
		}
	}
	
	//设置右Button是否可见
	public void setRightIsVisible(boolean visible) {
		if (visible) {
			rightButton.setVisibility(View.VISIBLE);
		} else {
			rightButton.setVisibility(View.GONE);
		}
	}
	
	public void setTitleText(String stitle){
		title.setText(stitle);
	}
	
	public void setLeftText(String stitle){
		leftButton.setText(stitle);
	}
	
}
