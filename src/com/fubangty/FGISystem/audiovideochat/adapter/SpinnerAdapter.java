package com.fubangty.FGISystem.audiovideochat.adapter;

import java.util.List;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.entity.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter{
	private Context context;
	private List<Message> list;
	private LayoutInflater inflater;
	
	public SpinnerAdapter(Context context, List<Message> list) {
		super();
		this.context = context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder=null;
		if(v==null){
			v=inflater.inflate(R.layout.spinner_item, null);
			holder=new ViewHolder();
			holder.tv=(TextView) v.findViewById(R.id.tv_spinner);
			v.setTag(holder);
		}else{
			holder=(ViewHolder) v.getTag();
		}
		Message msg=(Message) getItem(position);
		holder.tv.setText(msg.getInvite_man());
		return v;
	}
	class ViewHolder{
		TextView tv;
	}
}
