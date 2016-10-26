package com.fubangty.FGISystem.deffend.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.adapter.DailyLogAdapter;
import com.fubangty.FGISystem.deffend.http.CallHttpBiz;
import com.fubangty.FGISystem.deffend.http.ConnectionUrl;
import com.fubangty.FGISystem.deffend.http.MyCallback;
import com.fubangty.FGISystem.deffend.util.DailyLogDBHelper;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 日志已上报和未上报界面
 * 
 * @author lemon
 *
 */
public class DailylogActivity extends Activity implements MyCallback {
	private ListView lvDailyLog;
	/** 已上报集合 **/
	List<HashMap<String, String>> hasDailylogs;
	DailyLogAdapter hasLogAdapter;
	private TextView tvReport;
	private RadioButton rbLogUnreport;
	private SQLiteOpenHelper dbHelper;
	/** DailylogActivity.this **/
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dailylog);
		tvReport = (TextView) findViewById(R.id.tv_log_report);
		lvDailyLog = (ListView) findViewById(R.id.lv_log_info);
		rbLogUnreport = (RadioButton) findViewById(R.id.rb_log_unreport);
		dbHelper = new DailyLogDBHelper(this, "mydailylog.db", null, 1);
		context = this;
		setListener();
	}

	private void setListener() {
		// 填报
		tvReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Log.d("limeng", "tvReport"+"");
				Intent intent = new Intent(context, FillReportActivity.class);
				startActivity(intent);
			}
		});

		// 未上报
		rbLogUnreport.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("limeng", "+++进入了isChecked()方法"+isChecked);
				if (isChecked) {
					// 未上报被选中
					setListView();
				} else {
					// 已上报被选中
					setListView1();
				}
			}

		});

		// 未上报listview的单机监听
		lvDailyLog.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg1) {

				Intent intent = new Intent(context, UnReportActivity.class);
				TextView textView = (TextView) view.findViewById(R.id.tv_item_time);
				if (rbLogUnreport.isChecked()) {
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = db.query("dailylog", null, null, null, null, null, "id" + " DESC");
					if (cursor != null && cursor.moveToFirst()) {
						do {
							HashMap<String, String> map = new HashMap<String, String>();
							int id = cursor.getInt(cursor.getColumnIndex("id"));

							String time = cursor.getString(cursor.getColumnIndex("time"));
							if (time.equals(textView.getText().toString())) {
								// 将要显示的数据传递
								intent.putExtra("id", id + "");
								intent.putExtra("name", cursor.getString(cursor.getColumnIndex("name")));
								intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
								intent.putExtra("company", cursor.getString(cursor.getColumnIndex("company")));
								intent.putExtra("district_county",
										cursor.getString(cursor.getColumnIndex("district_county")));
								intent.putExtra("township", cursor.getString(cursor.getColumnIndex("township")));
								intent.putExtra("daily_work", cursor.getString(cursor.getColumnIndex("daily_work")));
								intent.putExtra("emergency_handling",
										cursor.getString(cursor.getColumnIndex("emergency_handling")));
								break;
							}

						} while (cursor.moveToNext());
						cursor.close();
					}
				}else {
					// 已上报被选中的单击
					HashMap<String, String> map = hasDailylogs.get(position);
					intent.putExtra("id", "-1");// id为-1说明是已上报的显示，隐藏按钮
					intent.putExtra("name", map.get("name"));
					intent.putExtra("time", map.get("time"));
					intent.putExtra("company", map.get("company"));
					intent.putExtra("district_county", map.get("district_county"));
					intent.putExtra("township", map.get("township"));
					intent.putExtra("daily_work", map.get("daily_work"));
					intent.putExtra("emergency_handling", map.get("emergency_handling"));

			}
				startActivity(intent);
			}
		});

		// 未上报listView的长按监听
		lvDailyLog.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg1) {
				if (rbLogUnreport.isChecked()) {
					TextView textView = (TextView) view.findViewById(R.id.tv_item_time);
					// 查询出数据库中的id号
					int id = -1;
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					// 根据时间查询数据库id号
					Cursor cursor = db.query("dailylog", null, "time=?", new String[] { textView.getText().toString() },
							null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						id = cursor.getInt(cursor.getColumnIndex("id"));
					}
					cursor.close();
					dialog(0,id);
				}else{
					
					//已上报不监听长按
				}

				return true;
			}

		});

	}

	/**
	 * dialog显示判断删除与否，根据id号删除
	 */
	private void dialog(int type,final int id) {
		// 先new出一个监听器，设置好监听
		DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case Dialog.BUTTON_POSITIVE:
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("dailylog", "id = ?", new String[] { id + "" });
					setListView();
					break;
				case Dialog.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(context); // 先得到构造器
		builder.setTitle("提示"); // 设置标题
		builder.setMessage("是否删除此条内容?"); // 设置内容
		// builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
		builder.setPositiveButton("确认", dialogOnclicListener);
		builder.setNegativeButton("取消", dialogOnclicListener);
		builder.create().show();
	}

	@Override
	public void onStart() {
		super.onStart();
		changeListView();
	}

	// 未上报被选中
	private void setListView() {
		List<HashMap<String, String>> dailylogs = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("dailylog", null, null, null, null, null, "id" + " DESC");
		if (cursor != null && cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String company = cursor.getString(cursor.getColumnIndex("company"));
				String district_county = cursor.getString(cursor.getColumnIndex("district_county"));
				String township = cursor.getString(cursor.getColumnIndex("township"));
				String daily_work = cursor.getString(cursor.getColumnIndex("daily_work"));
				String emergency_handling = cursor.getString(cursor.getColumnIndex("emergency_handling"));
				map.put("name", name);
				map.put("time", time);
				map.put("company", company);
				map.put("district_county", district_county);
				map.put("township", township);
				map.put("daily_work", daily_work);
				map.put("emergency_handling", emergency_handling);
				dailylogs.add(map);
			} while (cursor.moveToNext());
		}
		cursor.close();
		Log.d("limeng", "setListView:");
		lvDailyLog.setAdapter(new DailyLogAdapter(context, dailylogs));
	}

	private void changeListView() {
		if (rbLogUnreport.isChecked()) {
			setListView();
		} else {
			setListView1();
		}
	}

	// 已上报被选中
	private void setListView1() {
		Toast.makeText(context, "进入了setListView1()方法", 0).show();
		hasDailylogs = new ArrayList<HashMap<String, String>>();
		hasLogAdapter = new DailyLogAdapter(context, hasDailylogs);
		lvDailyLog.setAdapter(hasLogAdapter);
		sendMessage(2, 8 + "", 1 + "");
	}

	// 获取当前时间
	private String getSystemTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sDateFormat.format(new Date(System.currentTimeMillis()));
		return date;
	}

	/**
	 * 向后台发送请求
	 * 
	 * post请求, type (1表示post,0表示get), URL 目标地址, mRequestParams 集合对象 callback
	 */
	public void sendMessage(int type, String pageCount, String currPage) {
		Toast.makeText(context, "进入了sendMessage()方法", 0).show();
		System.out.println("进入了sendMessage()方法");
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		SharedPreferences shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		String phoneNum = shared.getString("phoneNum", 0 + "");
		String phoneID = shared.getString("phoneID", 0 + "");
		params.addBodyParameter("phoneID", phoneID); // 手机号码(登录名)
		params.addBodyParameter("phoneNum", phoneNum); // 手机号码(登录名)
		params.addBodyParameter("currMonth", getSystemTime()); // 时间
		params.addBodyParameter("pageCount", pageCount); // 每页显示条数(默认5)
		params.addBodyParameter("currPage", currPage); // 当前页(默认为1)
		call.PostData(2, new ConnectionUrl().LOGHASREPORT, params, this);
	}

	/**
	 * 访问服务器成功后的回调方法
	 */
	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 2:
			JSONObject jsonObject;
			try {
				hasDailylogs.clear();
				jsonObject = new JSONObject(data);
				String Total = jsonObject.optString("total");
				String CurrPage = jsonObject.optString("currPage");
				JSONArray jsonArray = jsonObject.optJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {// 遍历JSONArray
					JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
					System.out.println("12121__" + jsonObject1.toString());
					String LogID = jsonObject1.optString("logid");
					String UserName = jsonObject1.optString("userName");
					String RecordTime = jsonObject1.optString("recordTime");
					String Units = jsonObject1.optString("units");
					String District = jsonObject1.optString("district");
					String Township = jsonObject1.optString("township");
					String PostSituation = jsonObject1.optString("postSituation");
					String LogContent = jsonObject1.optString("logContent");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("name", UserName);
					RecordTime=RecordTime.replaceAll("T", " ");
					map.put("time", RecordTime);
					map.put("company", Units);
					map.put("district_county", District);
					map.put("township", Township);
					map.put("daily_work", PostSituation);
					map.put("emergency_handling", LogContent);
					hasDailylogs.add(map);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hasLogAdapter.notifyDataSetChanged();
			break;

		default:
			break;
		}

	}

	/**
	 * 访问服务器失败后的回调方法
	 */
	@Override
	public void onFailure(HttpException exception) {

		Toast.makeText(getApplicationContext(), "获取已上报数据失败！请检查网络配置或服务器", Toast.LENGTH_LONG).show();
	}
}
