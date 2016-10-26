package com.fubangty.FGISystem.deffend.fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.http.ConnectionUrl;
import com.fubangty.FGISystem.deffend.util.PhotoUntils;
import com.fubangty.FGISystem.deffend.util.ProcessUtils;
import com.fubangty.FGISystem.deffend.util.ToastUtils;
import com.fubangty.FGISystem.deffend.views.ImgDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 灾情速报
 * 
 * 文本与图片分开上传，2个接口
 * 先传文本，再传图片。传图片时，同时传phoneNum,phoneID,时间,服务器那边去关联文本数据库
 * 
 * @author lemon
 *
 */
@SuppressLint("InflateParams")
public class SituationFragment extends Fragment implements OnClickListener {
	private int refer;
	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_CAMER = 2;
	private boolean isShowDelete = false;// 是否显示删除图标
	private File out;// 图片保存的文件路径
	private MyAdapter ImgAdapter;
	private List<Bitmap> imgList = new ArrayList<Bitmap>();// 照片信息
	@ViewInject(value = R.id.et_disaster_recorder)
	private EditText etRecorder;// 记录人
	@ViewInject(value = R.id.tv_disaster_time)
	private TextView tvTime;// 显示时间
	@ViewInject(value = R.id.gv_photo)
	private GridView gvPhoto;// 显示照片
	@ViewInject(value = R.id.et_disaster_township_town)
	private EditText etPlaceTown;// 输入地点 乡镇
	@ViewInject(value = R.id.et_disaster_village)
	private EditText etPlaceVillage;// 输入地点 村
	@ViewInject(value = R.id.et_disaster_place)
	private EditText etPlaceGroup;// 输入地点 组
	@ViewInject(value = R.id.et_disaster_number)
	private EditText etDisasterNum;// 输入灾情 受灾人数
	@ViewInject(value = R.id.et_disaster_death_number)
	private EditText etDisasterDeathNum;// 输入灾情 死亡人数
	@ViewInject(value = R.id.et_disaster_miss_number)
	private EditText etDisasterMissNum;// 输入灾情 失踪人数
	@ViewInject(value = R.id.et_disaster_injuried_number)
	private EditText etDisasterInjuriedNum;// 输入灾情 受伤人数
	@ViewInject(value = R.id.et_disaster_family)
	private EditText etDangerFamily;// 输入险情 潜在威胁户数
	@ViewInject(value = R.id.et_disaster_person)
	private EditText etDangerPerson;// 输入险情 潜在危险人数
	@ViewInject(value = R.id.et_disaster_remarks)
	private EditText etRemarks;// 输入备注
	@ViewInject(value = R.id.btn_disaster_report)
	private Button btnSubmit;// 提交
	private Map<String, String> map;// 文本信息
	private DatePicker datePicker;// 日期控件
	private TimePicker timePicker;// 时间控件
	private AlertDialog dialog;// 选择时间日期对话框
	private String phoneNum;
	private String phoneID;
	private String Time;
	private int upnum;
	
