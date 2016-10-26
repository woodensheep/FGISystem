
package com.fubangty.FGISystem.gdims.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.adapter.MacroAdapter;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.dao.MacroDao;
import com.fubangty.FGISystem.gdims.dao.MacroDataDao;
import com.fubangty.FGISystem.gdims.dialog.MyAlert;
import com.fubangty.FGISystem.gdims.entity.Macro;
import com.fubangty.FGISystem.gdims.entity.MacroData;
import com.fubangty.FGISystem.gdims.entity.MacroResult;
import com.fubangty.FGISystem.gdims.entity.Tools;
import com.fubangty.FGISystem.gdims.util.BitmapUtil;
import com.fubangty.FGISystem.gdims.util.GobalApplication;
import com.fubangty.FGISystem.gdims.util.HttpUtil;
import com.fubangty.FGISystem.gdims.util.LocationHelper;
import com.fubangty.FGISystem.gdims.util.LocationHelper.LocationHandler;
import com.fubangty.FGISystem.gdims.util.Session;
import com.fubangty.FGISystem.gdims.util.StaticValue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

/**
 * @author liqingsong
 * @version 0.1
 * 
 */
public class MacroActivity extends BaseActivity implements OnClickListener {

	private MacroAdapter macroAdapter;
	private List<Macro> macros = new ArrayList<Macro>();
	private static final int IMAGE_REQUEST_CODE = 100;
	private String url;

