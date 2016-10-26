package com.fubangty.FGISystem.deffend.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.adapter.WeeklyMemberAdapter;
import com.fubangty.FGISystem.deffend.entity.WeeklyMember;
import com.fubangty.FGISystem.deffend.http.CallHttpBiz;
import com.fubangty.FGISystem.deffend.http.ConnectionUrl;
import com.fubangty.FGISystem.deffend.http.MyCallback;
import com.fubangty.FGISystem.deffend.parsjson.LoginJson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 周报上传界面
 * 
 * @author lemon
 */
public class WeeklyActivity extends Activity implements MyCallback {

	private ListView lvMember;
	private Button btnReport;
	private EditText etUnits;
	private TextView tvRecordTime;
	private EditText etUserName;
	private EditText etJobContent;
	private List<WeeklyMember> members;
	private SharedPreferences shared;
	private WeeklyMemberAdapter weeklyMemberAdapter;
	private final static int MEMBER = 2;
	private int GENXINMEMBER;
	/** 添加组员按钮 **/
	private Button btnAdd;
	/** WeeklyActivity.this **/
	private Context context;
	private String phoneNum;
	private Object phoneID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_fill_weekly);
		context = this;
		shared = context.getSharedPreferences("config", MODE_PRIVATE);
		phoneNum = shared.getString("phoneNum", 0 + "");
		phoneID = shared.getString("phoneID", 0 + "");
		members = new ArrayList<WeeklyMember>();
		initViews();
		btnAdd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		setListener();

	}

	/**
	 * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
	 * 
	 * requestCode 请求码，即调用startActivityForResult()传递过去的值 resultCode
	 * 结果码，结果码用于标识返回数据来自哪个新Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MEMBER && resultCode != -1) {
			// 得到新Activity 关闭后返回的数据
			String member_name = data.getExtras().getString("member_name");
			String xuncha_situation = data.getExtras().getString("xuncha_situation");
			String monitor = data.getExtras().getString("monitor");
			String others_work = data.getExtras().getString("others_work");
			WeeklyMember member = new WeeklyMember();
			member.setMemberName(member_name);
			member.setXunchaSituation(xuncha_situation);
			member.setMonitor(monitor);
			member.setOthersWork(others_work);
			members.add(member);
			weeklyMemberAdapter.notifyDataSetChanged();
		}
		if (requestCode == GENXINMEMBER && resultCode != -1) {
			// 修改成员的Activity 关闭后返回的数据
			String member_name = data.getExtras().getString("member_name");
			String xuncha_situation = data.getExtras().getString("xuncha_situation");
			String monitor = data.getExtras().getString("monitor");
			String others_work = data.getExtras().getString("others_work");
			members.get(GENXINMEMBER).setMemberName(member_name);
			members.get(GENXINMEMBER).setXunchaSituation(xuncha_situation);
			members.get(GENXINMEMBER).setMonitor(monitor);
			members.get(GENXINMEMBER).setOthersWork(others_work);
			weeklyMemberAdapter.notifyDataSetChanged();
		}
	}

	// 获取当前时间
	private String getSystemTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sDateFormat.format(new Date(System.currentTimeMillis()));
		return date;
	}

	private void initViews() {
		btnReport = (Button) findViewById(R.id.btn_weekly_report);
		lvMember = (ListView) findViewById(R.id.lv_member);
		btnAdd = (Button) findViewById(R.id.btn_add);
		etUnits = (EditText) findViewById(R.id.et_fill_company);
		tvRecordTime = (TextView) findViewById(R.id.tv_recordtime);
		tvRecordTime.setText(getSystemTime());
		etUserName = (EditText) findViewById(R.id.et_head_name);
		etJobContent = (EditText) findViewById(R.id.et_xuncha_work);
		etUnits.setText(shared.getString("weeklyUnits", ""));
		etUserName.setText(shared.getString("weeklyUserName", ""));
	}

	private void setListener() {
		weeklyMemberAdapter = new WeeklyMemberAdapter(context, members);
		lvMember.setAdapter(weeklyMemberAdapter);
		// 添加小组成员
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MemberActivity.class);
				startActivityForResult(intent, MEMBER);
			}
		});

		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isEditText()) {
					Toast.makeText(context, "请输入完整信息", Toast.LENGTH_LONG).show();
				} else {
					shared.edit().putString("weeklyUnits", etUnits.getText().toString().trim()).commit();
					shared.edit().putString("weeklyUserName", etUserName.getText().toString().trim()).commit();
					// 上传周报
					sendMessage(2);
				}
			}
		});

		// listview的单机监听
		lvMember.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg1) {
				Intent intent = new Intent(context, MemberActivity.class);
				intent.putExtra("member_name", members.get(position).getMemberName());
				intent.putExtra("xuncha_situation", members.get(position).getXunchaSituation());
				intent.putExtra("monitor", members.get(position).getMonitor());
				intent.putExtra("others_work", members.get(position).getOthersWork());
				GENXINMEMBER = position;
				startActivityForResult(intent, GENXINMEMBER);
			}
		});

		// listView的长按监听
		lvMember.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg1) {
				dialog(position);
				return true;
			}
		});

	}

	private boolean isEditText() {
		if (etUnits.getText().toString().trim().length() == 0 | etUserName.getText().toString().trim().length() == 0
				| etJobContent.getText().toString().trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * dialog显示判断删除与否，根据id号删除
	 */
	private void dialog(final int id) {
		// 先new出一个监听器，设置好监听
		DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case Dialog.BUTTON_POSITIVE:
					members.remove(id);
					weeklyMemberAdapter.notifyDataSetChanged();
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

	/**
	 * 向后台发送请求
	 * type (每次请求的标记), URL 目标地址, mRequestParams 集合对象, callback 回调
	 */
	public void sendMessage(int type) {
		System.out.println("进入了sendMessage()方法");
		CallHttpBiz call = new CallHttpBiz(this);
		JSONObject jsonObject = new JSONObject();
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < members.size(); i++) {
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("memberName", members.get(i).getMemberName());
				jsonObject1.put("inspect", members.get(i).getXunchaSituation());
				jsonObject1.put("monitor", members.get(i).getMonitor());
				jsonObject1.put("other", members.get(i).getOthersWork());
				jsonArray.put(jsonObject1);
			}
			jsonObject.put("member", jsonArray);
			jsonObject.put("phoneNum", phoneNum);
			jsonObject.put("phoneID", phoneID);
			jsonObject.put("units", etUnits.getText().toString().trim());
			jsonObject.put("recordTime", getSystemTime());
			jsonObject.put("userName", etUserName.getText().toString().trim());
			jsonObject.put("jobContent", etJobContent.getText().toString().trim());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String data = String.valueOf(jsonObject);
		System.out.println("WeeklyActivity:" + data);
		RequestParams params = new RequestParams();
		params.addBodyParameter("data", data); // 手机号码(登录名)
		call.PostData(2, new ConnectionUrl().UPWEEKLY, params, this);
	}

	/**
	 * 访问服务器成功后的回调方法
	 */
	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 2:
			// 判断返回数据state是否为200
			LoginJson loginJson = new LoginJson(context);
			if (loginJson.parserData(data)) {
				finish();
			}
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
		Toast.makeText(getApplicationContext(), "上传失败！请检查网络配置或服务器", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			dialog();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	
	/**
	 * dialog显示判断删除与否，根据id号删除
	 */
	private void dialog() {
		// 先new出一个监听器，设置好监听
		DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case Dialog.BUTTON_POSITIVE:
					finish();
					break;
				case Dialog.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(context); // 先得到构造器
		builder.setTitle("提示"); // 设置标题
		builder.setMessage("是否退出周报填写?"); // 设置内容
		builder.setIcon(R.drawable.tab_rizhi_pressed);// 设置图标，图片id即可
		builder.setPositiveButton("确认", dialogOnclicListener);
		builder.setNegativeButton("取消", dialogOnclicListener);
		builder.create().show();
	}
}
