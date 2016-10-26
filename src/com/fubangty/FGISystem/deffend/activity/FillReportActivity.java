package com.fubangty.FGISystem.deffend.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
 * 填写工作日志
 * 
 * @author lemon
 *
 */
public class FillReportActivity extends Activity implements MyCallback {
	private ImageView ivBack;
	private TextView tvRecordTime;
	private EditText etName;
	private SharedPreferences shared;
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
	/** 保存 **/
	private Button btnSave;
	/** 上传 **/
	private Button btnReport;
	/** 存储日志信息 **/
	private HashMap<String, String> logMap;
	private Context context;
	/** SharedPreferences保存名称+id **/
	public static final String DAILY_LOG = "dailylog";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_fill_report);
		context = this;
		etName = (EditText) findViewById(R.id.et_recorder);
		etCompany = (EditText) findViewById(R.id.et_company);
		etDistrictCounty = (EditText) findViewById(R.id.et_district_county);
		etTownship = (EditText) findViewById(R.id.et_township);
		etDailyWork = (EditText) findViewById(R.id.et_daily_work);
		etEmergencyHandling = (EditText) findViewById(R.id.et_emergency_handling);
		ivBack = (ImageView) findViewById(R.id.iv_worklog_back);
		tvRecordTime = (TextView) findViewById(R.id.tv_recordtime);
		btnSave = (Button) findViewById(R.id.btn_save);
		btnReport = (Button) findViewById(R.id.btn_report);
		tvRecordTime.setText(getSystemTime());
		setlistener();
		initdatas();
	}

	
	/**
	 * 初始化默认数据：姓名、单位等
	 */
	private void initdatas() {
		shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		String Name=shared.getString("logName", "");
		String Company=shared.getString("logCompany", "");
		String DistrictCounty=shared.getString("logDistrictCounty", "");
		String Township=shared.getString("logTownship", "");
		etName.setText(Name);		
		etCompany.setText(Company);		
		etDistrictCounty.setText(DistrictCounty);		
		etTownship.setText(Township);		
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
		if (etName.getText().toString().trim().length() == 0 | tvRecordTime.getText().toString().trim().length() == 0
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

	@Override
	protected void onResume() {
		tvRecordTime.setText(getSystemTime());
		super.onResume();
	}

	/**
	 * 保存到数据库
	 */
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
			values.put("name", etName.getText().toString().trim());
			values.put("time", getSystemTime());
			values.put("company", etCompany.getText().toString());
			values.put("district_county", etDistrictCounty.getText().toString());
			values.put("township", etTownship.getText().toString());
			values.put("daily_work", etDailyWork.getText().toString());
			values.put("emergency_handling", etEmergencyHandling.getText().toString());
			db.insert("dailylog", null, values);
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
		params.addBodyParameter("recordTime", tvRecordTime.getText().toString().trim());
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
				finish();
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