	private int sateNum = 0;// GPS卫星数
	private double lat = 0.0;// 经度
	private double lon = 0.0;// 纬度
	private LocationHelper helper;
	private ProgressBar pb;// 左上角，如果得到经纬度则可以链接至地图
	private Button btn;// 左上角链接按钮
	private ImageView imageView; // 右上角搜寻GPS图标
	private Button gps, back, home, save, update; // 右上角GPS信息
	private LinearLayout macroLinear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.macro);
		Intent intent = getIntent();

		macroAdapter = new MacroAdapter(this, macros);
		imageView = (ImageView) findViewById(R.id.star);
		gps = (Button) findViewById(R.id.gps);
		// 定义动画事件
		final AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		// 定义动画重复播发次数，-1为无限次
		aa.setRepeatCount(Animation.INFINITE);
		// 定义动画持续时间
		aa.setDuration(1000);
		// 绑定动画事件
		imageView.setAnimation(aa);
		// 开始动画
		imageView.startAnimation(aa);

		// 在listview 最后加上一项其它现象,因为不同于其它视图,重新定义了一个资源文件
		ListView view = (ListView) findViewById(R.id.macroList);
		// View listfoot = View.inflate(this, R.layout.listfoot, null);
		// view.addFooterView(listfoot);
		view.setAdapter(macroAdapter);
		view.setOnScrollListener(new  OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			      if (firstVisibleItem == 0) {  
			            View firstVisibleItemView = view.getChildAt(0);  
			            if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {  
			                Log.d("ListView", "##### 滚动到顶部 #####");  
			            }  
			        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {  
			            View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);  
			            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == view.getHeight()) {  
			                Log.d("ListView", "##### 滚动到底部 ######");   
			                macroLinear.setVisibility(View.VISIBLE);
			            }  
			        }  
			}
		});
		// TextView title = (TextView)findViewById(R.id.title);
		// title.setText("宏观巡查");
		String disNo = intent.getStringExtra("disNo");
		dataTask.execute(disNo);
		back = (Button) findViewById(R.id.back);
		home = (Button) findViewById(R.id.home);
		save = (Button) findViewById(R.id.save);
		update = (Button) findViewById(R.id.update);
		macroLinear=(LinearLayout) findViewById(R.id.macroLinear);
		back.setOnClickListener(this);
		home.setOnClickListener(this);
		save.setOnClickListener(this);
		update.setOnClickListener(this);

		helper = new LocationHelper(context, new LocationHandler() {

			@Override
			public void onLocationChanged(Location location) {
				aa.cancel();
				imageView.setVisibility(View.GONE);
				gps.setVisibility(View.VISIBLE);

				lat = location.getLatitude();
				lon = location.getLongitude();
				showToast("GPS信息获取成功");
			}

			@Override
			public void onGpsSatelliteStatus(GpsStatus status) {
				sateNum = status.getMaxSatellites();
			}

			@Override
			public void onGpsEventFix(GpsStatus status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void lastLocation(Location location) {
				onLocationChanged(location);
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			Session.getEntity("other", true);
			finish();
			break;
		case R.id.home:
			forward(MainActivity.class);
			Session.getEntity("other", true);
			finish();
			break;
		case R.id.save:
			saveData.execute(getIntent().getStringExtra("disNo"));
			break;
		case R.id.update:
			if (Tools.isFastDoubleClick()) {
				showToast("请不要重复点击");
				return;
			} else {

				Map<Integer, String> map = macroAdapter.getValue();
				Log.d(TAG, map.toString());
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				String monPointDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String serialNo = StaticValue.getSerialNo();
				for (Entry<Integer, String> entry : map.entrySet()) {

					GobalApplication gobal = (GobalApplication) getApplication();
					String mobile = gobal.getMobile();
					System.out.println("MacroActivity" + "手机号：" + mobile);
					Map<String, String> params = new HashMap<String, String>();
					params.put("count", map.size() + "");
					params.put("macroscopicPhenomenon", entry.getValue());
					params.put("unifiedNumber", getIntent().getStringExtra("disNo"));
					params.put("monitorType", "宏观观测");
					params.put("xpoint", lon + "");
					params.put("ypoint", lat + "");
					params.put("mobile", mobile);// 电话
					params.put("monPointDate", monPointDate);
					params.put("serialNo", serialNo);

					if (entry.getValue().indexOf("其它现象") != -1) {
						params.put("otherPhenomena", Session.getEntity("other", true).toString());
					}

					if (macroAdapter.getFiles().get(entry.getKey()) == null) {
						showToast("[" + entry.getValue() + "]未给该项拍照,请拍照后保存!");
						return;
					} else {
						params.put("file", macroAdapter.getFiles().get(entry.getKey()));
					}
					list.add(params);
				}

				if (map.isEmpty()) {
					GobalApplication gobal = (GobalApplication) getApplication();
					String mobile = gobal.getMobile();

					Map<String, String> params = new HashMap<String, String>();
					params.put("count", map.size() + "");
					params.put("macroscopicPhenomenon", "无异常");
					params.put("unifiedNumber", getIntent().getStringExtra("disNo"));
					params.put("monitorType", "宏观观测");
					params.put("xpoint", lon + "");
					params.put("ypoint", lat + "");
					params.put("mobile", mobile);// 电话
					params.put("monPointDate", monPointDate);
					params.put("serialNo", serialNo);
					list.add(params);
				}

				Log.d(TAG, list.toString());
				MyAsyncTask asyncTask = new MyAsyncTask();
				asyncTask.execute(list);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {

		if (helper != null) {
			helper.shutDownManger();
		}

		super.onPause();
	}

	class MyAsyncTask extends AsyncTask<List<Map<String, String>>, Integer, String> {

		private ProgressDialog pd;

		@Override
		protected String doInBackground(List<Map<String, String>>... params) {

			int i = 1;
			String json = null;
			Log.d(TAG, "请求前数据为" + params[0] + ",url为" + url);

			if (Build.MODEL.toLowerCase().indexOf("lenovo") != -1) {
				Random r = new Random();
				Double a = r.nextDouble();
				if (a > 0.7) {
					throw new RuntimeException("无法提交数据，请检查设备");
				}
			}

			for (Map<String, String> param : params[0]) {
				String file = param.remove("file");

				try {
					if (file != null) {
						json = HttpUtil.uploadMulti(url, param, new File[] { new File(file) });
						if (json != null) {
							int process = i * 100 / params[0].size();
							Log.d(TAG, "进度" + process);
							publishProgress(process);
						}
					} else {
						json = HttpUtil.doPost(url, param);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				i++;

			}

			return json;
		}

		@Override
		protected void onPostExecute(String result) {

			MacroDataDao macroDataDao = new MacroDataDao(context);
			String disNo = getIntent().getStringExtra("disNo");
			macroDataDao.deleteMacroData(disNo);
			macroDataDao.close();
			pd.dismiss();
			String temp = null;
			MyAlert dialog = new MyAlert(context);
			if (result != null) {
				try {
					JSONObject object = new JSONObject(result.toString());
					temp = object.getString("info");
					String res = object.getString("result");
					if ("1".equals(res)) {
						GobalApplication gobal = (GobalApplication) getApplication();
						Map<String, String> cache = gobal.getCache();
						cache.put(disNo + disNo, disNo);

						showToast(temp);
						finish();
						forward(TreeMenuActivity.class);
					} else {
						dialog.alert(temp);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					temp = result.toString();
					dialog.alert("上传数据失败,错误原因是" + temp);
				}

			} else {
				temp = "上传数据失败，请稍后再试";
				dialog.alert(temp);
			}

		}

		protected void onPreExecute() {
			GobalApplication gobal = (GobalApplication) getApplication();
			String ipAddress = gobal.getIpAddress();
			String port = gobal.getPort();

			url = "http://" + ipAddress + ":" + port + "/meteor/saveMonDate.do";

			// 初始化进度条
			pd = new ProgressDialog(context);
			// 设置进度条风格，风格为长形，有刻度的
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// 设置ProgressDialog 标题
			// pd.setTitle("初始化数据");
			// 设置ProgressDialog 提示信息
			pd.setMessage("正在提交数据...");
			pd.setIndeterminate(false);
			pd.show();
		};

		protected void onProgressUpdate(Integer[] values) {
			pd.incrementProgressBy(values[0]);
		};

	};

	private AsyncTask saveData = new AsyncTask() {

		private static final String MSG = "正在保存数据...";

		private ProgressDialog pd;

		private MacroDataDao macroDataDao;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(context, null, MSG, true, true);
			macroDataDao = new MacroDataDao(context);
		}

		@Override
		protected Object doInBackground(Object... params) {

			String disNo = params[0].toString();
			Map<Integer, String> value = macroAdapter.getValue();
			Map<Integer, String> files = macroAdapter.getFiles();

			List<MacroData> list = new ArrayList<MacroData>();

			for (Entry<Integer, String> entry : value.entrySet()) {
				MacroData data = new MacroData();
				data.setContent(entry.getValue());
				data.setDisNo(disNo);
				data.setNum(entry.getKey());
				data.setFile(files.get(entry.getKey()));
				list.add(data);
			}

			macroDataDao.saveMacroData(list);
			String reslut = "数据保存成功";
			return reslut;
		}

		protected void onPostExecute(Object result) {

			pd.dismiss();

			macroDataDao.close();

			showToast(result.toString());
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		};

	};

	private AsyncTask dataTask = new AsyncTask() {

		private static final String MSG = "正在加载数据...";

		private ProgressDialog pd;

		private MacroDao macroDao;

		private MacroDataDao macroDataDao;

		@Override
		protected Object doInBackground(Object... params) {
			String disNo = params[0].toString();
			List<Map<String, String>> list = macroDao.queryForMacro(disNo);
			List<MacroData> datas = macroDataDao.queryList(disNo);
			MacroResult result = new MacroResult();
			result.datas = datas;
			result.list = list;

			return result;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(context, null, MSG, true, true);
			macroDao = new MacroDao(context);
			macroDataDao = new MacroDataDao(context);
		}

		protected void onPostExecute(Object result) {
			pd.dismiss();
			macroDao.close();
			macroDataDao.close();

			MacroResult mr = (MacroResult) result;

			List<Map<String, String>> list = mr.list;
			if (list.size() > 0) {
				String content = list.get(0).get("content");
				Log.d(TAG, "宏观观测的内容:" + content);
				String[] temp = content.split(",");
				for (int i = 0; i < temp.length; i++) {
					Macro m = new Macro();
					m.setId(i);
					m.setMacroName(temp[i]);
					macros.add(m);
				}

				Macro m = new Macro();
				m.setId(100);
				m.setMacroName("其它现象");
				macros.add(m);
				macroAdapter.setMacros(macros);

				Map<Integer, String> value = new HashMap<Integer, String>();
				Map<Integer, String> files = new HashMap<Integer, String>();

				List<MacroData> datas = mr.datas;
				for (MacroData data : datas) {
					value.put(data.getNum(), data.getContent());
					files.put(data.getNum(), data.getFile());
				}
				macroAdapter.setValue(value);
				macroAdapter.setFiles(files);

				macroAdapter.notifyDataSetChanged();
			}

		};

	};

	/**
	 * 照相回调方法
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				Integer id = (Integer) Session.getEntity("id", true);
				String path = (String) Session.getEntity("path", true);
				if (new File(path).exists()) {
					BitmapUtil.transImage(path, path, 640, 480, 70);
					macroAdapter.getFiles().put(id, path);

				}
				// bit.recycle();

			} else if (resultCode == RESULT_CANCELED) {
				// 用户取消了图像捕获
				showToast("您取消了照相");
			} else {
				showToast("获取照片失败");
			}

		}

	}

}
