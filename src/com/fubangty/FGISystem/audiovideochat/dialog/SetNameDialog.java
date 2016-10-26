package com.fubangty.FGISystem.audiovideochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.fubangty.FGISystem.R;

/**
 * <font color="green">用户输入真实姓名弹出框</font>
 * @ClassName SetName
 * @author 樊艳红
 * @date 2016年4月8日 上午10:52:47
 *
 * @version
 */
public class SetNameDialog extends Dialog {

	private Context context;
	public EditText  etPhone,etCode;
	public Button sure,getcode; 
	private android.view.View.OnClickListener onClickListener;
	
//	public SetNameDialog(Context context, int themeResId,android.view.View.OnClickListener onClickListener) {
//		super(context, R.style.transparent_dialog);
//		this.context = context;
//		this.onClickListener = onClickListener;
//		setContentView(R.layout.dialog_setname);
//		init();
//	}
	public SetNameDialog(Context context,android.view.View.OnClickListener onClickListener) {
		super(context, R.style.transparent_dialog_chat);
		this.context = context;
		this.onClickListener = onClickListener;
		setContentView(R.layout.dialog_setname);
		init();
	}

	private void init() {
//		username = (EditText) findViewById(R.id.dialog_username);
//		ip = (EditText) findViewById(R.id.dialog_ip);
//		port = (EditText) findViewById(R.id.dialog_port);
		etPhone = (EditText) findViewById(R.id.dialog_phone);
		etCode = (EditText) findViewById(R.id.et_code);
		sure = (Button) findViewById(R.id.btn_dialog_register);
		getcode = (Button) findViewById(R.id.btn_register_getcode);
		sure.setOnClickListener(onClickListener);
		getcode.setOnClickListener(onClickListener);
	}

}
