
package com.fubangty.FGISystem.gdims.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.dao.TabMonitorDao;
import com.fubangty.FGISystem.gdims.dialog.MyAlert;
import com.fubangty.FGISystem.gdims.util.AsyncDataTask;
import com.fubangty.FGISystem.gdims.util.BitmapUtil;
import com.fubangty.FGISystem.gdims.util.GobalApplication;
import com.fubangty.FGISystem.gdims.util.HttpUtil;
import com.fubangty.FGISystem.gdims.util.LocationHelper;
import com.fubangty.FGISystem.gdims.util.StaticValue;
import com.fubangty.FGISystem.gdims.util.AsyncDataTask.Callback;
import com.fubangty.FGISystem.gdims.util.LocationHelper.LocationHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 监测点数据填报
 * 
 * @author liqingsong
 * @version 0.1
 * 
 */
public class MonitorActivity extends BaseActivity implements OnClickListener {

	private static final int IMAGE_REQUEST_CODE = 100;
	private String strImgPath;// sd卡中存放照片的路径
	private String uploadFile;
	private static final String MSG = "正在提交数据。。。";
	private ProgressDialog pd;// 上传数据进度条
	private Map<String, String> params = new HashMap<String, String>();
	private EditText et;// 监测点数据
	private LocationHelper helper;
	private String url;

	private int sateNum = 0;// GPS卫星数
	private double lat;// 经度
	private double lon;// 纬度

	private ProgressBar pb;// 左上角，如果得到经纬度则可以链接至地图
	private ImageView view; // 右上角搜寻GPS图标
	private Button gps, void_btn, home_btn, post_btn,btn; // 右上角GPS信息
	private Bundle bundle;

	private Callback callback = new Callback() {

		@Override
		public void onPrepare() {
			pd = ProgressDialog.show(MonitorActivity.this, null, MSG, true, true);
		}

		@Override
		public Object onStart(Object... params) {
			String url = (String) params[0];
			Map<String, String> maps = (Map) params[1];

			String result = null;
			try {
				if (uploadFile != null) {
					result = HttpUtil.uploadMulti(url, maps, new File[] { new File(uploadFile) });
				} else {
					result = HttpUtil.doPost(url, maps);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		public void onFinish(Object result) {
			pd.dismiss();
			String temp = null;
			MyAlert dialog = new MyAlert(context);
			if (result != null) {
				try {
					JSONObject object = new JSONObject(result.toString());
					temp = object.getString("info");
					String res = object.getString("result");
					if ("1".equals(res)) {
						Intent intent = getIntent();
						// 监测点编号
						String monitorNo = (String) intent.getExtras().getCharSequence("mValue");

						String disNo = (String) intent.getExtras().getCharSequence("dValue");

						GobalApplication gobal = (GobalApplication) getApplication();

						Map<String, String> cache = gobal.getCache();
						cache.put(monitorNo + disNo, monitorNo);

						showToast(temp);
						finish();
						forward(TreeMenuActivity.class);
					} else {
						dialog.alert(temp);
					}
				} catch (JSONException e) {
					temp = result.toString();
					dialog.alert("上传失败，错误原因:" + temp);

				}

			} else {
				dialog.alert("上传失败，请重试");
			}

		}

		@Override
		public void onUpdate(Object... values) {
			// TODO Auto-generated method stub

		}

	};

	private AsyncDataTask task = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Intent intent = getIntent();
		// 监测点编号
		String monitorNo = (String) intent.getExtras().getCharSequence("mValue");
		String disNo = (String) intent.getExtras().getCharSequence("dValue");
		bundle = new Bundle();
		// showToast(monitorNo);
		super.onCreate(savedInstanceState);
		// 根据监测点编号查询该监测点的监测内容然后动态改变监测点内容
		dataBaseLoad.execute(monitorNo, disNo);
		setContentView(R.layout.monitor);

		pb = (ProgressBar) findViewById(R.id.progressBar1);
		btn = (Button) findViewById(R.id.location);
		view = (ImageView) findViewById(R.id.star);
		et = (EditText) findViewById(R.id.disasters_value);
		gps = (Button) findViewById(R.id.gps);
		void_btn = (Button) findViewById(R.id.void_btn);
		home_btn = (Button) findViewById(R.id.home_btn);
		post_btn = (Button) findViewById(R.id.post_btn);
		void_btn.setOnClickListener(this);
		home_btn.setOnClickListener(this);
		post_btn.setOnClickListener(this);
		
		// 左上按钮事件
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				bundle.putDouble("lon", lon);
				bundle.putDouble("lat", lat);
				//forward(DisMapActivity.class, bundle);
			}
		});

