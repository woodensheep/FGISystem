package com.fubangty.FGISystem.audiovideochat.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.adapter.RoleListAdapter;
import com.fubangty.FGISystem.audiovideochat.adapter.SpinnerAdapter;
import com.fubangty.FGISystem.audiovideochat.common.Constants;
import com.fubangty.FGISystem.audiovideochat.config.ConfigEntity;
import com.fubangty.FGISystem.audiovideochat.config.ConfigService;
import com.fubangty.FGISystem.audiovideochat.config.VideoConfig;
import com.fubangty.FGISystem.audiovideochat.entity.Message;
import com.fubangty.FGISystem.audiovideochat.entity.RoleInfo;
import com.fubangty.FGISystem.audiovideochat.global.Global;
import com.fubangty.FGISystem.audiovideochat.http.CallHttpBiz;
import com.fubangty.FGISystem.audiovideochat.http.ConnectionUrl;
import com.fubangty.FGISystem.audiovideochat.http.MyCallback;
import com.fubangty.FGISystem.audiovideochat.service.MyService;
import com.fubangty.FGISystem.audiovideochat.service.VideoSpeakService;
import com.fubangty.FGISystem.audiovideochat.utils.ServiceUtil;
import com.fubangty.FGISystem.audiovideochat.utils.ValueUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

/**
 * <font color="green">语音会话界面</font>
 * 
 * @ClassName SpeakLoginActivity
 * @author Administrator
 * @date 2016年7月4日 上午11:12:41
 *
 * @version
 */
public class SpeakLoginActivity extends Activity implements AnyChatBaseEvent, MyCallback {

	// 视频配置界面标识
	public static final int ACTIVITY_ID_VIDEOCONFIG = 1;

	private ListView mRoleList;
	private EditText mEditIP;
	private EditText mEditPort;
	private EditText mEditName;
	private EditText mEditRoomID;
	private TextView mBottomConnMsg;
	private Button mBtnStart;
	private Button mBtnLogout;
	private Button mBtnWaiting;
	private LinearLayout mWaitingLayout;
	private LinearLayout mProgressLayout;

	private String mStrIP = "demo.anychat.cn";
	private String mStrName = "name";
	private int mSPort = 8906;
	private int mSRoomID = 1037;

	private final int SHOWLOGINSTATEFLAG = 1; // 显示的按钮是登陆状态的标识
	private final int SHOWWAITINGSTATEFLAG = 2; // 显示的按钮是等待状态的标识
	private final int SHOWLOGOUTSTATEFLAG = 3; // 显示的按钮是登出状态的标识
	private final int LOCALVIDEOAUTOROTATION = 1; // 本地视频自动旋转控制

	private List<RoleInfo> mRoleInfoList = new ArrayList<RoleInfo>();
	private RoleListAdapter mAdapter;
	private int UserselfID;

