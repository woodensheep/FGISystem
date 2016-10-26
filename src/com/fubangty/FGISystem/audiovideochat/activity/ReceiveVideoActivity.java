package com.fubangty.FGISystem.audiovideochat.activity;

import java.util.List;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.entity.Message;
import com.fubangty.FGISystem.audiovideochat.service.MyService;
import com.fubangty.FGISystem.audiovideochat.service.VideoSpeakService;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ReceiveVideoActivity extends Activity implements OnClickListener {
	private Button btnAccept;
	private Button btnReject;
	private TextView tvMsg;
	private Intent intent;
	private List<Message> list;
	private String id;
	private String push_userid;
	private int room_id;
	private String invite_man;
	private String push_msg;
	private MediaPlayer player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive_video);
		wakeUpAndUnlock();
		play();
		initView();
		System.out.println("进入提醒页面。。。。。。。。。");
	}

	private void play() {
		player=MediaPlayer.create(this, R.raw.alarm);
		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				player.start();
				player.setLooping(true);
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(player.isPlaying()){
			player.stop();
		}
		player.release();
	}
	private void initView() {
		btnAccept = (Button) findViewById(R.id.btn_accept);
		btnReject = (Button) findViewById(R.id.btn_reject);
		tvMsg=(TextView) findViewById(R.id.tv_msg);
		btnAccept.setOnClickListener(this);
		btnReject.setOnClickListener(this);
		intent = getIntent();
		list = (List<Message>) intent.getSerializableExtra("MsgList");
		if(list!=null){
			id = list.get(0).getId()+"";
			push_msg = list.get(0).getPush_msg();
			push_userid = list.get(0).getInvite_userId()+"";
			room_id = list.get(0).getRoom_id();
			invite_man = list.get(0).getInvite_man();
			tvMsg.setText(push_msg);
		}
	}
	public void wakeUpAndUnlock(){  
        KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");  
        //解锁  
        kl.disableKeyguard();  
        //获取电源管理器对象  
        PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);  
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag  
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");  
        //点亮屏幕  
        wl.acquire();  
        //释放  
        wl.release();  
    } 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_accept:
			Log.d("点击接听", list.toString());
			if(list.get(0).getPush_type()==1){
				Intent i = new Intent(this, VideoSpeakService.class);
				i.putExtra("id", id);
				i.putExtra("UserID", push_userid);
				i.putExtra("roomID", room_id);
				i.putExtra("inviteMan", invite_man);
				Log.e("视频接听", "进入通话界面。。。。。。。");
				startService(i);
			}else{
				Intent i = new Intent(this, VideoSpeakService.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("id", id);
				i.putExtra("UserID", push_userid);
				i.putExtra("roomID", room_id);
				Log.e("语音接听", "进入通话界面。。。。。。。");
				startService(i);
			}
			finish();
			System.out.println("点击了接听。。。。。。。。。。。。。。。。");
			break;
		case R.id.btn_reject:
			finish();
			break;
		}
	}

}
