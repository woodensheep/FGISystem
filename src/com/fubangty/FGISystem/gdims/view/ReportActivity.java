
package com.fubangty.FGISystem.gdims.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.dao.FunctionDao;
import com.fubangty.FGISystem.gdims.dao.ItemsDao;
import com.fubangty.FGISystem.gdims.dialog.MyAlert;
import com.fubangty.FGISystem.gdims.entity.Tools;
import com.fubangty.FGISystem.gdims.util.BitmapUtil;
import com.fubangty.FGISystem.gdims.util.GobalApplication;
import com.fubangty.FGISystem.gdims.util.HttpUtil;
import com.fubangty.FGISystem.gdims.util.StaticValue;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 灾情速报
 * 
 * @author 李青松
 * @version 0.1
 *
 */
public class ReportActivity extends BaseActivity implements View.OnClickListener {
	private String url;
	private Map<String, String> params;
	private int[] pro = { R.id.lonlat, R.id.zhuanyi, R.id.hhfw, R.id.hhmj, R.id.wxrs, R.id.wxfw, R.id.wxmj, R.id.yfys,
			R.id.fzcs, R.id.fzqs };
	private int[] stand = { R.id.zhlx, R.id.zhtj, R.id.sunshi, R.id.wxcc };
	private EditText reportDate;
	private EditText reportTime;
	private final static int DATE_DIALOG = 1;
	private final static int TIME_DIALOG = 2;
	private String strImgPath;
	private final static int IMAGE_REQUEST_CODE = 100;
	private List<String> picPath = new ArrayList<String>();
	private Spinner village;
	private Button button1, button2;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);

		village = createVillage();
	progressBar = (ProgressBar) findViewById(R.id.my_progress);
		TextView tv = (TextView) findViewById(R.id.title);
		tv.setText("灾情速报");

		reportDate = (EditText) findViewById(R.id.reportDate);// 日期
		reportTime = (EditText) findViewById(R.id.reportTime);// 时间

		reportDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		reportTime.setText("00:00:00");
		button1 = (Button) findViewById(R.id.post);
		button2 = (Button) findViewById(R.id.clear);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		addListener();
		dataLoader.execute();
	}