	public AnyChatCoreSDK anyChatSDK;
	private static SpeakLoginActivity instance;
	private Spinner spRoom;
	SharedPreferences prefer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_speak_login);
		// 启动轮询服务，接受服务器视频推送
		if (!ServiceUtil.isServiceRunning(getApplicationContext(), Constants.POI_SERVICE)) {
			ServiceUtil.invokeTimerPOIService(getApplicationContext());
		}
		if (ServiceUtil.isServiceRunning(getApplicationContext(), Constants.POI_SERVICE)) {
			Intent i = new Intent(this, VideoSpeakService.class);
			stopService(i);
		}

		InitSelf();
		InitSDK();
		readLoginDate();
		InitLayout();
		// initLoginConfig();
		initWaitingTips();
		ApplyVideoConfig();
	}

	private void InitSelf() {
		synchronized (this) {
			if (instance == null) {
				instance = this;
			}
		}
	}

	public static SpeakLoginActivity getInstance() {
		if (instance != null) {
			return instance;
		}
		return null;
	}

	private void InitSDK() {
		if (anyChatSDK == null) {
			anyChatSDK = AnyChatCoreSDK.getInstance(this);
			anyChatSDK.SetBaseEvent(this);
			anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, LOCALVIDEOAUTOROTATION);
		}
	}

	private void InitLayout() {
		mRoleList = (ListView) this.findViewById(R.id.roleListView);
		mBtnStart = (Button) this.findViewById(R.id.mainUIStartBtn);
		mBottomConnMsg = (TextView) this.findViewById(R.id.mainUIbottomConnMsg);
		mBtnLogout = (Button) this.findViewById(R.id.mainUILogoutBtn);
		mBtnWaiting = (Button) this.findViewById(R.id.mainUIWaitingBtn);
		mWaitingLayout = (LinearLayout) this.findViewById(R.id.waitingLayout);
		spRoom = (Spinner) findViewById(R.id.sp_room);
		if (MyService.list != null) {
			SpinnerAdapter adapter = new SpinnerAdapter(this, MyService.list);
			spRoom.setAdapter(adapter);
		}
		mRoleList.setDivider(null);
		spRoom.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Message msg = (Message) arg0.getItemAtPosition(arg2);
				mSRoomID = msg.getRoom_id();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		mBtnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setBtnVisible(SHOWWAITINGSTATEFLAG);
				anyChatSDK.Connect(mStrIP, mSPort);
				anyChatSDK.Login(mStrName, "");
			}
		});

		mBtnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setBtnVisible(SHOWLOGINSTATEFLAG);
				anyChatSDK.LeaveRoom(-1);
				anyChatSDK.Logout();
				mRoleList.setAdapter(null);
				mBottomConnMsg.setText("未连接到服务器....");
				Intent mIntent = new Intent(Global.stop_service);
				// 发送广播
				sendBroadcast(mIntent);
			}
		});
	}

	private void initLoginConfig() {
		mEditIP.setText(mStrIP);
		mEditName.setText(mStrName);
		mEditPort.setText(String.valueOf(mSPort));
		mEditRoomID.setText(String.valueOf(mSRoomID));
	}

	// 读取登陆数据
	private void readLoginDate() {
		SharedPreferences preferences = getSharedPreferences("config", 0);
		// mStrIP = preferences.getString("UserIP", "demo.anychat.cn");
		// mSPort = preferences.getInt("UserPort", 8906);
		mStrIP = ConnectionUrl.IP;
		mSPort = Integer.valueOf(ConnectionUrl.PORT);
		mStrName = preferences.getString("phoneNum", "");
		// mSRoomID = preferences.getInt("UserRoomID", 1037);
	}

	// 保存登陆相关数据
	private void saveLoginData() {
		SharedPreferences preferences = getSharedPreferences("SpeakLoginInfo", 0);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putString("UserIP", mStrIP);
		preferencesEditor.putInt("UserPort", mSPort);
		preferencesEditor.putInt("UserRoomID", mSRoomID);
		preferencesEditor.commit();
	}

	private boolean checkInputData() {
		String ip = mEditIP.getText().toString().trim();
		String port = mEditPort.getText().toString().trim();
		if (ValueUtils.isStrEmpty(ip)) {
			mBottomConnMsg.setText("请输入IP");
			return false;
		} else if (ValueUtils.isStrEmpty(port)) {
			mBottomConnMsg.setText("请输入端口号");
			return false;
		} else {
			return true;
		}
	}

	// 控制登陆，等待和登出按钮状态
	private void setBtnVisible(int index) {
		if (index == SHOWLOGINSTATEFLAG) {
			mBtnStart.setVisibility(View.VISIBLE);
			mBtnLogout.setVisibility(View.GONE);
			mBtnWaiting.setVisibility(View.GONE);

			mProgressLayout.setVisibility(View.GONE);
		} else if (index == SHOWWAITINGSTATEFLAG) {
			mBtnStart.setVisibility(View.GONE);
			mBtnLogout.setVisibility(View.GONE);
			mBtnWaiting.setVisibility(View.VISIBLE);

			mProgressLayout.setVisibility(View.VISIBLE);
		} else if (index == SHOWLOGOUTSTATEFLAG) {
			mBtnStart.setVisibility(View.GONE);
			mBtnLogout.setVisibility(View.VISIBLE);
			mBtnWaiting.setVisibility(View.GONE);

			mProgressLayout.setVisibility(View.GONE);
		}
	}

	// init登陆等待状态UI
	private void initWaitingTips() {
		if (mProgressLayout == null) {
			mProgressLayout = new LinearLayout(this);
			mProgressLayout.setOrientation(LinearLayout.HORIZONTAL);
			mProgressLayout.setGravity(Gravity.CENTER_VERTICAL);
			mProgressLayout.setPadding(1, 1, 1, 1);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 5, 5, 5);
			ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
			mProgressLayout.addView(progressBar, params);
			mProgressLayout.setVisibility(View.GONE);
			mWaitingLayout.addView(mProgressLayout, new LayoutParams(params));
		}
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		anyChatSDK.LeaveRoom(-1);
		anyChatSDK.Logout();
		anyChatSDK.Release();
		if (instance != null) {
			instance = null;
		}
	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		anyChatSDK.SetBaseEvent(this);

		// 一种简便的方法，当断网的时候，返回到登录界面，不去刷新用户列表，下面广播已经清空了列表
		if (mBtnStart.getVisibility() != View.VISIBLE)
			updateUserList();
	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		if (!bSuccess) {
			setBtnVisible(SHOWLOGINSTATEFLAG);
			mBottomConnMsg.setText("连接服务器失败，自动重连，请稍后...");
		}
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			saveLoginData();
			setBtnVisible(SHOWLOGOUTSTATEFLAG);
			// hideKeyboard();

			mBottomConnMsg.setText("Connect to the server success.");
			int sHourseID = mSRoomID;
			anyChatSDK.EnterRoom(sHourseID, "");

			UserselfID = dwUserId;

		} else {
			setBtnVisible(SHOWLOGINSTATEFLAG);
			mBottomConnMsg.setText("登录失败，errorCode：" + dwErrorCode);
		}
	}

	public boolean isonline;

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			// "房间进入成功......"
			isonline = true;
		}
		System.out.println("OnAnyChatEnterRoomMessage" + dwRoomId + "err:" + dwErrorCode);
	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		mBottomConnMsg.setText("进入房间成功！");
		updateUserList();
	}

	private void updateUserList() {
		mRoleInfoList.clear();
		int[] userID = anyChatSDK.GetOnlineUser();
		RoleInfo userselfInfo = new RoleInfo();
		userselfInfo.setName(anyChatSDK.GetUserName(UserselfID) + "(自己) 【点击可设置】");
		userselfInfo.setUserID(String.valueOf(UserselfID));
		userselfInfo.setRoleIconID(getRoleRandomIconID());
		mRoleInfoList.add(userselfInfo);

		for (int index = 0; index < userID.length; ++index) {
			RoleInfo info = new RoleInfo();
			info.setName(anyChatSDK.GetUserName(userID[index]));
			info.setUserID(String.valueOf(userID[index]));
			info.setRoleIconID(getRoleRandomIconID());
			mRoleInfoList.add(info);
		}

		mAdapter = new RoleListAdapter(SpeakLoginActivity.this, mRoleInfoList);
		mRoleList.setAdapter(mAdapter);
		mRoleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {
					Intent intent = new Intent();
					intent.setClass(SpeakLoginActivity.this, VideoConfig.class);
					startActivityForResult(intent, ACTIVITY_ID_VIDEOCONFIG);
					return;
				}

				onSelectItem(arg2);
			}
		});
	}

	private void onSelectItem(int postion) {
		String strUserID = mRoleInfoList.get(postion).getUserID();

		Intent intent = new Intent();
		intent.putExtra("UserID", strUserID);
		intent.setClass(this, SpeakActivity.class);
		startActivity(intent);

	}

	private int getRoleRandomIconID() {
		int number = new Random().nextInt(5) + 1;
		if (number == 1) {
			return R.drawable.role_1;
		} else if (number == 2) {
			return R.drawable.role_2;
		} else if (number == 3) {
			return R.drawable.role_3;
		} else if (number == 4) {
			return R.drawable.role_4;
		} else if (number == 5) {
			return R.drawable.role_5;
		}

		return R.drawable.role_1;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK && requestCode == ACTIVITY_ID_VIDEOCONFIG) {
			ApplyVideoConfig();
		}
	}

	// 根据配置文件配置视频参数
	private void ApplyVideoConfig() {
		ConfigEntity configEntity = ConfigService.LoadConfig(this);
		if (configEntity.mConfigMode == 1) // 自定义视频参数配置
		{
			// 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, configEntity.mVideoBitrate);
			// if (configEntity.mVideoBitrate == 0) {
			// 设置本地视频编码的质量
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL, configEntity.mVideoQuality);
			// }
			// 设置本地视频编码的帧率
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL, configEntity.mVideoFps);
			// 设置本地视频编码的关键帧间隔
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL, configEntity.mVideoFps * 4);
			// 设置本地视频采集分辨率
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL, configEntity.mResolutionWidth);
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL, configEntity.mResolutionHeight);
			// 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
			AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL, configEntity.mVideoPreset);
		}
		// 让视频参数生效
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM, configEntity.mConfigMode);
		// P2P设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC, configEntity.mEnableP2P);
		// 本地视频Overlay模式设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY, configEntity.mVideoOverlay);
		// 回音消除设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL, configEntity.mEnableAEC);
		// 平台硬件编码设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC, configEntity.mUseHWCodec);
		// 视频旋转模式设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL, configEntity.mVideoRotateMode);
		// 本地视频采集偏色修正设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA, configEntity.mFixColorDeviation);
		// 视频GPU渲染设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
				configEntity.mVideoShowGPURender);
		// 本地视频自动旋转设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, configEntity.mVideoAutoRotation);
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		if (bEnter) {
			RoleInfo info = new RoleInfo();
			info.setUserID(String.valueOf(dwUserId));
			info.setName(anyChatSDK.GetUserName(dwUserId));
			info.setRoleIconID(getRoleRandomIconID());
			mRoleInfoList.add(info);
			mAdapter.notifyDataSetChanged();
		} else {

			for (int i = 0; i < mRoleInfoList.size(); i++) {
				if (mRoleInfoList.get(i).getUserID().equals("" + dwUserId)) {
					mRoleInfoList.remove(i);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		setBtnVisible(SHOWLOGINSTATEFLAG);
		mRoleList.setAdapter(null);
		anyChatSDK.LeaveRoom(-1);
		anyChatSDK.Logout();
		mBottomConnMsg.setText("连接关闭，errorCode：" + dwErrorCode);
	}

	// 广播
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("SpeakActivity")) {
				Toast.makeText(SpeakLoginActivity.this, "网络已断开！", Toast.LENGTH_SHORT).show();
				setBtnVisible(SHOWLOGINSTATEFLAG);
				mRoleList.setAdapter(null);
				mBottomConnMsg.setText("未连接到服务器...");
				anyChatSDK.LeaveRoom(-1);
				anyChatSDK.Logout();
			}
		}
	};

	// 来自推送消息的广播
	private String id;
	private BroadcastReceiver XX = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Global.speak_receiver)) {
				// 判断获取的UserID处于激活状态
				String uid = intent.getStringExtra("UserID");
				id = intent.getStringExtra("id");
				Log.e("tag", "接收到广播...");

				// 发送消息到服务器，通知已点击消息(传id值)
				doRequest();

				int[] userID = anyChatSDK.GetRoomOnlineUsers(mSRoomID);
				String[] sId = new String[userID.length];
				boolean isUserOnline = false;
				for (int index = 0; index < userID.length; ++index) {
					sId[index] = String.valueOf(userID[index]);
				}
				for (int index = 0; index < sId.length; ++index) {
					if (uid.equals(sId[index])) {// 邀请id在线
						isUserOnline = true;
					}
				}

				if (isUserOnline) {
					Intent toSpeak = new Intent(SpeakLoginActivity.this, SpeakActivity.class);
					toSpeak.putExtra("UserID", intent.getStringExtra("UserID"));
					startActivity(toSpeak);

				} else {
					Toast.makeText(SpeakLoginActivity.this, "会话邀请已过期", Toast.LENGTH_LONG).show();
				}

			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("SpeakActivity");
		registerReceiver(mBroadcastReceiver, myIntentFilter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Global.speak_receiver);
		registerReceiver(XX, filter);
	}

	protected void doRequest() {
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id);
		System.out.println("cancleid:" + id);
		// call.PostData(1, ConnectionUrl.URL_CANCLE, params, this);
		call.PostData(1, new ConnectionUrl().URL_CANCLE, params, this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onDestroy();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void getJson(int type, String data) {

	}

	@Override
	public void onFailure(HttpException arg0, String arg1) {

	}
}
