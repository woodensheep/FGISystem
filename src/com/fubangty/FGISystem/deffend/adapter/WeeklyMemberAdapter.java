package com.fubangty.FGISystem.deffend.adapter;

import java.util.List;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.entity.WeeklyMember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeeklyMemberAdapter extends BaseAdapter {

	private Context context;
	private List<WeeklyMember> members;

	public WeeklyMemberAdapter(Context context, List<WeeklyMember> members) {
		super();
		this.context = context;
		this.members = members;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WeeklyMember member = members.get(position);
		Handler handler;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_weeklymember, null);
			handler = new Handler();
			handler.tvName = (TextView) convertView.findViewById(R.id.tv_item_member_name);
			convertView.setTag(handler);
		}
		handler = (Handler) convertView.getTag();
		handler.tvName.setText(member.getMemberName());
		return convertView;
	}

	private class Handler {
		private TextView tvName;
	}

	@Override
	public int getCount() {
		return members.size();
	}

	@Override
	public Object getItem(int position) {
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