//上传
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.post:
			 if(Tools.isFastDoubleClick()){
				 showToast("请不要重复点击");
				 return;
			 }else{
				 params = getValues();
				 if(params!=null){
				 asyncTask.execute(params); 
			 }
			 }
			 break;
		case R.id.clear:
			
			 if(Tools.isFastDoubleClick()){
				 showToast("请不要重复点击");
				 return;
			 }else{
				 Intent intent = getIntent();
					finish();
					startActivity(intent);
			 }
			break;
			default:
				break;
		}
	}

	/**
	 * 初始化乡镇下拉菜单
	 * 
	 * @return
	 */
	public Spinner createVillage() {

		Spinner spinner = (Spinner) findViewById(R.id.village);// 乡镇
		final ItemsDao itemsDao = new ItemsDao(context);
		String[] items = itemsDao.getTownItems();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				String town = (String) parent.getAdapter().getItem(position);

				// String[] items = itemsDao.getCountryItems(town);
				//
				// ArrayAdapter<String> adapter = new
				// ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,items);
				// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// hamlet.setAdapter(adapter);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		return spinner;
	}

	public void addListener() {

		reportDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG);
			}
		});

		reportTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG);
			}
		});

		Button camera = (Button) findViewById(R.id.camera);
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				strImgPath = StaticValue.REPORT_PIC_PATH;
				String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
				File file = new File(strImgPath);
				if (!file.exists()) {
					file.mkdir();
				}
				File out = new File(strImgPath, fileName);
				Uri uri = Uri.fromFile(out);
				strImgPath += fileName;
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Log.d(TAG, strImgPath);
				startActivityForResult(intent, IMAGE_REQUEST_CODE);
			}
		});
	}

	/**
	 * 照相后回调方法
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, requestCode + "");
		Log.d(TAG, resultCode + "");
		if (requestCode == IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.d(TAG, strImgPath);
				if (strImgPath != null) {
					int i = picPath.size() > 4 ? 4 : picPath.size();
					BitmapUtil.transImage(strImgPath, strImgPath, 640, 480, 70);
					picPath.add(i, strImgPath);
					TextView num = (TextView) findViewById(R.id.pic_number);
					num.setText(picPath.size() + "/5");
					progressBar.setProgress((int)(progressBar.getProgress()+20));
					
					if(picPath.size()==5){
						showToast("拍照完成");
					}
				}

			} else {
				showToast("您取消了照相");
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		final Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int moth = ca.get(Calendar.MONTH);
		int day = ca.get(Calendar.DAY_OF_MONTH);
		int minute = ca.get(Calendar.MINUTE);
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		Dialog dialog = null;
		switch (id) {
		case DATE_DIALOG:
			dialog = new DatePickerDialog(this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String month = monthOfYear + 1 >= 10 ? "" + (monthOfYear + 1) : "0" + (monthOfYear + 1);
					String day = dayOfMonth >= 10 ? "" + dayOfMonth : "0" + dayOfMonth;
					reportDate.setText(new String(year + "-" + month + "-" + day));
				}
			}, year, moth, day);
			break;

		default:
			dialog = new TimePickerDialog(context, new OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					reportTime.setText(hourOfDay + ":" + minute);
				}
			}, hour, minute, true);
			break;
		}

		return dialog;
	}

	public SimpleAdapter getItemAdapter(List<Map<String, Object>> itemlist) {

		SimpleAdapter adapter = new SimpleAdapter(this, itemlist, R.layout.meunitem,
				new String[] { "ItemImage", "ItemText" }, new int[] { R.id.ItemImage, R.id.ItemText });

		return adapter;
	}

	/**
	 * 获取所有文本框的数据
	 * 
	 * @return
	 */
	private Map<String, String> getValues() {

		EditText hamlet = (EditText) findViewById(R.id.hamlet);// 村
		EditText place = (EditText) findViewById(R.id.place);// 地方
		EditText lon_du = (EditText) findViewById(R.id.lon_du);// 经度-度
		EditText lon_fen = (EditText) findViewById(R.id.lon_fen);// 经度-分
		EditText lon_miao = (EditText) findViewById(R.id.lon_miao);// 经度-秒

		EditText lat_du = (EditText) findViewById(R.id.lat_du);// 纬度-度
		EditText lat_fen = (EditText) findViewById(R.id.lat_fen);// 纬度-分
		EditText lat_miao = (EditText) findViewById(R.id.lat_miao);// 纬度-秒

		EditText disasterScale = (EditText) findViewById(R.id.disasterScale);// 灾害规模、体积
		Spinner disasterType = (Spinner) findViewById(R.id.disasterType);// 灾害类型
		EditText victims = (EditText) findViewById(R.id.victims);// 受灾人数
		EditText deadNum = (EditText) findViewById(R.id.deadNum);// 死亡人数
		EditText missNum = (EditText) findViewById(R.id.missNum);// 失踪人数
		EditText injuredNum = (EditText) findViewById(R.id.injuredNum);// 受伤人数

		EditText directLosses = (EditText) findViewById(R.id.directLosses);// 直接损失
		EditText destroyHouse = (EditText) findViewById(R.id.destroyHouse);// 毁坏房屋户
		EditText destroyBuilding = (EditText) findViewById(R.id.destroyBuilding);// 毁坏房屋间
		EditText destroyScale = (EditText) findViewById(R.id.destroyScale);// 毁坏房屋面积

		EditText transferNum = (EditText) findViewById(R.id.transferNum);// 转移户数
		EditText transferPeople = (EditText) findViewById(R.id.transferPeople);// 转移人数
		EditText threatenNum = (EditText) findViewById(R.id.threatenNum);// 威胁人数
		EditText threatenProperyt = (EditText) findViewById(R.id.threatenProperyt);// 威胁财产

		EditText threatenHouse = (EditText) findViewById(R.id.threatenHouse);// 威胁房屋户
		EditText threatenHouseNum = (EditText) findViewById(R.id.threatenHouseNum);// 我i现房屋间
		EditText threatenScale = (EditText) findViewById(R.id.threatenScale);// 威胁房屋面积
		Spinner factor = (Spinner) findViewById(R.id.factor);// 引发因素
		EditText development = (EditText) findViewById(R.id.development);// 发展趋势
		EditText controlMethod = (EditText) findViewById(R.id.controlMethod);// 防治措施
		EditText remark = (EditText) findViewById(R.id.remark);// 备注

		EditText qzh = (EditText) findViewById(R.id.qzh);// 潜在威胁人
		EditText qzr = (EditText) findViewById(R.id.qzr);// 潜在威胁户
		MyAlert alert = new MyAlert(context);
		if (!lon_du.getText().toString().equals("")) {

			int num = Integer.parseInt(lon_du.getText().toString());
			if (num > 180) {
				alert.alert("经度的度不能超过180");
				return null;
			}
		}

		if (!lon_fen.getText().toString().equals("")) {

			int num = Integer.parseInt(lon_fen.getText().toString());
			if (num > 60) {
				alert.alert("经度的分不能超过60");
				return null;
			}
		}

		if (!lon_miao.getText().toString().equals("")) {

			int num = Integer.parseInt(lon_miao.getText().toString());
			if (num > 60) {
				alert.alert("经度的秒不能超过60");
				return null;
			}
		}

		if (!lat_du.getText().toString().equals("")) {

			int num = Integer.parseInt(lat_du.getText().toString());
			if (num > 180) {
				alert.alert("纬度的度不能超过180");
				return null;
			}
		}

		if (!lat_fen.getText().toString().equals("")) {

			int num = Integer.parseInt(lat_fen.getText().toString());
			if (num > 60) {
				alert.alert("纬度的分不能超过60");
				return null;
			}
		}

		if (!lat_miao.getText().toString().equals("")) {

			int num = Integer.parseInt(lat_miao.getText().toString());
			if (num > 60) {
				alert.alert("纬度的秒不能超过60");
				return null;
			}
		}

		Map<String, String> map = new HashMap<String, String>();

		// map.put("disasterScale", et4.getText());
		// map.put("threatenHouse", et5.getText());
		// map.put("threatenHouseNum", et6.getText());
		// map.put("threatenNum", et7.getText());
		// map.put("threatenScale", et8.getText());
		String happenTime = reportDate.getText().toString() + " " + reportTime.getText().toString() + ":00";
		System.out.println("时间"+happenTime);
		map.put("happenTime", happenTime);
		map.put("hamlet", hamlet.getText().toString());
		System.out.println("村："+hamlet.getText().toString());
		map.put("village", village.getSelectedItem().toString());
		System.out.println("乡镇:"+village.getSelectedItem().toString());
		map.put("place", place.getText().toString());
		System.out.println("地址"+ place.getText().toString());
		String longitude = lon_du.getText() + "-" + lon_fen.getText() + "-" + lon_miao.getText();
		String latitude = lat_du.getText() + "-" + lat_fen.getText() + "-" + lat_miao.getText();
		System.out.println("经纬度："+longitude+"-"+latitude);
		map.put("longitude", longitude);
		map.put("latitude", latitude);
		map.put("disasterType", disasterType.getSelectedItem().toString());
		System.out.println("灾害类型:"+disasterType.getSelectedItem().toString());
		map.put("disasterScale", disasterScale.getText().toString());
		System.out.println("灾害规模："+disasterScale.getText().toString());
		map.put("victims", victims.getText().toString());
		System.out.println("受灾人数:"+victims.getText().toString());
		map.put("deadNum", deadNum.getText().toString());
		System.out.println("死亡人数："+deadNum.getText().toString());
		map.put("missNum", missNum.getText().toString());
		System.out.println("失踪人数:"+missNum.getText().toString());
		map.put("injuredNum", injuredNum.getText().toString());
		System.out.println("受伤人数："+injuredNum.getText().toString());
		map.put("directLosses", directLosses.getText().toString());
		System.out.println("直接损失："+directLosses.getText().toString());
		map.put("destroyHouse", destroyHouse.getText().toString());
		System.out.println("毁坏户："+destroyHouse.getText().toString());
		map.put("destroyBuilding", destroyBuilding.getText().toString());
		System.out.println("毁坏间："+destroyBuilding.getText().toString());
		map.put("destroyScale", destroyScale.getText().toString());
		System.out.println("毁坏房屋面积："+destroyScale.getText().toString());
		map.put("transferNum", transferNum.getText().toString());
		System.out.println("转移户数："+transferNum.getText().toString());
		map.put("transferPeople", transferPeople.getText().toString());
		System.out.println("转移人数："+transferPeople.getText().toString());
		map.put("threatenNum", threatenNum.getText().toString());
		System.out.println("威胁人数:"+threatenNum.getText().toString());
		map.put("threatenProperyt", threatenProperyt.getText().toString());
		System.out.println("威胁财产："+threatenProperyt.getText().toString());
		map.put("threatenHouse", threatenHouse.getText().toString());
		System.out.println("威胁户："+threatenHouse.getText().toString());
		map.put("threatenHouseNum", threatenHouseNum.getText().toString());
		System.out.println("威胁间："+threatenHouseNum.getText().toString());
		map.put("threatenScale", threatenScale.getText().toString());
		System.out.println("威胁房屋面积："+threatenScale.getText().toString());
		map.put("factor", factor.getSelectedItem().toString());
		System.out.println("引发因素："+ factor.getSelectedItem().toString());
		map.put("development", development.getText().toString());
		System.out.println("发展趋势："+development.getText().toString());
		map.put("controlMethod", controlMethod.getText().toString());
		System.out.println("防治措施："+controlMethod.getText().toString());
		map.put("remark", remark.getText().toString());
		System.out.println("备注："+remark.getText().toString());
		GobalApplication gobal = (GobalApplication) getApplication();
		String mobile = gobal.getMobile();
		map.put("mobile", mobile);
		map.put("remark", remark.getText().toString());

		return map;
	}

	/**
	 * 
	 * @param ids
	 */
	public void dismissView(int[] ids) {
		for (int i = 0; i < ids.length; i++) {
			View view = findViewById(ids[i]);
			if (view != null) {
				view.setVisibility(View.GONE);
			}
		}
	}

	private AsyncTask dataLoader = new AsyncTask() {

		private static final String MSG = "正在加载数据...";
		private FunctionDao dao;
		private ProgressDialog pd;

		@Override
		protected Object doInBackground(Object... params) {
			Map<String, String> map = dao.queryForFunction();
			return map;
		}

		protected void onPostExecute(Object result) {

			pd.dismiss();
			dao.close();

			if (result != null) {
				Map<String, String> map = (Map<String, String>) result;
				String type = map.get("context");
				if ("0".equals(type)) {
					dismissView(pro);
					dismissView(stand);
				} else if ("1".equals(type)) {
					dismissView(pro);
				} else {
					View view = findViewById(R.id.qzwx);
					view.setVisibility(View.GONE);
				}
			} else {
				showToast("没有找到的配置灾情速报类型");
			}
		};

		protected void onPreExecute() {
			pd = ProgressDialog.show(ReportActivity.this, null, MSG);
			dao = new FunctionDao(ReportActivity.this);

		};

	};

	private AsyncTask asyncTask = new AsyncTask() {

		private static final String MSG = "正在提交数据...";
		private ProgressDialog pd;

		@Override
		protected Object doInBackground(Object... params) {

			Map<String, String> map = (Map<String, String>) params[0];
			File[] uploads = new File[picPath.size()];

			for (int i = 0; i < picPath.size(); i++) {
				uploads[i] = new File(picPath.get(i));
			}
			String result = null;

			GobalApplication gobal = (GobalApplication) getApplication();
			String ipAddress = gobal.getIpAddress();
			String port = gobal.getPort();

			url = "http://" + ipAddress + ":" + port + "/meteor/saveQuickReported.do";
			try {
				result = HttpUtil.uploadMulti(url, map, uploads);
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getLocalizedMessage());
				result = "网络参数错误";
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				result = "网络连接失败";
			}
			return result;
		}

		protected void onPostExecute(Object result) {
			pd.dismiss();
			String temp = null;
			if (result != null) {
				try {
					JSONObject object = new JSONObject(result.toString());
					temp = object.getString("info");
				} catch (JSONException e) {
					e.printStackTrace();
					temp = result.toString();
				}
			} else {
				temp = "上传数据失败，请稍后再试";
			}
			showToast(temp);
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		};

		protected void onPreExecute() {

			pd = ProgressDialog.show(context, null, MSG);
		};
	};
}

class Listener implements OnCheckedChangeListener {

	private Map<Integer, String> value = new HashMap<Integer, String>();

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			value.put(buttonView.getId(), buttonView.getText().toString());
		} else {
			value.remove(buttonView.getId());
		}

	}

	/**
	 * 获取多选框组选中的值
	 * 
	 * @return
	 */
	public String getValues() {
		if (value.isEmpty()) {
			return null;
		}
		String values = "";
		for (String str : value.values()) {
			values = str + value + ",";
		}
		return values;
	}

};