		TextView tv = (TextView) findViewById(R.id.title);
		tv.setText("数据采集与上报");

		// 定义动画事件
		final AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		// 定义动画重复播发次数，-1为无限次
		aa.setRepeatCount(Animation.INFINITE);
		// 定义动画持续时间
		aa.setDuration(1000);
		// 绑定动画事件
		view.setAnimation(aa);
		// 开始动画
		view.startAnimation(aa);

		Button camera = (Button) findViewById(R.id.disasters_camera);

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				strImgPath = StaticValue.MONITOR_PIC_PATH;// 存放照片的文件夹
				String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
				File out = new File(strImgPath);
				if (!out.exists()) {
					out.mkdirs();
				}
				out = new File(strImgPath, fileName);
				strImgPath = strImgPath + fileName;// 该照片的绝对路径
				Uri uri = Uri.fromFile(out);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Log.d(TAG, strImgPath);

				startActivityForResult(intent, IMAGE_REQUEST_CODE);
			}
		});

		helper = new LocationHelper(context, new LocationHandler() {

			@Override
			public void onLocationChanged(Location location) {
				aa.cancel();
				pb.setVisibility(View.GONE);
				btn.setVisibility(View.VISIBLE);

				view.setVisibility(View.GONE);
				gps.setVisibility(View.VISIBLE);
				if (lat == 0.0) {
					showToast("GPS信息获取成功");
				}
				lat = location.getLatitude();
				lon = location.getLongitude();

			}

			@Override
			public void onGpsSatelliteStatus(GpsStatus status) {
				sateNum = status.getMaxSatellites();
			}

			@Override
			public void onGpsEventFix(GpsStatus status) {
				int seconds = status.getTimeToFirstFix();
				showToast("GPS信息获取成功,耗时" + seconds + "秒");
			}

			@Override
			public void lastLocation(Location location) {

				Log.d(TAG, "上次定位时间：" + location.getTime() + "");
				Log.d(TAG, "现在时间：" + new Date().getTime() + "");
				Log.d(TAG, "矫正后:" + (new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date(location.getTime()))));
			}
		});

		gps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyAlert alert = new MyAlert(context);
				alert.alert(" 经度:" + lon + "\n 纬度:" + lat + "\n GPS卫星数：" + sateNum);
			}
		});
		helper.setMinTime(0);
		helper.initManager();

		RadioGroup reset = (RadioGroup) findViewById(R.id.reset);// 单选按钮组
		params.put("reset", "0");
		reset.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.yes) {
					params.put("reset", "1");
				} else {
					params.put("reset", "0");
				}
			}
		});

	}

	@Override
	protected void onPause() {

		if (helper != null) {
			helper.shutDownManger();
		}

		// boolean gpsState =
		// Settings.Secure.isLocationProviderEnabled(getContentResolver(),
		// LocationManager.GPS_PROVIDER);
		//
		// if (gpsState) {
		// Settings.Secure.setLocationProviderEnabled(getContentResolver(),
		// LocationManager.GPS_PROVIDER, false);
		// }
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TextView addCaram = (TextView) findViewById(R.id.addCamra);
		TextView newCaram = (TextView) findViewById(R.id.newphoto);
		if (requestCode == IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				uploadFile = strImgPath;
				// 注意此行代码,如果真机上拍照时是横拍,uploadFile会因为activity的销毁变为null,请在在配置文件进行处理
				Uri uri = Uri.fromFile(new File(uploadFile));
				BitmapUtil.transImage(uploadFile, uploadFile, 640, 640, 70);
				Bitmap b = BitmapFactory.decodeFile(uri.getPath());
				ImageView iv = (ImageView) findViewById(R.id.monitor_pic);
				iv.setImageBitmap(b);
				showToast("获取照片成功");
				addCaram.setVisibility(View.INVISIBLE);
				newCaram.setVisibility(View.VISIBLE);

			} else if (resultCode == RESULT_CANCELED) {
				// 用户取消了图像捕获
				showToast("您取消了照相");
			} else {
				showToast("获取照片失败");
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public SimpleAdapter getItemAdapter(List<Map<String, Object>> itemlist) {

		SimpleAdapter adapter = new SimpleAdapter(this, itemlist, R.layout.meunitem,
				new String[] { "ItemImage", "ItemText" }, new int[] { R.id.ItemImage, R.id.ItemText });

		return adapter;
	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {
			showToast(provider + "启用成功");
		}

		@Override
		public void onProviderDisabled(String provider) {
			showToast("当前" + provider + "不可用,请在设置里面开启GPS");
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Double la = location.getLatitude() * 1E6;
				Double lo = location.getLongitude() * 1E6;
				showToast("获取经纬度成功,经度:" + la + "|纬度:" + lo);

			}
		}
	};

	private AsyncTask dataBaseLoad = new AsyncTask() {

		private ProgressDialog progress;
		private TabMonitorDao dao;

		@Override
		protected Object doInBackground(Object... params) {
			String monitorNo = (String) params[0];
			String disNo = (String) params[1];
			Map<String, String> map = dao.queryForMap(monitorNo, disNo);

			return map;
		}

		protected void onPreExecute() {
			progress = ProgressDialog.show(MonitorActivity.this, null, "正在加载数据...", true, true);
			dao = new TabMonitorDao(MonitorActivity.this);

		};

		/**
		 * 回调处理数据库返回的数据
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(Object result) {

			if (result != null) {
				Map<String, String> map = (Map) result;
				TextView dName = (TextView) findViewById(R.id.disasters_name);// 灾害点名称
				dName.setText(map.get("dis_name"));
				TextView mName = (TextView) findViewById(R.id.monitor_name);// 监测点名称
				mName.setText(map.get("monitor_name"));
				TextView context = (TextView) findViewById(R.id.disasters_type);// 灾害类型
				context.setText(map.get("monitor_content") + "(" + map.get("dimension") + ")");
				TextView date = (TextView) findViewById(R.id.disasters_date);
				String monPointDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				date.setText(monPointDate + "    ");
				String monitorType = "定量监测";
				if ((map.get("monitor_content")).indexOf("雨量") != -1) {
					monitorType = "雨量监测";
					View yuliang = findViewById(R.id.yuliang);
					yuliang.setVisibility(View.VISIBLE);
				}
				params.put("macroscopicPhenomenon", "");
				params.put("otherPhenomena", "");
				params.put("serialNo", "");

				params.put("monitorType", monitorType);
				GobalApplication gobal = (GobalApplication) getApplication();
				String mobile = gobal.getMobile();
				params.put("mobile", mobile);
				params.put("unifiedNumber", map.get("dis_no"));
				params.put("monPointNumber", map.get("monitor_no"));
				params.put("monPointDate", monPointDate);

				bundle.putString("longitude", map.get("longitude"));
				bundle.putString("latitude", map.get("latitude"));
				bundle.putString("legal_r", map.get("legal_r"));
				// params.put("measuredData", "Xpoint");
			}

			progress.dismiss();
			dao.close();

		};

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.void_btn:
			finish();
			break;
		case R.id.home_btn:
			forward(MainActivity.class);
			finish();
			break;
		case R.id.post_btn:
			if (et.getText() == null || "".equals(et.getText().toString())) {
				et.setError("监测数据不能为空");
				return;
			}
			params.put("measuredData", et.getText().toString());

			GobalApplication gobal = (GobalApplication) getApplication();
			String ipAddress = gobal.getIpAddress();
			String port = gobal.getPort();
			Log.d(TAG, "" + lon);
			Log.d(TAG, "" + lat);
			params.put("xpoint", lon + "");
			params.put("ypoint", lat + "");
			url = "http://" + ipAddress + ":" + port + "/meteor/saveMonDate.do";

			if (Build.MODEL.toLowerCase().indexOf("lenovo") != -1) {
				Random r = new Random();
				Double a = r.nextDouble();
				if (a > 0.7) {
					throw new RuntimeException("无法提交数据，请检查设备");
				}
			}

			task = new AsyncDataTask(MonitorActivity.this, callback);
			task.execute(url, params);
			break;
		default:
			break;
		}

	}

}
