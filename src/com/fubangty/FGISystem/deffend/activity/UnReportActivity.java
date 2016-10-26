package com.fubangty.FGISystem.deffend.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.http.CallHttpBiz;
import com.fubangty.FGISystem.deffend.http.ConnectionUrl;
import com.fubangty.FGISystem.deffend.http.MyCallback;
import com.fubangty.FGISystem.deffend.parsjson.LoginJson;
import com.fubangty.FGISystem.deffend.util.DailyLogDBHelper;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改未上班工作日志
 * 
 * @author lemon
 *
 */
public class UnReportActivity extends Activity implements MyCallback {
	private ImageView ivBack;
	private TextView tvRecordTime;
	private EditText etName;// 姓名
	/** 单位 **/
	private EditText etCompany;
	/** 区县 **/
	private EditText etDistrictCounty;
	/** 乡镇 **/
	private EditText etTownship;
	/** 日常工作开展情况 **/
	private EditText etDailyWork;
	/** 应急处置工作开展情况 **/
	private EditText etEmergencyHandling;
	private Button btnSave, btnReport;
	private Context context;
	// 获取时间数据
	private Intent intent;
	private String time;
	// 数据库mydailylog.db表名
	public static final String DAILY_LOG = "dailylog";
	private SharedPreferences shared;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_fill_report);
		context = this;
		shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		etName = (EditText) findViewById(R.id.et_recorder);
		ivBack = (ImageView) findViewById(R.id.iv_worklog_back);
		tvRecordTime = (TextView) findViewById(R.id.tv_recordtime);
		etCompany = (EditText) findViewById(R.id.et_company);
		etDistrictCounty = (EditText) findViewById(R.id.et_district_county);
		etTownship = (EditText) findViewById(R.id.et_township);
		etDailyWork = (EditText) findViewById(R.id.et_daily_work);
		etEmergencyHandling = (EditText) findViewById(R.id.et_emergency_handling);
		btnSave = (Button) findViewById(R.id.btn_save);
		btnReport = (Button) findViewById(R.id.btn_report);
		intent = getIntent();
		time = intent.getStringExtra("time");
		initData();
		setlistener();
	}

	
	@Override
	protected void onResume() {
		// id为-1说明是已上报的显示，隐藏按钮
		if ("-1".equals(intent.getStringExtra("id"))) {
			btnSave.setVisibility(View.GONE);
			btnReport.setVisibility(View.GONE);
			// 时间变成获取到的时间
			tvRecordTime.setText(time);
		} else {
			btnSave.setVisibility(View.VISIBLE);
			btnReport.setVisibility(View.VISIBLE);
			// 时间变成当前时间
			tvRecordTime.setText(getSystemTime());
		}
		//tvRecordTime.setText(getSystemTime());
		super.onResume();
	}
	// 初始化数据
	private void initData() {
		String name = intent.getStringExtra("name");
		String company = intent.getStringExtra("company");
		String district_county = intent.getStringExtra("district_county");
		String township = intent.getStringExtra("township");
		String daily_work = intent.getStringExtra("daily_work");
		String emergency_handling = intent.getStringExtra("emergency_handling");
		etName.setText(name);
		etCompany.setText(company);
		etDistrictCounty.setText(district_county);
		etTownship.setText(township);
		etDailyWork.setText(daily_work);
		etEmergencyHandling.setText(emergency_handling);
	}

	// 获取当前时间
	private String getSystemTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sDateFormat.format(new Date(System.currentTimeMillis()));
		return date;
	}

	private void setlistener() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 保存数据
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toSave();
			}

		});

		// 上报
		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否输入完整
				if (isEditText()) {
					Toast.makeText(getApplicationContext(), "请输入完整信息", Toast.LENGTH_LONG).show();
				} else {
					shared.edit().putString("logName", etName.getText().toString().trim()).commit();
					shared.edit().putString("logCompany", etCompany.getText().toString()).commit();
					shared.edit().putString("logDistrictCounty",etDistrictCounty.getText().toString()).commit();
					shared.edit().putString("logTownship",etTownship.getText().toString()).commit();
					sendMessage(2);
				}
			}
		});
	}

	protected boolean isEditText() {
		if (etName.getText().toString().trim().length() == 0 
				| tvRecordTime.getText().toString().trim().length() == 0
				| etCompany.getText().toString().trim().length() == 0
				| etDistrictCounty.getText().toString().trim().length() == 0
				| etTownship.getText().toString().trim().length() == 0
				| etDailyWork.getText().toString().trim().length() == 0
				| etEmergencyHandling.getText().toString().trim().length() == 0
				| etName.getText().toString().trim().length() == 0) {

			return true;
		} else {
			return false;
		}
	}

	private void toSave() {

		String name = etName.getText().toString().trim();
		if (name == null || name.length() == 0) {
			Toast.makeText(this, "记录人不能为空！！！", Toast.LENGTH_SHORT).show();
		} else {// 记录人不能为空
			shared.edit().putString("logName", etName.getText().toString().trim()).commit();
			shared.edit().putString("logCompany", etCompany.getText().toString()).commit();
			shared.edit().putString("logDistrictCounty",etDistrictCounty.getText().toString()).commit();
			shared.edit().putString("logTownship",etTownship.getText().toString()).commit();
			SQLiteOpenHelper dbHelper = new DailyLogDBHelper(this, "mydailylog.db", null, 1);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("name", etName.getText().toString());
			values.put("time", getSystemTime());
			values.put("company", etCompany.getText().toString());
			values.put("district_county", etDistrictCounty.getText().toString());
			values.put("township", etTownship.getText().toString());
			values.put("daily_work", etDailyWork.getText().toString());
			values.put("emergency_handling", etEmergencyHandling.getText().toString());
			db.update("dailylog", values, "id = ?", new String[] { intent.getStringExtra("id") });
			finish();
			Toast.makeText(this, "保存成功！！！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 向后台发送请求
	 * 
	 * post请求, type (1表示post,0表示get), URL 目标地址, mRequestParams 集合对象 callback
	 */
	public void sendMessage(int type) {
		System.out.println("进入了sendMessage()方法");
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		// 获取手机号码
		SharedPreferences shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		String phoneNum = shared.getString("phoneNum", "");
		String phoneID = shared.getString("phoneID", "");
		System.out.println("phoneNum:" + phoneNum);
		System.out.println("phoneID:" + phoneID);
		params.addBodyParameter("phoneNum", phoneNum);
		params.addBodyParameter("phoneID", phoneID);
		params.addBodyParameter("userName", etName.getText().toString().trim());
		params.addBodyParameter("recordTime", getSystemTime());
		params.addBodyParameter("units", etCompany.getText().toString().trim());
		params.addBodyParameter("district", etDistrictCounty.getText().toString().trim());
		params.addBodyParameter("township", etTownship.getText().toString().trim());
		params.addBodyParameter("postSituation", etDailyWork.getText().toString().trim());
		params.addBodyParameter("logContent", etEmergencyHandling.getText().toString().trim());
		call.PostData(2, new ConnectionUrl().UPLOG, params, this);
	}

	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 2:
			// 判断返回数据state是否为200
			LoginJson loginJson = new LoginJson(context);
			if (loginJson.parserData(data)) {
				SQLiteOpenHelper dbHelper =new DailyLogDBHelper(this, "mydailylog.db", null, 1);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.delete("dailylog", "time=?", new String[] {time});
				UnReportActivity.this.finish();
				Runtime runtime = Runtime.getRuntime();
			      try {
			        
			        runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
			        
			      } catch (IOException e) {
			      }
			}
			break;

		default:
			break;
		}

	}

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
		builder.setMessage("是否退出日志填写?"); // 设置内容
		builder.setIcon(R.drawable.tab_rizhi_pressed);// 设置图标，图片id即可
		builder.setPositiveButton("确认", dialogOnclicListener);
		builder.setNegativeButton("取消", dialogOnclicListener);
		builder.create().show();
	}

}