	private Context context;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_situation, null);
		ViewUtils.inject(this, v);
		context=getActivity();
		setPhoto();// 拍照相关方法
		setlistener();// 设置监听器
		return v;
	}

	private void setPhoto() {
		ImgAdapter = new MyAdapter();
		ImgAdapter.setIsShowDelete(isShowDelete);
		gvPhoto.setAdapter(ImgAdapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			Cursor cursor;
			switch (requestCode) {
			// 从相册选择
			case SELECT_PICTURE:
				Uri vUri = data.getData();
				Log.d("limeng", "vUri:"+vUri.toString());
				// 将图片内容解析成字节数组
				String[] proj = { MediaStore.Images.Media.DATA };
//				 cursor = getActivity().managedQuery(vUri, proj, null, null, null);
				cursor=getActivity().getContentResolver().query(vUri, proj,null, null, null);
				int column_index = cursor.getColumnIndex(proj[0]);
				Log.d("limeng", "column_index:"+column_index);
				cursor.moveToFirst();
				String path1 = cursor.getString(column_index);
				cursor.close();
				cursor=null;
				Log.d("limeng", "path1:"+path1);
				Bitmap bm = PhotoUntils.getxtsldraw(getActivity(), path1);
				if (null != bm && !"".equals(bm)) {
					imgList.add(bm);
				}
				ImgAdapter.notifyDataSetChanged();
				break;
			// 拍照添加图片
			case SELECT_CAMER:
				Bitmap bm1 = PhotoUntils.getxtsldraw(getActivity(), out.getAbsolutePath());
				if (null != bm1 && !"".equals(bm1)) {
					bm1=compressImage(bm1);
					imgList.add(bm1);
				}
				ImgAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

		}
	}

	private void setlistener() {
		tvTime.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		// 单击item的响应事件
		gvPhoto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isShowDelete == true) {
					// 如果处于正在删除的状态，单击则删除图标消失
					isShowDelete = false;
					ImgAdapter.setIsShowDelete(isShowDelete);
				} else {
					// 处于正常状态
					if (position == imgList.size() && imgList.size() < 5) {
						// 添加图片
						selectimg();
					}
					if (position < imgList.size()) {
						// 放大图片
						showPhoto(imgList.get(position));
					}
				}

				ImgAdapter.notifyDataSetChanged();
			}
		});

		// 长按item的响应事件 删除图片
		gvPhoto.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// 长按显示删除图标
				if (isShowDelete == false) {
					isShowDelete = true;
				}
				ImgAdapter.setIsShowDelete(isShowDelete);
				return true;
			}
		});
	}

	/**
	 * 点击照片放大
	 * 
	 * @param bitmap
	 */
	protected void showPhoto(Bitmap bitmap) {
		ImgDialog.Builder builder = new ImgDialog.Builder(getActivity());
		builder.setImage(bitmap);
		ImgDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_disaster_time:
			showDialog();// 显示对话框获取时间
			break;
		case R.id.btn_disaster_report:
			map = getMaps();
			if(isEditText()){
				Toast.makeText(getActivity(), "请输入完整信息", Toast.LENGTH_LONG).show();
			}else{
				sendText(getActivity());
			}
		}
	}

	private boolean isEditText() {
		if (etDangerFamily.getText().toString().trim().length() == 0|
			etDangerPerson.getText().toString().trim().length() == 0|	
			tvTime.getText().toString().trim().length() == 0|	
			etDisasterDeathNum.getText().toString().trim().length() == 0|	
			etDisasterInjuriedNum.getText().toString().trim().length() == 0|	
			etDisasterMissNum.getText().toString().trim().length() == 0|	
			etDisasterNum.getText().toString().trim().length() == 0|	
			etPlaceGroup.getText().toString().trim().length() == 0|	
			etPlaceTown.getText().toString().trim().length() == 0|	
			etPlaceVillage.getText().toString().trim().length() == 0|	
			etRecorder.getText().toString().trim().length() == 0|	
			etRemarks.getText().toString().trim().length() == 0|imgList.size()==0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Recorder ：记录人 Time ：时间 Town ：乡/镇 Village ：村 Group ：组 DisasterNum ：受灾人数
	 * DeathNum ：死亡人数 MissNum ：失踪人数 InjuriedNum ：受伤人数 DangerFamily ：潜在威胁：户
	 * DangerPerson ：人 Remarks ：备注
	 * 
	 */
	private void sendText(final Context context) {
		ProcessUtils.showProcess(context, "数据上传中......");
		// 上传地址
		String url = new ConnectionUrl().UPSITUATIONTEXT;
		SharedPreferences shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		 phoneNum=shared.getString("phoneNum", 0+"");
		 phoneID=shared.getString("phoneID", 0+"");
		 Log.d("limeng", "1223---"+phoneNum+phoneID);
		// 发请求上传数据
		RequestParams params = new RequestParams();
		params.addBodyParameter("phoneNum", phoneNum);
		params.addBodyParameter("phoneID", phoneID);
		params.addBodyParameter("userName", map.get("Recorder"));
		params.addBodyParameter("happenTime", map.get("Time"));
		params.addBodyParameter("township", map.get("Town"));
		params.addBodyParameter("village", map.get("Village"));
		params.addBodyParameter("group", map.get("Group"));
		params.addBodyParameter("disasterNum", map.get("DisasterNum"));
		params.addBodyParameter("dieNum", map.get("DeathNum"));
		params.addBodyParameter("missingNum", map.get("MissNum"));
		params.addBodyParameter("injuredNum", map.get("InjuriedNum"));
		params.addBodyParameter("houseNum", map.get("DangerFamily"));
		params.addBodyParameter("peopleNum", map.get("DangerPerson"));
		params.addBodyParameter("notes", map.get("Remarks"));
		final HttpUtils httpUtils = new HttpUtils();

		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ProcessUtils.closeProcess();
				Toast.makeText(getActivity(), "文本数据上传失败!!!", 1).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String json = responseInfo.result;
				
				try {
					JSONObject obj = new JSONObject(json);
					Log.d("limeng", json.toString());
					if (obj.getString("status").equals("200")) {
						// recordId = obj.getInt("recordId") + "";
						upnum=0;
						sendImg(httpUtils, context);
					} else {
						ProcessUtils.closeProcess();
						Toast.makeText(getActivity(),obj.getString("message"), 0).show();
						//Toast.makeText(context, "else文本数据上传失败....", 0).show();
					}

				} catch (Exception e) {
					ProcessUtils.closeProcess();
					Toast.makeText(context, "文本数据上传失败...." + e.toString(), 0).show();
				}

			}

		});
	}

	// 上传图片
	public void sendImg(final HttpUtils httpUtils, final Context context) {
		//ProcessUtils.showProcess(context, "上传图片中...");
		if (imgList.size()==0) {
			ProcessUtils.closeProcess();
			return ;
		}
		
			Log.d("limeng", "imgList.size():"+imgList.size()+"upnum="+upnum);
			refer = upnum;
			Bitmap bitmap = imgList.get(upnum);
			ByteArrayOutputStream ops = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, ops);
			byte[] bs = ops.toByteArray();
			ByteArrayInputStream ips = new ByteArrayInputStream(bs);
			String filename = UUID.randomUUID().toString() + ".png";

			RequestParams params = new RequestParams();
			params.addBodyParameter("phoneNum", phoneNum);
			params.addBodyParameter("phoneID", phoneID);
			params.addBodyParameter("happenTime", map.get("Time"));
			params.addBodyParameter("upload", ips, bs.length, filename, "image/png");

			httpUtils.send(HttpMethod.POST, new ConnectionUrl().UPSITUATIONIMAGE, params,
					new RequestCallBack<String>() {

						// 在主线程中执行的回调方法
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String json = responseInfo.result;
							
								try {
									JSONObject obj = new JSONObject(json);
									if (obj.getString("status").equals("200")) {
										Log.d("limeng", "obj.toString():"+obj.toString());
										if (refer == imgList.size() - 1) {
											ProcessUtils.closeProcess();
											clearAll();
											Runtime runtime = Runtime.getRuntime();
										      try {
										        
										        runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
										        
										      } catch (IOException e) {
										      }
										      ToastUtils.setToast(context, "上传成功！！！", Toast.LENGTH_LONG);
										}else if(upnum<imgList.size()-1){
											upnum++;
											sendImg(httpUtils, context);
										}
									} else {
										ProcessUtils.closeProcess();
										Toast.makeText(context, "上传失败！请检查网络配置或服务器", 0).show();
									}
								} catch (Exception e) {
									ProcessUtils.closeProcess();
									Toast.makeText(context, "上传失败！请检查网络配置或服务器" + e.toString(), 0).show();
								}
							
						}

						@Override
						public void onFailure(HttpException e, String msg) {
							ProcessUtils.closeProcess();
							Toast.makeText(context, "上传失败！请检查网络配置或服务器" + e.toString(), 1).show();
						}

					});

		
	}

	
	
	/**
	 * 清除editView
	 */
	private  void clearAll() {
		etDangerFamily.setText("");
		etDangerPerson.setText("");
		etDisasterDeathNum.setText("");
		etDisasterInjuriedNum.setText("");
		etDisasterMissNum.setText("");
		etDisasterNum.setText("");
		etPlaceGroup.setText("");
		etPlaceTown.setText("");
		etPlaceVillage.setText("");
		etRecorder.setText("");
		etRemarks.setText("");
		tvTime.setText("");
		imgList.clear();
		ImgAdapter.notifyDataSetChanged();
	}

	/**
	 * 获取灾情详细信息
	 * 
	 * @return map
	 */
	private Map<String, String> getMaps() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Recorder", etRecorder.getText().toString().trim());
		map.put("Time", tvTime.getText().toString().trim());
		map.put("Town", etPlaceTown.getText().toString().trim());
		map.put("Village", etPlaceVillage.getText().toString().trim());
		map.put("Group", etPlaceGroup.getText().toString().trim());
		map.put("DisasterNum", etDisasterNum.getText().toString().trim());
		map.put("DeathNum", etDisasterDeathNum.getText().toString().trim());
		map.put("MissNum", etDisasterMissNum.getText().toString().trim());
		map.put("InjuriedNum", etDisasterInjuriedNum.getText().toString().trim());
		map.put("DangerFamily", etDangerFamily.getText().toString().trim());
		map.put("DangerPerson", etDangerPerson.getText().toString().trim());
		map.put("Remarks", etRemarks.getText().toString().trim());
		return map;
	}

	/**
	 * 选择图片来源
	 */
	private void selectimg() {
		final CharSequence[] items = { "拍照上传", "从相册选择" };
		new AlertDialog.Builder(getActivity()).setTitle("选择图片来源")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == SELECT_PICTURE) {
							toGetLocalImage();
						} else {
							toGetCameraImage();
						}
					}
				}).create().show();
	}

	/**
	 * 选择本地图片
	 */
	public void toGetLocalImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(intent, SELECT_PICTURE);
	}

	// 获取当前时间
		private String getSystemTime() {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			String date = sDateFormat.format(new Date(System.currentTimeMillis()));
			return date;
		}
	
	/**
	 * 照相选择图片
	 */
	private int i=0;
	public void toGetCameraImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		String photoname = getSystemTime()+"as.jpg";
		i++;
		out = new File(getSDPath(), photoname);
		Uri uri = Uri.fromFile(out);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, SELECT_CAMER);
		// finish();
	}

	/**
	 * 获取sd卡路径
	 * 
	 * @return
	 */
	private File getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			// 这里可以修改为你的路径
			sdDir = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
		}
		return sdDir;
	}

	/**
	 * 显示获取时间的对话框
	 */
	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_time, null);
		datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
		timePicker = (TimePicker) view.findViewById(R.id.timePicker1);
		builder.setTitle("设置时间");
		builder.setView(view);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int day = datePicker.getDayOfMonth();
				int month = datePicker.getMonth()+1;
				int year = datePicker.getYear();
				int hour = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute();
				String month1=month+"";
				String day1=day+"";
				String hour1=hour+"";
				String minute1=minute+"";
				if(month<=9){
					month1=0+month1;
				}
				if(day<=9){
					day1=0+day1;
				}
				if(hour<=9){
					hour1=0+hour1;
				}
				if(minute<=9){
					minute1=0+minute1;
				}
				tvTime.setText(year + "-" + month1 + "-" + day1 + " " + hour1 + ":" + minute1 + ":"+"00");
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * 用于gridview显示多张照片
	 * 
	 * @author wlc
	 * @date 2015-4-16
	 */
	public class MyAdapter extends BaseAdapter {

		private boolean isDelete; // 用于删除图标的显隐
		private LayoutInflater inflater = LayoutInflater.from(getActivity());

		@Override
		public int getCount() {

			// 需要额外多出一个用于添加图片
			return imgList.size() + 1;

		}

		@Override
		public Object getItem(int arg0) {
			return imgList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {

			// 初始化页面和相关控件
			convertView = inflater.inflate(R.layout.item_imgview, null);
			ImageView img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
			LinearLayout ll_picparent = (LinearLayout) convertView.findViewById(R.id.ll_picparent);
			ImageView delete = (ImageView) convertView.findViewById(R.id.img_delete);

			// 默认的添加图片的那个item是不需要显示删除图片的
			if (imgList.size() >= 1) {
				if (position <= imgList.size() - 1) {
					ll_picparent.setVisibility(View.GONE);
					img_pic.setVisibility(View.VISIBLE);
					img_pic.setImageBitmap(imgList.get(position));
					// 设置删除按钮是否显示
					delete.setVisibility(isDelete ? View.VISIBLE : View.GONE);
				}
			}
			if (imgList.size() >= 5) {
				ll_picparent.setVisibility(View.GONE);
			}

			// 当处于删除状态时，删除事件可用
			// 注意：必须放到getView这个方法中，放到onitemClick中是不起作用的。
			if (isDelete) {
				delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						imgList.remove(position);
						ImgAdapter.notifyDataSetChanged();

					}
				});
			}

			return convertView;
		}

		/**
		 * 设置是否显示删除图片
		 * 
		 * @param isShowDelete
		 */
		public void setIsShowDelete(boolean isShowDelete) {
			this.isDelete = isShowDelete;
			notifyDataSetChanged();
		}

	}
	
	private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

	@Override
	public void onDestroy() {
		imgList=null;
		ImgAdapter.notifyDataSetChanged();
		super.onDestroy();
	}
}
