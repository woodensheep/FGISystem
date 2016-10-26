package com.fubangty.FGISystem.deffend.adapter;

import java.util.HashMap;
import java.util.List;

import com.fubangty.FGISystem.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DailyLogAdapter extends BaseAdapter {

	private Context context;
	private List<HashMap<String, String>> dailylogs;

	public DailyLogAdapter(Context context, List<HashMap<String, String>> dailylogs) {
		super();
		this.context = context;
		this.dailylogs = dailylogs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HashMap<String, String> map = dailylogs.get(position);
		Handler handler;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_dailylog, null);
			handler = new Handler();
			handler.tvNum = (TextView) convertView.findViewById(R.id.tv_item_num);
			handler.tvName = (TextView) convertView.findViewById(R.id.tv_item_name);
			handler.tvTime = (TextView) convertView.findViewById(R.id.tv_item_time);
			convertView.setTag(handler);
		}
		handler = (Handler) convertView.getTag();
		position++;
		handler.tvNum.setText(position+"");
		handler.tvName.setText(map.get("name"));
		handler.tvTime.setText(map.get("time"));
		return convertView;
	}

	private class Handler {
		
		private TextView tvNum;
		private TextView tvName;
		private TextView tvTime;
	}

	@Override
	public int getCount() {
		return dailylogs.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
