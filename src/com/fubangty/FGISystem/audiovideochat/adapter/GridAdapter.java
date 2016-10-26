package com.fubangty.FGISystem.audiovideochat.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.global.Global;
import com.fubangty.FGISystem.audiovideochat.utils.image.BitmapUtils;

/**
 * <font color="green">图片适配器</font>
 * @ClassName GridAdapter
 * @author 包宏燕
 * @date 2016年5月31日 下午1:57:02
 *
 * @version
 */
public class GridAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private int selectedPosition=-1;
	private boolean shape;
	private Context context;
	private Handler handler;
	
	public boolean isShape(){
		return shape;
	}
	
	public void setShape(boolean shape){
		this.shape=shape;
	}

	public GridAdapter(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		inflater = LayoutInflater.from(context);
	}
	
	public void update(){
    	loading();
    }
	
	@Override
	public int getCount() {
		if(BitmapUtils.tempSelectBitmap.size() == Global.MAX_NUM){
			return Global.MAX_NUM;
		}
		return (BitmapUtils.tempSelectBitmap.size()+1);
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}
	
	public int getSelectedPosition() {
		return selectedPosition;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_published_grida,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position == BitmapUtils.tempSelectBitmap.size()){
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_unfocused));
			if (position == 9) {
				holder.image.setVisibility(View.GONE);
			}
		} else{
			holder.image.setImageBitmap(BitmapUtils.tempSelectBitmap
					.get(position).getBitmap());
		}
		
		return convertView;
	}
	
	/*
	 * 使用线程不断加载
	 */
	public void loading() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if(BitmapUtils.max == BitmapUtils.tempSelectBitmap.size()){
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
						break;
					} else{
						BitmapUtils.max += 1;
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
						break;
					}
				}
			}
		}).start();
	}
	
	class ViewHolder {
		public ImageView image;	
	}
	
}
